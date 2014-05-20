package mailclassifier.classification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import mailclassifier.helpers.MailHelpers;
import mailclassifier.mail.MailClientIMAP;
import mailclassifier.models.ClassifiedMessage;
import mailclassifier.models.MailAccount;
import mailclassifier.models.MailMessageDto;
import mailclassifier.models.MessageAttributes;
import mailclassifier.models.MessageData;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import weka.core.Instances;

public class MailClassifierClient {

	Instances instances;
	MailClientIMAP mailClient;

	public MailClientIMAP getMailClient() {
		return mailClient;
	}

	private MailClassifier _mailClassifier;
	Boolean _isClassifierLoaded = false;

	public MailClassifierClient(MailAccount mailAccount, String storagePath,
			String accountName) throws Exception {
		mailClient = new MailClientIMAP(mailAccount.getServerAddress(),
				mailAccount.getUsername(), mailAccount.getPassword());

		setMailClassifier(new MailClassifier(storagePath, accountName));
	}

	public void loadClassifier() throws Exception {
		getMailClassifier().loadOrCreateDataSet();
		_isClassifierLoaded = true;
	}

	public void connectToMailServer() throws MessagingException {
		mailClient.connect();
	}

	public void disconnectFromMailServer() throws MessagingException {
		mailClient.disconnect();
	}

	public ArrayList<ClassifiedMessage> getAndClassifyNewMails(Date fromDate)
			throws Exception {

		ArrayList<ClassifiedMessage> classifiedMesasages = new ArrayList<ClassifiedMessage>();

		MailMessageDto newMessages[] = getNewMailMessageDtos(fromDate);
		if (newMessages.length == 0) {
			return classifiedMesasages;
		}

		for (int i = 0; i < newMessages.length; i++) {
			MailMessageDto message = newMessages[i];
			String classifiedFolder = classifyMessageAndGetFolder(message,
					instances);
			ClassifiedMessage messageWithFolder = new ClassifiedMessage();
			messageWithFolder.setMessage(message);
			messageWithFolder.setFolder(classifiedFolder);
			
			classifiedMesasages.add(messageWithFolder);
		}

		return classifiedMesasages;
	}

	public Message[] getNewMessagesSinceDate(Date fromDate) throws IOException,
			Exception {
		Message newMessages[] = getNewMailMessages(fromDate);
		return newMessages;
	}

	private String classifyMessageAndGetFolder(Message message,
			Instances instances2) throws Exception {
		if (!_isClassifierLoaded) {
			throw new Exception(
					"Classifier is not initialized! Please consider loadClassifier method!");
		}

		String messageData = MailHelpers.mailToText(message);
		String classFolder = getMailClassifier().classifyMessageToFolder(
				messageData);
		return classFolder;
	}
	
	private String classifyMessageAndGetFolder(MailMessageDto message,
			Instances instances2) throws Exception {
		if (!_isClassifierLoaded) {
			throw new Exception(
					"Classifier is not initialized! Please consider loadClassifier method!");
		}

		String messageData = MailHelpers.mailToText(message);
		String classFolder = getMailClassifier().classifyMessageToFolder(
				messageData);
		return classFolder;
	}

	public Message[] getNewMailMessages(Date fromDate) throws IOException,
			Exception {
		return mailClient.getMessagesFromFolderSince("inbox", fromDate);
	}

	public MailMessageDto[] getNewMailMessageDtos(Date fromDate)
			throws IOException, Exception {
		return mailClient.getMessagesDtoFromFolderSince("inbox", fromDate);
	}

	public void downloadAllMailData(Boolean topicFoldersOnly, int foldersCount,
			int maxMessagesInFolder) throws MessagingException {
		// this will word for first folder level
		Folder[] topicFolders = mailClient.getTopicFolders();
		System.out.println("Loading messages from " + topicFolders.length
				+ " topic folders");

		if (foldersCount > topicFolders.length || foldersCount == 0) {

			foldersCount = topicFolders.length;
		}

		for (int i = 0; i < foldersCount; i++) {
			Folder f = topicFolders[i];
			f.open(Folder.READ_ONLY);

			String currentFolder = f.getName();
			System.out.println(currentFolder + " - " + f.getMessageCount());

			Message[] folderMessages = f.getMessages();

			// fetch data
			// FetchProfile fp = new FetchProfile();
			// fp.add(FetchProfile.Item.ENVELOPE);
			// fp.add(FetchProfile.Item.CONTENT_INFO);
			// f.fetch(folderMessages, fp);

			int messagesCount = folderMessages.length;
			if (maxMessagesInFolder < messagesCount && maxMessagesInFolder != 0) {
				messagesCount = maxMessagesInFolder;
			}

			for (int j = 0; j < messagesCount; j++) {
				System.out.print("Msg:" + j + "/" + messagesCount + "...");
				Message msg = folderMessages[j];

				String msgAsText = MailHelpers.mailToText(msg);
				getMailClassifier().trainWithMessage(msgAsText, currentFolder);
				System.out.println("done");
			}
			f.close(true);
		}

	}

	public boolean hasBuiltArffData() {
		try {
			Boolean doesArffExists = getMailClassifier()
					.doesArffExistsForCurrentAccount();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public void saveMailDataToArff() throws Exception {
		getMailClassifier().saveDataSet();
	}

	public MailClassifier getMailClassifier() {
		return _mailClassifier;
	}

	public void setMailClassifier(MailClassifier _mailClassifier) {
		this._mailClassifier = _mailClassifier;
	}

}

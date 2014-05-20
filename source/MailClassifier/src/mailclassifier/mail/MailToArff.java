package mailclassifier.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;

import com.sun.mail.imap.IMAPMessage;

import mailclassifier.classification.MailClassifier;
import mailclassifier.classification.MailClassifierClient;
import mailclassifier.helpers.MailHelpers;
import mailclassifier.models.MailAccount;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;

public class MailToArff {
	private String _serverAddress;
	private String _username;
	private String _password;

	MailClientIMAP _mailClient;
	Instances _mailInstances;
	FastVector _mailAttributes;

	public MailToArff(String serverAddress, String username, String password)
			throws FileNotFoundException, IOException {
		if (serverAddress == null || serverAddress.isEmpty()) {
			throw new IllegalArgumentException(
					"serverAddress must not be empty");
		}
		_serverAddress = serverAddress;

		if (username == null || username.isEmpty()) {
			throw new IllegalArgumentException("username must not be empty");
		}
		_username = username;

		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("password must not be empty");
		}
		_password = password;

		_mailClient = new MailClientIMAP(serverAddress, username, password);

		_mailAttributes = new FastVector(9);
		_mailAttributes
				.addElement(new Attribute("MessageId", (FastVector) null));
		// _mailAttributes.addElement(new Attribute("Date", (FastVector) null));
		_mailAttributes.addElement(new Attribute("Subject", (FastVector) null));
		_mailAttributes.addElement(new Attribute("From", (FastVector) null));
		_mailAttributes.addElement(new Attribute("To", (FastVector) null));
		_mailAttributes.addElement(new Attribute("Cc", (FastVector) null));
		_mailAttributes.addElement(new Attribute("Bcc", (FastVector) null));
		_mailAttributes.addElement(new Attribute("Body", (FastVector) null));
		_mailAttributes.addElement(new Attribute("Folder", (FastVector) null));

		_mailInstances = new Instances("mails", _mailAttributes, 10);
		_mailInstances.setClassIndex(_mailInstances.numAttributes() - 1);
	}

	public void downloadMailData(Boolean topicFoldersOnly, int foldersCount,
			int maxMessagesInFolder) throws MessagingException {
		// this will word for first folder level
		Folder[] topicFolders = _mailClient.getTopicFolders();
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

				try {

					addMailToInstacesSet(_mailInstances, msg);
				} catch (Exception e) {
					System.out.println("Error for msg:["
							+ msg.getMessageNumber() + "] - " + e.toString());
				}
				System.out.println("done");
			}
			f.close(true);
		}

	}

	public void connectToMailServer() throws MessagingException {
		_mailClient.connect();
	}

	public void disconnectFromMailServer() throws MessagingException {
		_mailClient.disconnect();
	}

	private void addMailToInstacesSet(Instances data, Message message)
			throws MessagingException {
		Instance instance = new Instance(9);

		// build instance with the specified attributes
		String messageId = ((IMAPMessage) message).getMessageID();
		instance.setValue(data.attribute("MessageId"), messageId);
		// String sentDateStr = Utils.quote(message.getSentDate().toString());
		// instance.setValue(data.attribute("Date"), sentDateStr);
		String subject = Utils.quote(message.getSubject());
		instance.setValue(data.attribute("Subject"), subject);
		String from = MailHelpers.ToCommaSeparatedString(message.getFrom());
		if (from != null && !from.isEmpty()) {
			instance.setValue(data.attribute("From"), Utils.quote(from));
		}
		String to = MailHelpers.ToCommaSeparatedString(message
				.getRecipients(RecipientType.TO));
		if (to != null && !to.isEmpty()) {
			instance.setValue(data.attribute("To"), Utils.quote(to));
		}
		String cc = MailHelpers.ToCommaSeparatedString(message
				.getRecipients(RecipientType.CC));
		if (cc != null && !cc.isEmpty()) {
			instance.setValue(data.attribute("Cc"), Utils.quote(cc));
		}

		String bcc = MailHelpers.ToCommaSeparatedString(message
				.getRecipients(RecipientType.BCC));
		if (bcc != null && !bcc.isEmpty()) {
			instance.setValue(data.attribute("Bcc"), Utils.quote(bcc));
		}
		instance.setValue(data.attribute("Body"),
				MailHelpers.extractTextFromBodyOnly(message));
		String name = message.getFolder().getName();
		instance.setValue(data.attribute("Folder"), name);

		instance.setDataset(data);

		data.add(instance);
	}

	public void saveDataSet(String filePath) throws Exception {

		// StringToWordVector filter = new StringToWordVector();
		// filter.setInputFormat(_dataSet);
		// Instances dataSet = Filter.useFilter(_dataSet, filter);

		Instances dataSet = _mailInstances;

		// Export instances to arff file
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataSet);

		saver.setFile(new File(filePath));
		saver.writeBatch();
		System.out.println("Instances saved to " + filePath);
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		String serverAddress = "imap.gmail.com";
		String username = "tbmihailov@gmail.com";
		String password = "";
		MailAccount mailAccount = new MailAccount(serverAddress, username,
				password);

		// specify the arff file is created
		String arffFile = args[0];//"D:\\Programming\\Java\\MailClassification\\data\\sample.arff";// storage
		// path
		// is
		// provided

		try {
			// init the mail classifier client
			MailToArff client = new MailToArff(serverAddress, username,
					password);

			Boolean topicFoldersOnly = true;
			int numberOfTopicFolders = 0;// for test purposes - 0 for all
			int maxMessagesInFolder = 1000;// for test purposes - 0 for all

			client.connectToMailServer();
			// download all the mail
			client.downloadMailData(topicFoldersOnly, numberOfTopicFolders,
					maxMessagesInFolder);

			// save data
			client.saveDataSet(arffFile);
			client.disconnectFromMailServer();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

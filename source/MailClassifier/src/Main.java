import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;

import mailclassifier.classification.MailClassifier;
import mailclassifier.classification.MailClassifierClient;
import mailclassifier.models.ClassifiedMessage;
import mailclassifier.models.MailAccount;
import mailclassifier.models.MessageAttributes;

import java.util.*;

import com.sun.mail.imap.*;

import java.io.*;

public class Main {
	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		String serverAddress = "imap.gmail.com";
		String username = "tbmihailov@gmail.com";
		String password = "";
		MailAccount mailAccount = new MailAccount(serverAddress, username,
				password);

		// specify the arff file is created
		String arffStoragePath = "D:\\Programming\\Java\\MailClassification\\data";// storage
																					// path
																					// is
																					// provided
		// because of future
		// multiple account support
		String userProfile = "tbmihailov_gmail";// this is the name of the
												// profile to be saved for
												// current email account

		try {
			// init the mail classifier client
			MailClassifierClient client = new MailClassifierClient(mailAccount,
					arffStoragePath, userProfile);

			// connect to mail server

			client.loadClassifier();
			if (!client.getMailClassifier().doesArffExistsForCurrentAccount()) {
				Boolean topicFoldersOnly = true;
				int numberOfTopicFolders = 0;// for test purposes - 0 for all
				int maxMessagesInFolder = 1000;// for test purposes - 0 for all

				client.connectToMailServer();

				// download all the mail
				client.downloadAllMailData(topicFoldersOnly,
						numberOfTopicFolders, maxMessagesInFolder);

				// save data
				client.saveMailDataToArff();
				client.disconnectFromMailServer();
			}

			MailClassifier classifier = client.getMailClassifier();

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -20);
			Date oneHourBack = cal.getTime();

				
			client.connectToMailServer();
			ArrayList<ClassifiedMessage> classifiedMessages = client.getAndClassifyNewMails(oneHourBack);
			
			for (int i = 0; i < classifiedMessages.size(); i++) {
				System.out.println("["+i+"]"+classifiedMessages.get(i).getMessage().getSubject());
				System.out.println("Dest folder:["+classifiedMessages.get(i).getFolder()+"]");
			}
			//String folder = classifier.classifyMessageToFolder("techcrunch ");
			//System.out.println(folder);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
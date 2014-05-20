package mailclassifier.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import mailclassifier.models.MailMessageDto;

import org.jsoup.Jsoup;

import weka.core.Utils;

import com.sun.mail.imap.IMAPMessage;

public class MailHelpers {
	public static Folder[] getGmailFolders(String username, String password)
			throws MessagingException {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore("imaps");
		store.connect("imap.gmail.com", username, password);
		System.out.println(store);

		Folder[] f = store.getDefaultFolder().list();
		store.close();
		return f;
	}

	public static void getMessagesFromFolders(String username, String password,
			String folderName) throws MessagingException, IOException {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore("imaps");
		store.connect("imap.gmail.com", username, password);
		System.out.println(store);

		Folder folder = store.getFolder(folderName);
		// Open the Folder.
		folder.open(Folder.READ_ONLY);
		Message[] message = folder.getMessages();

		// fetch data
		FetchProfile fp = new FetchProfile();
		fp.add(FetchProfile.Item.ENVELOPE);
		fp.add(FetchProfile.Item.CONTENT_INFO);

		folder.fetch(message, fp);

		// Display message.
		for (int i = 0; i < message.length; i++) {
			System.out.println("------------ Message " + (i + 1)
					+ " ------------");
			System.out.println(MailHelpers.extractTextFromBodyOnly(message[i]));
			System.out.println("------------");
		}
		folder.close(true);
		store.close();

	}

	public static String mailToText(Message message) {
		StringBuilder sb = new StringBuilder();
		try {
			//sb.append("From : ");
			sb.append(message.getFrom());
			sb.append(System.getProperty("line.separator"));
			//sb.append("Subject : ");
			sb.append(message.getSubject());
			sb.append(System.getProperty("line.separator"));
			//sb.append("Body:");
			sb.append(MailHelpers.extractTextFromBodyOnly(message));
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String mailToText(MailMessageDto message) {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("line.separator"));
		sb.append("From : ");
		sb.append(message.getFrom());
		sb.append(System.getProperty("line.separator"));
		sb.append("Subject : ");
		sb.append(message.getSubject());
		sb.append(System.getProperty("line.separator"));
		sb.append("Body:");
		sb.append(message.getBody());
		return sb.toString();
	}

	public static String extractTextFromBodyOnly(Message message) {
		String result = "";
		try {
			if (message instanceof MimeMessage) {

				MimeMessage m = (MimeMessage) message;
				Object contentObject;

				contentObject = m.getContent();

				if (contentObject instanceof Multipart) {
					BodyPart clearTextPart = null;
					BodyPart htmlTextPart = null;
					Multipart content = (Multipart) contentObject;
					int count = content.getCount();
					for (int i = 0; i < count; i++) {
						BodyPart part = content.getBodyPart(i);
						if (part.isMimeType("text/plain")) {
							clearTextPart = part;
							break;
						} else if (part.isMimeType("text/html")) {
							htmlTextPart = part;
						}
					}

					if (clearTextPart != null) {
						result = (String) clearTextPart.getContent();
					} else if (htmlTextPart != null) {
						String html = (String) htmlTextPart.getContent();
						result = Jsoup.parse(html).text();
					}

				} else if (contentObject instanceof String) // a simple text
															// message
				{
					result = Jsoup.parse((String) contentObject).text();
				} else // not a mime message
				{
					// "not a mime part or multipart {0}",message.toString());
					result = null;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static String[] nonTopicFoldersFilter = new String[] { "inbox",
			"trash", "[gmail]", "sent", "sentmail", "drafts", "deleted" };

	/**
	 * @param folders
	 * @return Returns topic folders only - without inbox, drafts, trash, etc.
	 */
	public static Folder[] getTopicFolders(Folder[] folders) {
		List<Folder> topicFolders = new ArrayList<Folder>();
		for (Folder f : folders) {
			String folderPlain = f.getName().toLowerCase().trim();
			if (!Arrays.asList(nonTopicFoldersFilter).contains(folderPlain)) {
				topicFolders.add(f);
			}
		}

		Folder[] topicFoldersArray = topicFolders
				.toArray(new Folder[topicFolders.size()]);
		return topicFoldersArray;
	}

	public static String ToCommaSeparatedString(Address[] from) {
		if (from == null) {
			return "";
		}

		if (from.length == 0) {
			return "";
		} else if (from.length == 1) {
			return from[0].toString();
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < from.length - 1; i++) {
				sb.append(from[i].toString() + ",");
			}
			sb.append(from[from.length].toString());
			return sb.toString();
		}

	}

	public static MailMessageDto messageToMailMessageDto(Message message)
			throws MessagingException {
		MailMessageDto mm = new MailMessageDto();
		mm.setMessageId(((IMAPMessage) message).getMessageID());
		mm.setSubject(message.getSubject());
		mm.setFrom(MailHelpers.ToCommaSeparatedString(message.getFrom()));
		mm.setTo(MailHelpers.ToCommaSeparatedString(message
				.getRecipients(RecipientType.TO)));
		mm.setBody(MailHelpers.extractTextFromBodyOnly(message));

		return mm;

	}

}

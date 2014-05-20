package mailclassifier.mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import mailclassifier.helpers.MailHelpers;
import mailclassifier.models.MailAccount;
import mailclassifier.models.MailMessageDto;
import mailclassifier.models.MessageData;

import com.sun.mail.imap.IMAPFolder;

public class MailClientIMAP {

	private String _serverAddress;
	private String _username;
	private String _password;

	public MailClientIMAP(String serverAddress, String username, String password)
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
	}

	Store _store;

	public void connect() throws MessagingException {
		String protocol = "imaps";
		String serverAddress = _serverAddress;
		String userUsername = _username;
		String userPassword = _password;

		Properties props = new Properties();
		props.setProperty("mail.store.protocol", protocol);
		Session session;
		session = Session.getDefaultInstance(props, null);
		_store = session.getStore(protocol);
		_store.connect(serverAddress, userUsername, userPassword);
	}

	public void disconnect() throws MessagingException {
		if (_store.isConnected()) {
			_store.close();
		}
	}

	public Folder[] getAllFolders() throws MessagingException {
		Folder[] folders = _store.getDefaultFolder().list();
		return folders;
	}

	public Folder[] getTopicFolders() throws MessagingException {
		Folder[] folders = _store.getDefaultFolder().list();
		Folder[] topicFoldersArray = MailHelpers.getTopicFolders(folders);
		return topicFoldersArray;
	}

	public String[] getTopicFoldersNames() throws MessagingException {
		Folder[] folders = getTopicFolders();
		String[] str = new String[folders.length];
		for (int i = 0; i < folders.length; i++) {
			str[i] = folders[i].getName();
		}

		return str;
	}

	@SuppressWarnings("finally")
	public Message[] getMessagesFromFolderSince(String folderName, Date fromDate)
			throws Exception, IOException {
		Message[] messages = null;
		try {
			Store store = _store;
			IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
			folder.open(Folder.READ_ONLY);

			SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT,
					fromDate);
			folder.search(newerThan);
			messages = folder.search(newerThan);

			// fetch data
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.CONTENT_INFO);

			folder.fetch(messages, fp);

			folder.close(false);
		} catch (MessagingException e) {
			System.out.println("Error: " + e);
		} finally {
			return messages;
		}

	}

	@SuppressWarnings("finally")
	public MailMessageDto[] getMessagesDtoFromFolderSince(String folderName,
			Date fromDate) throws Exception, IOException {
		ArrayList<MailMessageDto> mailMessages = new ArrayList<MailMessageDto>();
		try {
			Store store = _store;
			IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
			folder.open(Folder.READ_ONLY);

			SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT,
					fromDate);
			folder.search(newerThan);
			Message[] messages = folder.search(newerThan);

			for (int i = 0; i < messages.length; i++) {
				MailMessageDto mm = MailHelpers
						.messageToMailMessageDto(messages[i]);
				mailMessages.add(mm);
			}
			// fetch data
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.CONTENT_INFO);

			folder.fetch(messages, fp);

			folder.close(false);
		} catch (MessagingException e) {
			System.out.println("Error: " + e);
		} finally {
			MailMessageDto[] mmArray = new MailMessageDto[mailMessages.size()];
			mmArray = mailMessages.toArray(mmArray);
			return mmArray;
		}

	}

	public void moveMessageToFolder(Message message, String classifiedFolder) {

	}

}

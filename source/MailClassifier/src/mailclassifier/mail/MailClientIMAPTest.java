package mailclassifier.mail;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.Folder;
import javax.mail.MessagingException;

import org.junit.Test;

public class MailClientIMAPTest {

	String testServerAddress = "imap.gmail.com";
	String testUsername = "tbmihailov@gmail.com";
	String testPassword = "";

	@Test(expected = IllegalArgumentException.class)
	public void test_Contructor_Validation_ServerAddress()
			throws FileNotFoundException, IOException {
		MailClientIMAP imapClient = null;

		// address validation
		imapClient = new MailClientIMAP("", "", "");

		// username validation
		imapClient = new MailClientIMAP("test", "", "");

		// password validation
		imapClient = new MailClientIMAP("test", "test", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Contructor_Validation_Username()
			throws FileNotFoundException, IOException {
		MailClientIMAP imapClient = null;
		// username validation
		imapClient = new MailClientIMAP("test", "", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Contructor_Validation_Password()
			throws FileNotFoundException, IOException {
		MailClientIMAP imapClient = null;

		// password validation
		imapClient = new MailClientIMAP("test", "test", "");
	}

	@Test
	public void testGetAllFolders() throws FileNotFoundException, IOException,
			MessagingException {
		MailClientIMAP imapClient = new MailClientIMAP(testServerAddress,
				testUsername, testPassword);
		imapClient.connect();
		Folder[] folders = imapClient.getAllFolders();
		assertNotEquals(0, folders.length);

		System.out.println(">> All folders:");
		for (Folder fd : folders)
			System.out.println(">> " + fd.getName());
		imapClient.disconnect();
	}

	@Test
	public void testGetTopicFolders() throws FileNotFoundException,
			IOException, MessagingException {
		MailClientIMAP imapClient = new MailClientIMAP(testServerAddress,
				testUsername, testPassword);
		imapClient.connect();
		Folder[] folders = imapClient.getTopicFolders();
		assertNotEquals(0, folders.length);

		System.out.println(">> Topic folders:");
		for (Folder fd : folders)
			System.out.println(">> " + fd.getName());
		imapClient.disconnect();
	}

	@Test
	public void testGetMessagesFromInboxSince() {
		fail("Not yet implemented");
	}

}

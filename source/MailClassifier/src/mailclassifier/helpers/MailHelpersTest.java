package mailclassifier.helpers;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.mail.Folder;
import javax.mail.MessagingException;

import org.junit.Test;

public class MailHelpersTest {

	String testUser = "tbmihailov@gmail.com";
	String testPass = "";

	// @Test
	public void test() throws MessagingException {
		Folder[] mailFolders = MailHelpers.getGmailFolders(testUser, testPass);
		for (Folder fd : mailFolders)
			System.out.println(">> " + fd.getName());
	}

	@Test
	public void test_print_messages_from_folder() throws MessagingException,
			IOException {
		MailHelpers.getMessagesFromFolders(testUser, testPass, "Travel");
	}

}

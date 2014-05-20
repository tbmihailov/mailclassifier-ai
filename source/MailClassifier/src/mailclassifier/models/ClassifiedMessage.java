package mailclassifier.models;

import javax.mail.Message;

public class ClassifiedMessage {

	private MailMessageDto message;
	private String folder;
	
	public MailMessageDto getMessage() {
		return message;
	}
	public void setMessage(MailMessageDto message) {
		this.message = message;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}

}

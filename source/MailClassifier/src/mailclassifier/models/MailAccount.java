package mailclassifier.models;

public class MailAccount {
	private String mailProtocol;
	private String serverAddress;
	private String username;
	private String password;
	private String name;
	
	public MailAccount(String serverAddress2, String username2, String password2) {
		serverAddress = serverAddress2;
		username = username2;
		password = password2;
	}
	public String getMailProtocol() {
		return mailProtocol;
	}
	public void setMailProtocol(String mailProtocol) {
		this.mailProtocol = mailProtocol;
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

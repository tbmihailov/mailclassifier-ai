package mailclassifier.models;

public class MessageData {
	private StringBuilder textData;
	private String classFolder;

	StringBuilder getTextData() {
		return textData;
	}

	void setTextData(StringBuilder textData) {
		this.textData = textData;
	}

	String getClassFolder() {
		return classFolder;
	}

	void setClassFolder(String classFolder) {
		this.classFolder = classFolder;
	}
}

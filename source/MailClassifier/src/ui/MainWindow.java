package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.GridLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTabbedPane;

import java.awt.Font;

import javax.swing.JProgressBar;

import mailclassifier.classification.MailClassifier;
import mailclassifier.classification.MailClassifierClient;
import mailclassifier.helpers.JTextAreaOutputStream;
import mailclassifier.models.ClassifiedMessage;
import mailclassifier.models.MailAccount;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JList;
import javax.swing.JTextArea;

import java.awt.ComponentOrientation;
import java.awt.ScrollPane;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class MainWindow {

	private JFrame frame;
	private JPasswordField txtPassword;
	private JTextField txtMail;
	private JTextField txtAccountMailServer;
	private JTextField txtAccountName;
	private JTextField txtDataFolder;
	private JTextField txtArffFile;
	private JTable tableMessages;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				File file = new File(txtDataFolder.getText(), txtAccountName
						.getText() + ".arff");
				txtArffFile.setText(file.getAbsolutePath());

			}
		});
		frame.setBounds(100, 100, 685, 579);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 670, 498);
		frame.getContentPane().add(tabbedPane);

		JPanel panelMain = new JPanel();
		panelMain.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				File file = new File(txtDataFolder.getText(), txtAccountName
						.getText() + ".arff");
				txtArffFile.setText(file.getAbsolutePath());
			}
		});

		JPanel panelSettings = new JPanel();
		tabbedPane.addTab("Settings", null, panelSettings, null);
		panelSettings.setLayout(null);

		txtPassword = new JPasswordField();
		txtPassword.setColumns(10);
		txtPassword.setBounds(122, 165, 144, 20);
		panelSettings.add(txtPassword);

		JLabel label = new JLabel("Password");
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setBounds(10, 165, 102, 14);
		panelSettings.add(label);

		JLabel label_1 = new JLabel("E-mail");
		label_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label_1.setBounds(10, 140, 102, 14);
		panelSettings.add(label_1);

		txtMail = new JTextField();
		txtMail.setText("tbmihailov@gmail.com");
		txtMail.setColumns(10);
		txtMail.setBounds(122, 140, 144, 20);
		panelSettings.add(txtMail);

		txtAccountMailServer = new JTextField();
		txtAccountMailServer.setText("imap.gmail.com");
		txtAccountMailServer.setColumns(10);
		txtAccountMailServer.setBounds(122, 115, 144, 20);
		panelSettings.add(txtAccountMailServer);

		JLabel label_2 = new JLabel("Mail server (IMAP)");
		label_2.setHorizontalAlignment(SwingConstants.TRAILING);
		label_2.setBounds(10, 115, 102, 14);
		panelSettings.add(label_2);

		JLabel label_3 = new JLabel("Account name");
		label_3.setHorizontalAlignment(SwingConstants.TRAILING);
		label_3.setBounds(10, 90, 102, 14);
		panelSettings.add(label_3);

		txtAccountName = new JTextField();
		txtAccountName.setText("tbmihailov_gmail");
		txtAccountName.setColumns(10);
		txtAccountName.setBounds(122, 90, 144, 20);
		panelSettings.add(txtAccountName);

		JButton button = new JButton("Save");
		button.setBounds(122, 196, 89, 23);
		panelSettings.add(button);

		JLabel lblDataStore = new JLabel("Data store (folder)");
		lblDataStore.setHorizontalAlignment(SwingConstants.TRAILING);
		lblDataStore.setBounds(10, 29, 102, 14);
		panelSettings.add(lblDataStore);

		txtDataFolder = new JTextField();
		txtDataFolder
				.setText("D:\\programming\\java\\MailClassification\\data");
		txtDataFolder.setColumns(10);
		txtDataFolder.setBounds(122, 29, 356, 20);
		panelSettings.add(txtDataFolder);

		JLabel lblStore = new JLabel("Store");
		lblStore.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblStore.setBounds(21, 4, 46, 14);
		panelSettings.add(lblStore);

		JLabel lblAccount = new JLabel("Account");
		lblAccount.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblAccount.setBounds(21, 67, 91, 14);
		panelSettings.add(lblAccount);
		tabbedPane.addTab("Mail", null, panelMain, null);
		panelMain.setLayout(null);

		JLabel lblArffPath = new JLabel("ARFF path");
		lblArffPath.setBounds(10, 11, 60, 14);
		panelMain.add(lblArffPath);

		txtArffFile = new JTextField();
		txtArffFile.setBounds(67, 8, 374, 20);
		panelMain.add(txtArffFile);
		txtArffFile.setColumns(10);

		JButton btnDownloadAllMail = new JButton(
				"Rebuild mail data (time consuming)");
		btnDownloadAllMail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// Custom button text
				Object[] options = { "Yes, please", "No, thanks", "Nooo" };
				int n = JOptionPane
						.showOptionDialog(
								frame,
								"Do you really want to download all mail again ",
								"Really? It's time consuming!!",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[2]);

				System.out.println("test");

				if (n == 0) {

					String serverAddress = txtAccountMailServer.getText();
					String username = txtMail.getText();
					String password = new String(txtPassword.getPassword());
					MailAccount mailAccount = new MailAccount(serverAddress,
							username, password);

					// specify the arff file is created
					String arffStoragePath = txtDataFolder.getText();// storage
																		// path
																		// is
																		// provided
					// because of future
					// multiple account support
					String userProfile = txtAccountName.getText();// this is the
																	// name of
																	// the
					// profile to be saved for
					// current email account

					try {
						// init the mail classifier client
						MailClassifierClient client = new MailClassifierClient(
								mailAccount, arffStoragePath, userProfile);

						// connect to mail server

						client.loadClassifier();

						Boolean topicFoldersOnly = true;
						int numberOfTopicFolders = 0;// for test purposes -
														// 0 for all
						int maxMessagesInFolder = 1000;// for test purposes
														// - 0 for all

						client.connectToMailServer();

						// download all the mail
						client.downloadAllMailData(topicFoldersOnly,
								numberOfTopicFolders, maxMessagesInFolder);

						// save data
						client.saveMailDataToArff();
						JOptionPane
								.showMessageDialog(
										frame,
										"Mail has been downloaded and added to weka ARFF file",
										"", JOptionPane.INFORMATION_MESSAGE);
						client.disconnectFromMailServer();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		btnDownloadAllMail.setBounds(451, 7, 204, 23);
		panelMain.add(btnDownloadAllMail);

		JButton btnGetAndClassify = new JButton("Get & Classify new mail");
		btnGetAndClassify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = new File(txtDataFolder.getText(), txtAccountName
						.getText() + ".arff");
				if (!file.exists()) {
					// custom title, warning icon
					JOptionPane
							.showMessageDialog(
									frame,
									"The file with messages does not exists. Please build mail file first!",
									"No mail instances",
									JOptionPane.WARNING_MESSAGE);
					return;
				}

				// download messages
				String serverAddress = txtAccountMailServer.getText();
				String username = txtMail.getText();
				String password = new String(txtPassword.getPassword());
				MailAccount mailAccount = new MailAccount(serverAddress,
						username, password);

				// specify the arff file is created
				String arffStoragePath = txtDataFolder.getText();// storage
																	// path
																	// is
																	// provided
				// because of future
				// multiple account support
				String userProfile = txtAccountName.getText();// this is the
																// name of
																// the
				// profile to be saved for
				// current email account

				try {
					// init the mail classifier client
					MailClassifierClient client = new MailClassifierClient(
							mailAccount, arffStoragePath, userProfile);

					// connect to mail server

					client.loadClassifier();
					if (!client.getMailClassifier()
							.doesArffExistsForCurrentAccount()) {
						JOptionPane
								.showMessageDialog(
										frame,
										"The file with messages does not exists. Please build mail file first!",
										"No mail instances",
										JOptionPane.WARNING_MESSAGE);
						return;
					}

					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.HOUR, -32);
					Date oneHourBack = cal.getTime();

					client.connectToMailServer();
					ArrayList<ClassifiedMessage> classifiedMessages = client
							.getAndClassifyNewMails(oneHourBack);

					int msgsSize = classifiedMessages.size();
					Object[][] data = new Object[msgsSize][3];
					tableMessages.setModel(new DefaultTableModel(data,
							new String[] { "MessageId", "Subject", "Folder" }) {
						/**
							 * 
							 */
						private static final long serialVersionUID = 1L;
						Class[] columnTypes = new Class[] { String.class,
								String.class, String.class };

						public Class getColumnClass(int columnIndex) {
							return columnTypes[columnIndex];
						}
					});

					// set topic folders in combobox column
					String[] topicFolderNames = client.getMailClient()
							.getTopicFoldersNames();
					TableColumn sportColumn = tableMessages.getColumnModel()
							.getColumn(2);
					JComboBox comboBox = new JComboBox();
					for (int i = 0; i < topicFolderNames.length; i++) {
						comboBox.addItem(topicFolderNames[i]);
					}
					sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

					for (int i = 0; i < classifiedMessages.size(); i++) {

						tableMessages.getModel().setValueAt(
								classifiedMessages.get(i).getMessage()
										.getMessageId(), i, 0);
						tableMessages.getModel().setValueAt(
								classifiedMessages.get(i).getMessage()
										.getSubject(), i, 1);
						tableMessages.getModel().setValueAt(
								classifiedMessages.get(i).getFolder(), i, 2);

						System.out.println("["
								+ i
								+ "]"
								+ classifiedMessages.get(i).getMessage()
										.getSubject() + "- ["
								+ classifiedMessages.get(i).getFolder() + "]");
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		btnGetAndClassify.setBounds(10, 42, 204, 23);
		panelMain.add(btnGetAndClassify);

		JButton btnTrainWithNew = new JButton(
				"Train with new messages & move to folders");
		btnTrainWithNew
				.setActionCommand("Train with new messages + move messages to folders");
		btnTrainWithNew.setBounds(10, 436, 303, 23);
		panelMain.add(btnTrainWithNew);

		JLabel lblClassifiedmails = new JLabel("Classified messages:");
		lblClassifiedmails.setBounds(10, 76, 125, 14);
		panelMain.add(lblClassifiedmails);

		tableMessages = new JTable();
		tableMessages.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "MessageId", "Subject", "Folder" }) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Class[] columnTypes = new Class[] { String.class, String.class,
					String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableMessages.setBounds(10, 102, 645, 323);
		panelMain.add(tableMessages);

		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				Object[][] data = new Object[3][3];
				for (int i = 0; i < data.length; i++) {
					data[i][0] = "m123";
					data[i][1] = "Testov subjecttt";
					data[i][2] = "Folder " + i + 1;
				}
				tableMessages.setModel(new DefaultTableModel(data,
						new String[] { "MessageId", "Subject", "Folder" }) {
					/**
						 * 
						 */
					private static final long serialVersionUID = 1L;
					Class[] columnTypes = new Class[] { String.class,
							String.class, String.class };

					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});

				TableColumn sportColumn = tableMessages.getColumnModel()
						.getColumn(2);
				JComboBox comboBox = new JComboBox();
				String[] topicFolderNames = new String[] { "Folder 1",
						"Folder 2", "Folder 3" };
				for (int i = 0; i < topicFolderNames.length; i++) {
					comboBox.addItem(topicFolderNames[i]);
				}
				sportColumn.setCellEditor(new DefaultCellEditor(comboBox));
			}
		});
		btnNewButton.setBounds(224, 42, 89, 23);
		panelMain.add(btnNewButton);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(0, 509, 659, 20);
		frame.getContentPane().add(progressBar);

		// JTextAreaOutputStream out = new JTextAreaOutputStream (textAreaLog);
		// System.setOut (new PrintStream (out));
	}
}

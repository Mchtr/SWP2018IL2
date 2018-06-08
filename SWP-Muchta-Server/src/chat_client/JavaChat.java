import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class JavaChat {

	private JFrame frame;
	private JTextArea textArea_ChatArea;
	private JTextArea textArea_UserOnlineArea;
	private JTextField textField_EingabeChat;
	private JTextField textField_adresse;
	private JTextField textField_Port;
	private JTextField textField_Username;
	private JTextField textField_Password;
	private JScrollPane scrollPaneGod;
	protected String fileName;
	

	String username, address = "localhost";
	ArrayList<String> users = new ArrayList();
	int port = 2222;
	Boolean isConnected = false;

	Socket sock;
	BufferedReader reader;
	PrintWriter writer;

	// --------------------------//

	public void ListenThread() {
		Thread IncomingReader = new Thread(new IncomingReader());
		IncomingReader.start();
	}

	// --------------------------//

	public void addUser(String data) {
		users.add(data);
	}

	// --------------------------//

	public void removeUser(String data) {
		textArea_ChatArea.append(data + " ist nun offline.\n");
	}

	// --------------------------//

	@SuppressWarnings("unused")
	public void writeUsers() {
		String[] temporaereList = new String[(users.size())];
		users.toArray(temporaereList);
		for (String token : temporaereList) {
			// users.append(token + "\n");
		}
	}

	// --------------------------//

	public void sendDisconnect() {
		try {
			writer.println(username + ": :Disconnect");
			writer.flush();
		} catch (Exception e) {
			textArea_ChatArea.append("Disconnect-Message wurde nicht gesendet.\n");
		}
	}

	public void Disconnect() {
		try {
			textArea_ChatArea.append("Disconnected.\n");
			sock.close();
		} catch (Exception ex) {
			textArea_ChatArea.append("Disconnect nicht erfolgreich. \n");
		}
		isConnected = false;
		textField_Username.setEditable(true);

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaChat window = new JavaChat();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public class IncomingReader implements Runnable {
		@Override
		public void run() {
			String[] data;
			String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";

			try {
				while ((stream = reader.readLine()) != null) {
					data = stream.split(":");

					if (data[2].equals(chat)) {
						textArea_ChatArea.append(data[0] + ": " + data[1] + "\n");
						textArea_ChatArea.setCaretPosition(textArea_ChatArea.getDocument().getLength());
					} else if (data[2].equals(connect)) {
						textArea_ChatArea.removeAll();
						addUser(data[0]);
					} else if (data[2].equals(disconnect)) {
						removeUser(data[0]);
					} else if (data[2].equals(done)) {
						// users.setText("");
						writeUsers();
						users.clear();
					}
				}
			} catch (Exception ex) {
			}
		}
	}

	private void connectingAction(java.awt.event.ActionEvent evt) {
		if (isConnected == false) {
			username = textField_Username.getText();
			textField_Username.setEditable(false);

			try {
				sock = new Socket(address, port);
				InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(streamreader);
				writer = new PrintWriter(sock.getOutputStream());
				writer.println(username + ":has connected.:Connect");
				writer.flush();
				isConnected = true;
			} catch (Exception ex) {
				textArea_ChatArea.append("Cannot Connect! Try Again. \n");
				textField_Username.setEditable(true);
			}

			ListenThread();

		} else if (isConnected == true) {
			textArea_ChatArea.append("You are already connected. \n");
		}
	}

	// __________________________________________________________________________

	private void disconnectingAction(java.awt.event.ActionEvent evt) {

		try {
			writer.println(username + ":has disconnected.:Disconnect");
			writer.flush();
		} catch (Exception e) {
			textArea_ChatArea.append("Disconnect-Message wurde nicht gesendet.\n");
		}

		try {
			textArea_ChatArea.append("Disconnected.\n");
			sock.close();
		} catch (Exception ex) {
			textArea_ChatArea.append("Disconnect nicht erfolgreich. \n");
		}
		isConnected = false;
		textField_Username.setEditable(true);
	}

	// ____________________________________________________________________________

	private void anonymousLogin(java.awt.event.ActionEvent evt) {
		textField_Username.setText("");
		if (isConnected == false) {
			String anon = "anon";
			Random rndmNummerGenerator = new Random();
			int i = rndmNummerGenerator.nextInt(999) + 1;
			String is = String.valueOf(i);
			anon = anon.concat(is);
			username = anon;

			textField_Username.setText(anon);
			textField_Username.setEditable(false);

			try {
				sock = new Socket(address, port);
				InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(streamreader);
				writer = new PrintWriter(sock.getOutputStream());
				writer.println(anon + ":has connected.:Connect");
				writer.flush();
				isConnected = true;
			} catch (Exception ex) {
				textArea_ChatArea.append("Can't Connect! Try Again. \n");
				textField_Username.setEditable(true);
			}

			ListenThread();

		} else if (isConnected == true) {
			textArea_ChatArea.append("You are already connected. \n");
		}
	}
	// ______________________________________________________________________________

	private void exportChat() {
		FileDialog dialog = new FileDialog(frame, "Save Chat", FileDialog.SAVE);
		dialog.setVisible(true);

		this.fileName = dialog.getDirectory() + dialog.getFile();
		if (this.fileName == null)
			return;

		try {
			File ausgabedatei = new File(this.fileName);
			FileWriter ausgabe = new FileWriter(ausgabedatei);
			// aktuellen Text aus dem Modell der TextArea ermitteln und speichern
			ausgabe.write(textArea_ChatArea.getText());
			ausgabe.close();
		} catch (IOException e) {
			System.out.println("ERROR - " + this.fileName);
			this.fileName = null;
		}
	}

	// ------------------------------------------------------------------------------

	private void adressChange(java.awt.event.ActionEvent evt) {

	}

	private void portChange(java.awt.event.ActionEvent evt) {

	}

	private void usernameChange(java.awt.event.ActionEvent evt) {
	}
	// _________________________________________________________________

	private void sendAction(java.awt.event.ActionEvent evt) {
		String nothing = "";
		if ((textField_EingabeChat.getText()).equals(nothing)) {
			textField_EingabeChat.setText("");
			textField_EingabeChat.requestFocus();
		} else {
			try {
				writer.println(username + ":" + textField_EingabeChat.getText() + ":" + "Chat");
				writer.flush();
			} catch (Exception ex) {
				textArea_ChatArea.append("Message was not sent. \n");
			}
			textField_EingabeChat.setText("");
			textField_EingabeChat.requestFocus();
		}

		textField_EingabeChat.setText("");
		textField_EingabeChat.requestFocus();
	}

	// __________________________________________________________________
	/**
	 * Create the application.
	 */
	public JavaChat() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Chat - V0.3_ALPHA");
		frame.setBounds(100, 100, 793, 576);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 110, 120, 61, 120, 36, 35, 2, 84, 18, 125, 0 };
		gridBagLayout.rowHeights = new int[] { 30, 28, 22, 0, 337, 0, 52, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblAdressLabel = new JLabel("Adresse:");
		GridBagConstraints gbc_lblAdressLabel = new GridBagConstraints();
		gbc_lblAdressLabel.fill = GridBagConstraints.BOTH;
		gbc_lblAdressLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblAdressLabel.gridx = 0;
		gbc_lblAdressLabel.gridy = 0;
		frame.getContentPane().add(lblAdressLabel, gbc_lblAdressLabel);

		textField_adresse = new JTextField();
		GridBagConstraints gbc_textField_adresse = new GridBagConstraints();
		gbc_textField_adresse.fill = GridBagConstraints.BOTH;
		gbc_textField_adresse.insets = new Insets(0, 0, 5, 5);
		gbc_textField_adresse.gridx = 1;
		gbc_textField_adresse.gridy = 0;
		frame.getContentPane().add(textField_adresse, gbc_textField_adresse);
		textField_adresse.setColumns(10);
		textField_adresse.setText("localhost");
		textField_adresse.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				adressChange(evt);
			}
		});

		JLabel lblPort = new JLabel("Port:");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.fill = GridBagConstraints.BOTH;
		gbc_lblPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblPort.gridx = 2;
		gbc_lblPort.gridy = 0;
		frame.getContentPane().add(lblPort, gbc_lblPort);

		textField_Port = new JTextField();
		GridBagConstraints gbc_textField_Port = new GridBagConstraints();
		gbc_textField_Port.fill = GridBagConstraints.BOTH;
		gbc_textField_Port.insets = new Insets(0, 0, 5, 5);
		gbc_textField_Port.gridx = 3;
		gbc_textField_Port.gridy = 0;
		frame.getContentPane().add(textField_Port, gbc_textField_Port);
		textField_Port.setColumns(10);
		textField_Port.setText("2222");
		textField_Port.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				portChange(evt);
			}
		});

		JButton btnAnonymousLogin = new JButton("Anonymous Login");
		GridBagConstraints gbc_btnAnonymousLogin = new GridBagConstraints();
		gbc_btnAnonymousLogin.fill = GridBagConstraints.BOTH;
		gbc_btnAnonymousLogin.insets = new Insets(0, 0, 5, 0);
		gbc_btnAnonymousLogin.gridwidth = 5;
		gbc_btnAnonymousLogin.gridx = 5;
		gbc_btnAnonymousLogin.gridy = 0;
		frame.getContentPane().add(btnAnonymousLogin, gbc_btnAnonymousLogin);

		try {
			Image imgAnonButton = ImageIO.read(getClass().getResource("anonymoususer.png"));
			btnAnonymousLogin.setIcon(new ImageIcon(imgAnonButton));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		btnAnonymousLogin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				anonymousLogin(evt);
			}
		});

		JLabel lblUsername = new JLabel("Username:");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.fill = GridBagConstraints.BOTH;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 1;
		frame.getContentPane().add(lblUsername, gbc_lblUsername);

		textField_Username = new JTextField();
		GridBagConstraints gbc_textField_Username = new GridBagConstraints();
		gbc_textField_Username.fill = GridBagConstraints.BOTH;
		gbc_textField_Username.insets = new Insets(0, 0, 5, 5);
		gbc_textField_Username.gridx = 1;
		gbc_textField_Username.gridy = 1;
		frame.getContentPane().add(textField_Username, gbc_textField_Username);
		textField_Username.setColumns(10);
		textField_Username.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				usernameChange(evt);
				;
			}
		});

		JLabel lblPasswort = new JLabel("Passwort:");
		GridBagConstraints gbc_lblPasswort = new GridBagConstraints();
		gbc_lblPasswort.fill = GridBagConstraints.BOTH;
		gbc_lblPasswort.insets = new Insets(0, 0, 5, 5);
		gbc_lblPasswort.gridx = 2;
		gbc_lblPasswort.gridy = 1;
		frame.getContentPane().add(lblPasswort, gbc_lblPasswort);

		textField_Password = new JTextField();
		GridBagConstraints gbc_textField_Password = new GridBagConstraints();
		gbc_textField_Password.fill = GridBagConstraints.BOTH;
		gbc_textField_Password.insets = new Insets(0, 0, 5, 5);
		gbc_textField_Password.gridx = 3;
		gbc_textField_Password.gridy = 1;
		frame.getContentPane().add(textField_Password, gbc_textField_Password);
		textField_Password.setColumns(10);

		JButton btnConnect = new JButton("Connect");
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.fill = GridBagConstraints.BOTH;
		gbc_btnConnect.insets = new Insets(0, 0, 5, 5);
		gbc_btnConnect.gridwidth = 3;
		gbc_btnConnect.gridx = 5;
		gbc_btnConnect.gridy = 1;
		frame.getContentPane().add(btnConnect, gbc_btnConnect);
		try {
			Image imgBtnCnnct = ImageIO.read(getClass().getResource("ccconnect.png"));
			btnConnect.setIcon(new ImageIcon(imgBtnCnnct));
		} catch (Exception ex) {
			System.out.println(ex);
		}

		btnConnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				connectingAction(evt);
			}
		});

		JButton btnDisconnect = new JButton("Disconnect");
		GridBagConstraints gbc_btnDisconnect = new GridBagConstraints();
		gbc_btnDisconnect.fill = GridBagConstraints.BOTH;
		gbc_btnDisconnect.insets = new Insets(0, 0, 5, 0);
		gbc_btnDisconnect.gridwidth = 2;
		gbc_btnDisconnect.gridx = 8;
		gbc_btnDisconnect.gridy = 1;
		frame.getContentPane().add(btnDisconnect, gbc_btnDisconnect);

		try {
			Image imgBtnDiscnnct = ImageIO.read(getClass().getResource("disconnect-icon.png"));
			btnDisconnect.setIcon(new ImageIcon(imgBtnDiscnnct));
		} catch (Exception ex) {
			System.out.println(ex);
		}

		btnDisconnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				//sendDisconnect();
				disconnectingAction(evt);
			}
		});

		JLabel lblChat = new JLabel("Chat");
		lblChat.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_lblChat = new GridBagConstraints();
		gbc_lblChat.fill = GridBagConstraints.BOTH;
		gbc_lblChat.insets = new Insets(0, 0, 5, 5);
		gbc_lblChat.gridwidth = 3;
		gbc_lblChat.gridx = 3;
		gbc_lblChat.gridy = 2;
		frame.getContentPane().add(lblChat, gbc_lblChat);

		JLabel lblUserOnline = new JLabel("User Online");
		lblUserOnline.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_lblUserOnline = new GridBagConstraints();
		gbc_lblUserOnline.fill = GridBagConstraints.BOTH;
		gbc_lblUserOnline.insets = new Insets(0, 0, 5, 0);
		gbc_lblUserOnline.gridwidth = 3;
		gbc_lblUserOnline.gridx = 7;
		gbc_lblUserOnline.gridy = 2;
		frame.getContentPane().add(lblUserOnline, gbc_lblUserOnline);

		textArea_ChatArea = new JTextArea();
		textArea_ChatArea.setBackground(SystemColor.control);
		textArea_ChatArea.setEditable(false);
		GridBagConstraints gbc_textArea_ChatArea = new GridBagConstraints();
		gbc_textArea_ChatArea.gridheight = 2;
		gbc_textArea_ChatArea.fill = GridBagConstraints.BOTH;
		gbc_textArea_ChatArea.insets = new Insets(0, 0, 5, 5);
		gbc_textArea_ChatArea.gridwidth = 6;
		gbc_textArea_ChatArea.gridx = 0;
		gbc_textArea_ChatArea.gridy = 4;
		frame.getContentPane().add(textArea_ChatArea, gbc_textArea_ChatArea);
		textArea_ChatArea.setColumns(10);

		JScrollBar scrollBar = new JScrollBar();
		GridBagConstraints gbc_scrollBar = new GridBagConstraints();
		gbc_scrollBar.anchor = GridBagConstraints.EAST;
		gbc_scrollBar.fill = GridBagConstraints.VERTICAL;
		gbc_scrollBar.insets = new Insets(0, 0, 5, 5);
		gbc_scrollBar.gridx = 5;
		gbc_scrollBar.gridy = 4;
		frame.getContentPane().add(scrollBar, gbc_scrollBar);

		textArea_UserOnlineArea = new JTextArea();
		textArea_UserOnlineArea.setBackground(SystemColor.control);
		textArea_UserOnlineArea.setEditable(false);
		GridBagConstraints gbc_textArea_UserOnlineArea = new GridBagConstraints();
		gbc_textArea_UserOnlineArea.fill = GridBagConstraints.BOTH;
		gbc_textArea_UserOnlineArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea_UserOnlineArea.gridwidth = 3;
		gbc_textArea_UserOnlineArea.gridx = 7;
		gbc_textArea_UserOnlineArea.gridy = 4;
		frame.getContentPane().add(textArea_UserOnlineArea, gbc_textArea_UserOnlineArea);
		textArea_UserOnlineArea.setColumns(10);

		JButton btnExportChat = new JButton("Export Chat");
		try {
			Image imgExportButton = ImageIO.read(getClass().getResource("Data-Export-icon.png"));
			btnExportChat.setIcon(new ImageIcon(imgExportButton));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		btnExportChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exportChat();
			}
		});
		GridBagConstraints gbc_btnExportChat = new GridBagConstraints();
		gbc_btnExportChat.gridwidth = 3;
		gbc_btnExportChat.fill = GridBagConstraints.BOTH;
		gbc_btnExportChat.insets = new Insets(0, 0, 5, 0);
		gbc_btnExportChat.gridx = 7;
		gbc_btnExportChat.gridy = 5;
		frame.getContentPane().add(btnExportChat, gbc_btnExportChat);

		textField_EingabeChat = new JTextField();
		textField_EingabeChat.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_textField_EingabeChat = new GridBagConstraints();
		gbc_textField_EingabeChat.fill = GridBagConstraints.BOTH;
		gbc_textField_EingabeChat.insets = new Insets(0, 0, 0, 5);
		gbc_textField_EingabeChat.gridwidth = 7;
		gbc_textField_EingabeChat.gridx = 0;
		gbc_textField_EingabeChat.gridy = 6;
		frame.getContentPane().add(textField_EingabeChat, gbc_textField_EingabeChat);
		textField_EingabeChat.setColumns(10);

		JButton btnSendButton = new JButton("Send");
		GridBagConstraints gbc_btnSendButton = new GridBagConstraints();
		gbc_btnSendButton.fill = GridBagConstraints.BOTH;
		gbc_btnSendButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnSendButton.gridwidth = 3;
		gbc_btnSendButton.gridx = 6;
		gbc_btnSendButton.gridy = 6;
		frame.getContentPane().add(btnSendButton, gbc_btnSendButton);
		try {
			Image imgbtnSendButton = ImageIO.read(getClass().getResource("message-bubble-send-icon.png"));
			btnSendButton.setIcon(new ImageIcon(imgbtnSendButton));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		btnSendButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sendAction(evt);
			}
		});

		JButton btnSendFileButton = new JButton("Send File");
		GridBagConstraints gbc_btnSendFileButton = new GridBagConstraints();
		gbc_btnSendFileButton.fill = GridBagConstraints.BOTH;
		gbc_btnSendFileButton.gridx = 9;
		gbc_btnSendFileButton.gridy = 6;
		frame.getContentPane().add(btnSendFileButton, gbc_btnSendFileButton);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnStart = new JMenu("Start");
		mnStart.setFont(new Font("Tahoma", Font.BOLD, 16));
		menuBar.add(mnStart);

		JMenuItem mntmChatExportiren = new JMenuItem("Chat Exportieren");
		mnStart.add(mntmChatExportiren);

		JMenu mnEinstellungen = new JMenu("Einstellungen");
		mnEinstellungen.setFont(new Font("Tahoma", Font.BOLD, 16));
		menuBar.add(mnEinstellungen);

	}
}

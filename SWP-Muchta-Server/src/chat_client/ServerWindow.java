import java.awt.EventQueue;
import java.awt.Scrollbar;
import java.awt.SystemColor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollBar;

public class ServerWindow {
	private JButton btnStartServer;
	private JFrame frmServerframeServerstatusalpha;
	private JButton btnClearChat;
	private JButton btnEndServer;
	private JTextArea textArea_ServerChat;
	private JButton btnChangePort;
	private JButton btnListUser;
	private JButton btnBanUser;
	private JScrollPane scroll;
 
	
	
	ArrayList clientOutputStreams;
	ArrayList<String> users;
	private JScrollBar scrollBar;

	public class ClientHandler implements Runnable {
		
		
		
		
		BufferedReader reader;
		Socket sock;
		PrintWriter client;

		public ClientHandler(Socket clientSocket, PrintWriter user) {
			client = user;
			try {
				sock = clientSocket;
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
			} catch (Exception ex) {
				textArea_ServerChat.append("Unexpected error... \n");
			}

		}

		@Override
		public void run() {
			String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat";
			String[] data;

			try {
				while ((message = reader.readLine()) != null) {
					textArea_ServerChat.append("Received: " + message + "\n");
					data = message.trim().split(":");
//
//					for (String token : data) {
//						textArea_ServerChat.append(token + "\n");
//					}

					if (data[2].equals(connect)) {
						serverMessage((data[0] + ":" + data[1] + ":" + chat));
						userAdd(data[0]);
					} else if (data[2].equals(disconnect)) {
						serverMessage((data[0] + ":hat den Chat verlassen. " + ": " + chat));
						userRemove(data[0]);
					} else if (data[2].equals(chat)) {
						serverMessage(message);
					} else {
						textArea_ServerChat.append("No Conditions were met. \n");
					}
				}
			} catch (Exception ex) {
				textArea_ServerChat.append("Connection lost. \n");
				ex.printStackTrace();
				clientOutputStreams.remove(client);
			}
		}
	}
	public class ServerStart implements Runnable {
		@Override
		public void run() {
			clientOutputStreams = new ArrayList();
			users = new ArrayList();

			try {
				ServerSocket serverSock = new ServerSocket(2222);

				while (true) {
					Socket clientSock = serverSock.accept();
					PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
					clientOutputStreams.add(writer);

					Thread listener = new Thread(new ClientHandler(clientSock, writer));
					listener.start();
					textArea_ServerChat.append("User hat sich verbunden! \n");
				}
			} catch (Exception ex) {
				textArea_ServerChat.append("Fehler beim Verbindungsaufbau. \n");
			}
		}
	}


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerWindow window = new ServerWindow();
					window.frmServerframeServerstatusalpha.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServerframeServerstatusalpha = new JFrame();
		frmServerframeServerstatusalpha.setTitle("ServerFrame - ServerStatus_ALPHA");
		frmServerframeServerstatusalpha.setBounds(100, 100, 638, 521);
		frmServerframeServerstatusalpha.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServerframeServerstatusalpha.getContentPane().setLayout(null);
		
		btnStartServer = new JButton("Start: Server");
		btnStartServer.setBounds(12, 13, 131, 35);
		frmServerframeServerstatusalpha.getContentPane().add(btnStartServer);
		btnStartServer.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startingServer(evt);
			}
		});
		
		btnClearChat = new JButton("Chat loeschen");
		btnClearChat.setBounds(12, 61, 131, 36);
		frmServerframeServerstatusalpha.getContentPane().add(btnClearChat);
		btnClearChat.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_clearActionPerformed(evt);
			}
		});
		
		btnEndServer = new JButton("End: Server");
		btnEndServer.setBounds(157, 13, 131, 35);
		frmServerframeServerstatusalpha.getContentPane().add(btnEndServer);
		btnEndServer.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				endServer(evt);
			}
		});
		
		btnListUser = new JButton("Auflistung: User");
		btnListUser.setBounds(157, 61, 131, 35);
		frmServerframeServerstatusalpha.getContentPane().add(btnListUser);
		btnListUser.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_usersActionPerformed(evt);
			}
		});
		
		btnBanUser = new JButton("User Bannen");
		btnBanUser.setBounds(300, 13, 131, 35);
		frmServerframeServerstatusalpha.getContentPane().add(btnBanUser);
		
		btnChangePort = new JButton("Port aendern");
		btnChangePort.setBounds(300, 61, 131, 36);
		frmServerframeServerstatusalpha.getContentPane().add(btnChangePort);
		
		textArea_ServerChat = new JTextArea();
		textArea_ServerChat.setBackground(SystemColor.inactiveCaptionBorder);
		textArea_ServerChat.setBounds(12, 110, 576, 351);
		frmServerframeServerstatusalpha.getContentPane().add(textArea_ServerChat);
	}
	//_______________________________________________________________
	private void endServer(java.awt.event.ActionEvent evt) {
		try {
			Thread.sleep(1000); 
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		serverMessage("Server:is stopping and all users will be disconnected.\n:Chat");
		textArea_ServerChat.append("Server stopping... \n");

		textArea_ServerChat.setText("");
	}
	//___________________________________________________________
	private void startingServer(java.awt.event.ActionEvent evt) {
		Thread starter = new Thread(new ServerStart());
		starter.start();

		textArea_ServerChat.append("Server started...\n");
	}
	
	//_________________________________________________________________________
	private void b_usersActionPerformed(java.awt.event.ActionEvent evt) {
		textArea_ServerChat.append("\n Online users : \n");
		for (String current_user : users) {
			textArea_ServerChat.append(current_user);
			textArea_ServerChat.append("\n");
		}

	}
	//_______________________________________________________
	private void b_clearActionPerformed(java.awt.event.ActionEvent evt) {
		textArea_ServerChat.setText("");
	}
//______________________________________________________
	public void userAdd(String data) {
		String message, add = ": :Connect",  name = data; //done = "Server: :Done",
		//textArea_ServerChat.append("Before " + name + " added. \n");
		users.add(name);
	//	textArea_ServerChat.append("After " + name + " added. \n");
		String[] tempList = new String[(users.size())];
		users.toArray(tempList);

//		for (String token : tempList) {
//			message = (token + add);
//			serverMessage(message);
//		}
//		serverMessage(done);
	}

	public void userRemove(String data) {
		String message, add = ": :Connect", name = data; //done = "Server: :Done", 
		users.remove(name);
		String[] tempList = new String[(users.size())];
		users.toArray(tempList);

//		for (String token : tempList) {
//			message = (token + add);
//			serverMessage(message);
//		}
//		serverMessage(done);
	}

	public void serverMessage(String message) {
		Iterator it = clientOutputStreams.iterator();

		while (it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				textArea_ServerChat.append("Servermitteilung: " + message + "\n");
				writer.flush();
				textArea_ServerChat.setCaretPosition(textArea_ServerChat.getDocument().getLength());

			} catch (Exception ex) {
				textArea_ServerChat.append("Error telling everyone. \n");
			}
		}
	}
}

package client;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import server.ChatServerInterface;



public class ChatClient  extends UnicastRemoteObject implements ChatClientInterface {
	/**
	 * 
	 */
//	private static final long serialVersionUID = 7468891722773409712L;
	ClientRMIGUI chatGUI;
	private String serviceName = "GroupChatService";
	private String clientServiceName;
	private String name;
	protected ChatServerInterface serverIF;
	protected boolean connectionProblem = false;

	
	// Constructor
	public ChatClient(ClientRMIGUI aChatGUI, String userName) throws RemoteException {
		super();
		this.chatGUI = aChatGUI;
		this.name = userName;
		this.clientServiceName = "ClientListenService_" + userName;
	}

	
	// Start client service
	// Runs when user clicks Connect
	public int startClient(String hostName) throws RemoteException {		
		String[] details = {name, hostName, clientServiceName};	
		int value = 0;

		try {
			Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
			serverIF = ( ChatServerInterface )Naming.lookup("rmi://" + hostName + "/" + serviceName);	
		} 
		catch (ConnectException  e) {
			JOptionPane.showMessageDialog(
					chatGUI.frame, "The server seems to be unavailable\nPlease try later",
					"Connection problem", JOptionPane.ERROR_MESSAGE);
			connectionProblem = true;
			e.printStackTrace();
		}
		catch(NotBoundException | MalformedURLException me){
			connectionProblem = true;
			me.printStackTrace();
		}
		if(!connectionProblem){
			value = registerWithServer(details);
		}	
		System.out.println("Client Listen RMI Server is running...\n");
		return value;
	}



	// Send username , host name and RMI name to server to request joining chat
	// Also check if username is already online
	public int registerWithServer(String[] details) {	
		int value = 0;
		try{
			serverIF.IdentityPass(this.ref);//now redundant ??
			value = serverIF.registerListener(details);	
			System.out.println("THE VALUE IS" + value);
//			if (value == 1){
//			JOptionPane.showMessageDialog(
//					chatGUI.frame, "USER EXISTS ALREADY",
//					"Connection problem", JOptionPane.ERROR_MESSAGE);
//			}
		}
			
		catch(Exception e){
			e.printStackTrace();
		}
		return value;

	}

	// Method used to receive messages from server and display to client interface
	@Override
	public void messageFromServer(String message) throws RemoteException {
		System.out.println( message );
		chatGUI.textArea.append( message );
		chatGUI.textArea.setCaretPosition(chatGUI.textArea.getDocument().getLength());
	}

	// Method to update list of users online
	@Override
	public void updateUserList(String[] currentUsers) throws RemoteException {

		if(currentUsers.length < 2){
			chatGUI.privateMsgButton.setEnabled(false);
		}
		chatGUI.userPanel.remove(chatGUI.clientPanel);
		chatGUI.setClientPanel(currentUsers);
		chatGUI.clientPanel.repaint();
		chatGUI.clientPanel.revalidate();
	}

}














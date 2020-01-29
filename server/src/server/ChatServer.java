package server;

import java.net.MalformedURLException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.*;

import client.ChatClientInterface;



public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {
	String line = "---------------------------------------------\n";
	private Vector<Users> chatters;
	private static final long serialVersionUID = 1L;
	
	//Constructor
	public ChatServer() throws RemoteException {
		super();
		chatters = new Vector<Users>(10, 1);
	}
	
	
	//Main method
	public static void main(String[] args) {
		RMI_registry_start();	
		String hostName = "localhost";
		String serviceName = "GroupChatService";
		
		if(args.length == 2){
			hostName = args[0];
			serviceName = args[1];
		}
		
		try{
			ChatServerInterface hello = new ChatServer();
			Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
			System.out.println("Group Chat RMI Server is running...");
		}
		catch(Exception e){
			System.out.println("Server had problems starting");
		}	
	}

	
	//Start the RMI Registry
	public static void RMI_registry_start() {
		try{
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("Seriver is ready");
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
	}
		
	
	// Remote methods
	
	
	// Send message to clients
	public String greet(String ClientName) throws RemoteException {
		System.out.println(ClientName + " sent a message");
		return "Hi " + ClientName + " from the chat server";
	}
	

	
	// Send latest message to all connected clients
	public void updateChat(String name, String nextPost) throws RemoteException {
		String message =  name + " : " + nextPost + "\n";
		send_group_message(message);
	}
	
	
	//New client remote interface
	@Override
	public void IdentityPass(RemoteRef ref) throws RemoteException {	
		try{
			System.out.println(line + ref.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
	// Method to receive clients, check if client already exists, print message on console, and call register method to register the client
	@Override
	public int registerListener(String[] details) throws RemoteException {	
		int exist = 0;
		
		for(Users c : chatters){
			System.out.println("This is the list " + details[0]);
			if(details[0].equals(c.getName())){
				exist = 1;
				System.out.println(details[0] + "ALREADY EXISTS");
				break;
			}
		}
		if(exist == 1){
			System.out.println(details[0] + "is not added");
			return 1;
		}
		else{
			System.out.println(new Date(System.currentTimeMillis()));
			System.out.println(details[0] + " has joined the chat session");
			System.out.println(details[0] + "'s hostname : " + details[1]);
			System.out.println(details[0] + "'sRMI service : " + details[2]);
			register_user(details);
			return 0;
		}
		
	}

	
	//register the clients interface and store it in a reference for future messages to be sent to
	// Send a test message for confirmation / test connection
	private void register_user(String[] details){	
//		int exist = 0;
		try{
			ChatClientInterface nextClient = ( ChatClientInterface )Naming.lookup("rmi://" + details[1] + "/" + details[2]);
			
			
//			for(Chatter c : chatters){
//				
//				if(details[0].equals(c.getName())){
//					exist = 1;
//					break;
//				}
//			}
			
//			if(exist == 1){nextClient.messageFromServer(details[0] + " user already exists");}
//			else{
			chatters.addElement(new Users(details[0], nextClient));
			
			nextClient.messageFromServer("[Server] : Hello " + details[0] + " you are now free to chat.\n");
			
			send_group_message("[Server] : " + details[0] + " has joined the group.\n");
			
			updateUserList();		
//			}
		}
		catch(RemoteException | MalformedURLException | NotBoundException e){
			e.printStackTrace();
		}
//		
	}
	
	// Update the list of clients that are online
	private void updateUserList() {
		String[] currentUsers = getUserList();	
		for(Users c : chatters){
			try {
				c.getClient().updateUserList(currentUsers);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}
	

	// Create a string of array of clients that are currently online
	private String[] getUserList(){
		// generate an array of current users
		String[] allUsers = new String[chatters.size()];
		for(int i = 0; i< allUsers.length; i++){
			allUsers[i] = chatters.elementAt(i).getName();
		}
		return allUsers;
	}
	

	// Used to send a message to all of the users
	public void send_group_message(String newMessage){	
		for(Users c : chatters){
			try {
				c.getClient().messageFromServer(newMessage);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}

	
	// Send a private message to clients that are selected by the user
	public void sendPM(int[] privateGroup, String privateMessage) throws RemoteException{
		Users pc;
		for(int i : privateGroup){
			pc= chatters.elementAt(i);
			pc.getClient().messageFromServer(privateMessage);
		}
	}

	
	// Remove a client from online users list, and send all users a message that client has left
	@Override
	public void leaveChat(String userName) throws RemoteException{
		
		for(Users c : chatters){
			if(c.getName().equals(userName)){
				System.out.println(line + userName + " left the chat session");
				System.out.println(new Date(System.currentTimeMillis()));
				chatters.remove(c);
				break;
			}
		}		
		if(!chatters.isEmpty()){
			updateUserList();
		}			
	}
		
}




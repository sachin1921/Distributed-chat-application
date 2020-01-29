package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;


public interface ChatServerInterface extends Remote {
		
	public void updateChat(String userName, String chatMessage)throws RemoteException;
	
	public void IdentityPass(RemoteRef ref)throws RemoteException;
	
	public int registerListener(String[] details)throws RemoteException;
	
	public void sendPM(int[] privateGroup, String privateMessage)throws RemoteException;
	
	public void leaveChat(String userName)throws RemoteException;
	
}



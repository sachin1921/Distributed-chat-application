package server;

import client.ChatClientInterface;



public class Users {

	public String name;
	public ChatClientInterface client;
	
	public Users(String name, ChatClientInterface client){
		this.name = name;
		this.client = client;
	}

	
	// method to get name and to set name
	public String getName(){
		return name;
	}
	public ChatClientInterface getClient(){
		return client;
	}
	
	
}

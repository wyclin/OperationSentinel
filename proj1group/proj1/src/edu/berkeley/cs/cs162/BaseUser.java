package edu.berkeley.cs.cs162;

import java.util.ArrayList;
/**
 * You should extend this class to deliver messages to other users and
 * groups.  Make sure you implement (but don't modify) the send() and
 * msgReceived() methods so we can use them from the test harness.
 *
 */

public class BaseUser extends Thread {
	ChatServer myServer;
	String name;
	ArrayList<Group> myGroups;
	
	public BaseUser() {
		super();
	}
	/* You may need to create additional constructors and methods to finish the project */

	public BaseUser(String name, ChatServer myServer) {
		this.name = name;
		this.myServer = myServer;
		myGroups = new ArrayList<Group>();
	}
	
	public String getUsername(){
		return name;
	}
	
	public ArrayList<Group> listGroups(){
		return myGroups;
	}
	
	/**
	 * This function is called when the user successfully connect to the server. 
	 * It also starts the thread that represents this particular user.
	 */
	public void connected() {
		this.start();
	}

	/**
	 * @param dest  Destination could be a user or a group. 
	 * @param msg   Message to be sent. 
         *
	 * This is used to inject messages from the test harness into
	 * your system.  It should send a message to the
	 * destination.  
	 */
	public void send(String dest, String msg) {
		Message message = new Message(name, dest, msg);
		myServer.send(message);
	}
	
	/**
	 * @param msg Received message.
	 * Called when a message is received by the thread. 
         *
         * This is part of the test harness -- in the future you will
         * send the message to the user over a socket.  For now, you
         * should print the message to stdout.  The format is:
         *
         * <source name>\t<destination>\t<sequence number>\t<message>
	 */
	public void msgReceived(String msg){
		System.out.println(msg);
	}

}

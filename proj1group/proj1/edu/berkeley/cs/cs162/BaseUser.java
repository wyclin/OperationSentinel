package edu.berkeley.cs.cs162;
public class BaseUser extends Thread {
// testing

	public BaseUser() {
		super();
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
	 * Send a message to the destination. Must not serialize.
	 */
	public void send(String dest, String msg) {
	
	}
	
	/**
	 * @param msg Received message.
	 * Called when a message is received by the thread. 
	 */
	public void msgReceived(String msg){
		
	}

}
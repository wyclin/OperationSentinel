package edu.berkeley.cs.cs162;

/**
 * This is an example of how you should test your chat server.  Create
 * users and groups, and have them login, logout, and send messages in
 * different orders.  By using a thread pool, we are able to increase
 * the level of concurrency to better stress your synchronization.
 *
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestChatServer {
	
	private static transient final Logger logger = Logger.getLogger(TestChatServer.class.getPackage().getName());
	
	public static void main(String [] args) throws InterruptedException {
		ChatServerInterface s = new ChatServer();
		ExecutorService exe = Executors.newFixedThreadPool(10);
		int i;
		
		
		s.login("steve");
		s.login("mike");
		BaseUser bu = s.getUser("mike");
		
		s.joinGroup(bu, "group1");
		
		for (i = 0; i < 50; i++) {
			MessageDeliveryTask t = new MessageDeliveryTask(s, "steve", "mike", "hi "+ i);
			MessageDeliveryTask c = new MessageDeliveryTask(s, "steve", "group1", "hig "+ i);
			exe.execute(t);
			exe.execute(c);
		}
		exe.shutdown();
		
		s.shutdown();
		System.out.println("done \n");
	}
	
	/**
	 * Logs the events of a user logging into the ChatServer.  This should only be called AFTER
	 * the user has been accepted by the ChatServer.
	 *
	 * @param username user logging the event.
	 * @param time time of the event.
	 */
	public static void logUserLogin(String username, Date time){
	}

	/**
	 *  Logs a login-failed event.  Should be called AFTER you are certain that the user has been rejected by
	 *  by the ChatServer.
	 * 
	 * @param username user logging the event.
	 * @param time time of the event.
	 * @param e login error
	 */
	public static void logUserLoginFailed(String username, Date time, LoginError e){
	}
	
	/**
	 * Logs the logout event.  This should only be called AFTER the user has been released by the
	 * ChatServer successfully.
	 * 
	 * @param username user logging the event
	 * @param time time of the event
	 */
	public static void logUserLogout(String username, Date time){
	}
	
	/**
	 * Logs the events of a user logging into the group.  This should only be called AFTER
	 * the user has been accepted by the group.
	 *
	 * @param groupname name of the group.
	 * @param username user logging the event.
	 * @param time time of the event.
	 */
	public static void logUserJoinGroup(String groupname, String username, Date time){
	}
	
	/**
	 * Logs the events of a user logging out of the group.
	 *
	 * @param groupname name of the group.
	 * @param username user logging the event.
	 * @param time time of the event.
	 */
	public static void logUserLeaveGroup(String groupname, String username, Date time){
	}
	
	
	/**
	 * This should be called when the user attempts to send a message to the chat server 
	 * (after the call is made).
	 *
	 * @param username the name of the user
	 * @param msg the string representation of the message. 
	 * 			SRC DST TIMESTAMP_UNIXTIME SQN
	 * 		example: alice bob 1298489721 23
	 */
	public static void logUserSendMsg(String username, String msg){
	}
	
	/**
	 * If, for any reason, the ChatServer determines that the message cannot be delivered.  This
	 * message should be called to log that event.
	 * 
	 * @param msg string representation of the message
	 * 			SRC DST TIMESTAMP_UNIXTIME SQN
	 * 		example: alice bob 1298489721 23
	 * @param time time when the event occurred.
	 */
	public static void logChatServerDropMsg(String msg, Date time){
	}
	
	/**
	 * When the user receives a message, this method should be called.
	 * 
	 * @parapm username name of the user
	 * @param msg string representation of the message.
	 * 			SRC DST TIMESTAMP_UNIXTIME SQN
	 * 		example: alice bob 1298489721 23
	 * @param time time when the event occurred.
	 */
	public static void logUserMsgRecvd(String username, String msg, Date time){
	}
}

package edu.berkeley.cs.cs162;


/**
 * This is the core of the chat server.  Put the management of groups
 * and users in here.  You will need to control all of the threads,
 * and respond to requests from the test harness.
 *
 * It must implement the ChatServerInterface Interface, and you should
 * not modify that interface; it is necessary for testing.
 */

public class ChatServer extends Thread implements ChatServerInterface {

	static final int MAX_CHAT_USERS = 100, MAX_GROUP_USERS = 10;
	
	private MessageDispatcher messageDispatcher;
	private UserManager userManager;
	
	private boolean isActive;
		
	public ChatServer(){
		isActive = true;
 		messageDispatcher = new MessageDispatcher(this);
		userManager = new UserManager(this);
		messageDispatcher.start();
	}
		
	public LoginError login(String username) {
		if (isActive) {
			return userManager.addUser(username);
		} else {
			return LoginError.USER_DROPPED;
		}
	}

	public boolean logoff(String username) {
		return userManager.removeUser(username);
	}

	public boolean joinGroup(BaseUser user, String groupname) {
		if (!userManager.hasGroup(groupname)) {
			userManager.createGroup(groupname);
		}
		return userManager.addUserToGroup(user, groupname);
	}

	public boolean leaveGroup(BaseUser user, String groupname) {
		if (!userManager.hasGroup(groupname)){
			return false;
		}
		return userManager.removeUserFromGroup(user.getUsername(), groupname);
	}

	/** Returns true if the chatServer has group or user with the given name. */
	public boolean hasName(String name) {
	       return userManager.hasName(name);
        }

        /** Returns the BaseUser object with the given username.*/
	public BaseUser getUser(String username) {
		return userManager.getUser(username);
	}
	
	/** Returns true if the server has user with given username. */
	public boolean hasUser(String username) {
		return userManager.hasUser(username);
	}

        /** Returns the Group object of the group with given groupName. */
	public Group getGroup(String groupName) {
		return userManager.getGroup(groupName);
	}

        /** Returns true if the server has group with given groupName. */
	public boolean hasGroup(String groupName) {
		return userManager.hasGroup(groupName);
	}

	public void shutdown() {
		isActive = false;
		while (messageDispatcher.hasMessage()){
			//sleep();
			// let messageDispatcher finish all the messages
		}
		messageDispatcher.stop();
		userManager.removeAll();
	}
	
	    public void send(Message message){
		messageDispatcher.enqueue(message);
	}
	
	public UserManager getUserManager(){
		return userManager;
	}
	
}

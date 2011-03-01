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
	
	MessageDispatcher messageDispatcher;
	UserManager userManager;
	
	private boolean isActive;
		
	public ChatServer(){
		isActive = true;
		messageDispatcher = new MessageDispatcher();
		userManager = new UserManager(this);
		messageDispatcher.start();
	}
		
	@Override
	public LoginError login(String username) {
		if (isActive) {
			return userManager.addUser(username);
		} else {
			return LoginError.USER_DROPPED;
		}
	}

	@Override
	public boolean logoff(String username) {
		return userManager.removeUser(username);
	}

	@Override
	public boolean joinGroup(BaseUser user, String groupname) {
		if (!userManager.hasGroup(groupname)) {
			userManager.createGroup(groupname);
		}
		return userManager.addUserToGroup(user, groupname);
	}

	@Override
	public boolean leaveGroup(BaseUser user, String groupname) {
		if (!userManager.hasGroup(groupname)){
			return false;
		}
		return userManager.removeUserFromGroup(user.getUsername(), groupname);
	}


	@Override
	public BaseUser getUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
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
}

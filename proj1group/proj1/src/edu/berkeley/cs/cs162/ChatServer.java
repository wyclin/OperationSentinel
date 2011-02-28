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

	HashMap users = new HashMap();
	HashMap groups = new HashMap();
	
	static final int MAX_CHAT_USERS = 100, MAX_GROUP_USERS = 10;
	
	MessageDispatcher MD;
	UserManager UM;
	
	private boolean isActive;
	
	
	public ChatServer(){
		MD = new MessageDispatcher();
		UM = new UserManager();
		active = true;
	}
		
	@Override
	public LoginError login(String username) {
		if (isActive) {
			return UM.addUser(username);
		} else {
			return LoginError.USER_DROPPED;
		}
	}

	@Override
	public boolean logoff(String username) {
		return UM.removeUser(username);
	}

	@Override
	public boolean joinGroup(BaseUser user, String groupname) {
		if (!UM.hasGroup(groupname)) {
			UM.createGroup(String groupname);
		}
		return UM.addUserToGroup(user, groupname);
	}

	@Override
	public boolean leaveGroup(BaseUser user, String groupname) {
		if (!UM.hasGroup(groupname)){
			return false;
		}
		return UM.removeUserFromGroup(user, groupname);
	}


	@Override
	public BaseUser getUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		active = false;
		while (MD.hasMessage()){
			//sleep();
		}
		UM.removeAll();
	}
	
    
    public void send(Message message){
		MD.enqueue(message);
	}
}

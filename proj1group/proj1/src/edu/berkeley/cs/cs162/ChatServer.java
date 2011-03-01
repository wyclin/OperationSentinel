package edu.berkeley.cs.cs162;

import java.util.Calendar;
import java.util.Set;

public class ChatServer extends Thread implements ChatServerInterface {

    static final int MAX_CHAT_USERS = 100, MAX_GROUP_USERS = 10;
	
	private MessageDispatcher messageDispatcher;
	private UserManager userManager;
	
    private boolean isActive;

    public ChatServer(){
        this.isActive = true;
        this.messageDispatcher = new MessageDispatcher(this);
        this.userManager = new UserManager(this);
        this.messageDispatcher.start();
	}
		
    public LoginError login(String userName) {
        if (isActive) {
            return userManager.addUser(userName);
        } else {
            TestChatServer.logUserLoginFailed(userName, Calendar.getInstance().getTime(), LoginError.USER_DROPPED);
            return LoginError.USER_DROPPED;
        }
    }

    public boolean logoff(String userName) {
        return userManager.removeUser(userName);
    }

    public boolean joinGroup(BaseUser user, String groupName) {
        if (!userManager.hasGroup(groupName)) {
            userManager.createGroup(groupName);
        }
        return userManager.addUserToGroup(user, groupName);
    }

    public boolean leaveGroup(BaseUser user, String groupName) {
        if (!userManager.hasGroup(groupName)){
            return false;
        }
        return userManager.removeUserFromGroup(user.getUsername(), groupName);
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

	public Set<String> listAllUsers(String userName) {
        if (userManager.hasUser(userName)) {
            return (Set<String>) userManager.listUsers();
        } else {
            return null;
        }
        /** Returns the Group object of the group with given groupName. */
	public Group getGroup(String groupName) {
		return userManager.getGroup(groupName);
	}

        /** Returns true if the server has group with given groupName. */
	public boolean hasGroup(String groupName) {
		return userManager.hasGroup(groupName);
	}

	public Set<String> listAllGroups(String userName) {
        if (userManager.hasUser(userName)) {
            return (Set<String>) userManager.listGroups();
        } else {
            return null;
        }
	}

	public int getNumberOfUsers(String userName) {
        if (userManager.hasUser(userName)) {
            return userManager.getNumUsers();
        } else {
            return -1;
        }
	}

	public int getNumberOfGroups(String userName) {
        if (userManager.hasUser(userName)) {
            return userManager.getNumGroups();
        } else {
            return -1;
        }
	}
	
	public void send(Message message){
		messageDispatcher.enqueue(message);
	}
	
	public UserManager getUserManager(){
		return userManager;
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
}

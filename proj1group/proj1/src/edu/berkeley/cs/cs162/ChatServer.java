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

    /* Logs user onto server, and returns a code representing action status */
    public LoginError login(String userName) {
        if (isActive) {
            return userManager.addUser(userName);
        } else {
            TestChatServer.logUserLoginFailed(userName, Calendar.getInstance().getTime(), LoginError.USER_DROPPED);
            return LoginError.USER_DROPPED;
        }
    }

    /* Logs user out of server */
    public boolean logoff(String userName) {
        return userManager.removeUser(userName);
    }

    /* Adds user to specified group.  Creates new group if group does not exist */
    public boolean joinGroup(BaseUser user, String groupName) {
        if (!userManager.hasGroup(groupName)) {
            userManager.createGroup(groupName);
        }
        return userManager.addUserToGroup(user, groupName);
    }

    /* Removes user from specified group */
    public boolean leaveGroup(BaseUser user, String groupName) {
        if (!userManager.hasGroup(groupName)){
            return false;
        }
        return userManager.removeUserFromGroup(user.getUsername(), groupName);
    }

	/* Returns the User object with the given username */
    public BaseUser getUser(String username) {
		return userManager.getUser(username);
	}

    /** Returns the Group object of the group with given groupName. */
    public Group getGroup(String groupName) {
        return userManager.getGroup(groupName);
    }

	/** Returns true if the server has user or group with given name. */
	public boolean hasName(String name) {
		return userManager.hasName(name);
	}
	
	/** Returns true if the server has user with given username. */
	public boolean hasUser(String username) {
		return userManager.hasUser(username);
	}

	/** Returns true if the server has group with given groupName. */
	public boolean hasGroup(String groupName) {
		return userManager.hasGroup(groupName);
	}

       /** Returns a list of all User objects on the chat server */
	public Set<String> listAllUsers(String userName) {
        if (userManager.hasUser(userName)) {
            return (Set<String>) userManager.listUsers();
        } else {
            return null;
        }
	}

    /* Returns a list of Group objects on the chat server */
	public Set<String> listAllGroups(String userName) {
        if (userManager.hasUser(userName)) {
            return (Set<String>) userManager.listGroups();
        } else {
            return null;
        }
	}

    /* Returns the total number of users logged on the chat server */
	public int getNumberOfUsers(String userName) {
        if (userManager.hasUser(userName)) {
            return userManager.getNumUsers();
        } else {
            return -1;
        }
	}

    /* Returns the total number of groups on the chat server */
	public int getNumberOfGroups(String userName) {
        if (userManager.hasUser(userName)) {
            return userManager.getNumGroups();
        } else {
            return -1;
        }
	}

	/* Sends a message, using the parameters found in the Message object */
	public void send(Message message) {
        messageDispatcher.enqueue(message);
	}

    /* Returns the UserManager object */
	public UserManager getUserManager(){
		return userManager;
	}

    /* Shuts down the chat server */
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

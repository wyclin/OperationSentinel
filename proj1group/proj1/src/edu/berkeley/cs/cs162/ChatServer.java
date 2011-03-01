package edu.berkeley.cs.cs162;

import java.util.Calendar;
import java.util.Set;

public class ChatServer extends Thread implements ChatServerInterface {

    static final int MAX_CHAT_USERS = 100, MAX_GROUP_USERS = 10;
	
    MessageDispatcher messageDispatcher;
    UserManager userManager;
	
    private boolean isActive;

    public ChatServer(){
        this.isActive = true;
        this.messageDispatcher = new MessageDispatcher();
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

    public BaseUser getUser(String userName) {
        return userManager.getUser(userName);
    }

	public Set<String> listAllGroups(){
		return (Set<String>) userManager.listGroups();
	}

	public Set<String> listAllUsers(){
		return (Set<String>) userManager.listUsers();
	}

	public int getNumberOfUsers(){
		return userManager.getNumUsers();
	}

	public int getNumberOfGroups(){
		return userManager.getNumGroups();
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

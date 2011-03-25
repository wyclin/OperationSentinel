package edu.berkeley.cs.cs162;

import java.util.Calendar;
import java.util.TreeSet;

public class ChatServer {

    public static final int MAX_CHAT_USERS = 100;
    public static final int MAX_GROUP_USERS = 10;

	private MessageDispatcher messageDispatcher;
	private UserManager userManager;
    private boolean shuttingDown;

    public ChatServer() {
        this.userManager = new UserManager(this, MAX_CHAT_USERS, MAX_GROUP_USERS);
        this.messageDispatcher = new MessageDispatcher(this);
        this.shuttingDown = false;
	}

    public void start() {
        messageDispatcher.start();
    }

	public void shutdown() {
        shuttingDown = true;
        messageDispatcher.shutdown();
        try {
            messageDispatcher.join();
        } catch (InterruptedException e) {
        }
        userManager.shutdown();
    }

    public ChatServerResponse login(ChatUser user) {
        if (shuttingDown) {
            return ChatServerResponse.SHUTTING_DOWN;
        } else {
            return userManager.addUser(user);
        }
    }

    public ChatServerResponse logoff(ChatUser user) {
        return userManager.removeUser(user);
    }

    public ChatServerResponse joinGroup(ChatUser user, String groupName) {
        if (shuttingDown) {
            return ChatServerResponse.SHUTTING_DOWN;
        } else {
            return userManager.addUserToGroup(user, groupName);
        }
    }

    public ChatServerResponse leaveGroup(ChatUser user, String groupName) {
        return userManager.removeUserFromGroup(user, groupName);
    }

	public ChatServerResponse send(Message message) {
        if (shuttingDown) {
            return ChatServerResponse.SHUTTING_DOWN;
        } else {
            return messageDispatcher.enqueue(message);
        }
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }
}

package edu.berkeley.cs.cs162;

import java.util.Calendar;
import java.util.TreeSet;

public class ChatServer {

    public static final int MAX_CHAT_USERS = 100;
    public static final int MAX_GROUP_USERS = 10;

	private MessageDispatcher messageDispatcher;
	private UserManager userManager;
    private ConnectionManager connectionManager;
    private boolean shuttingDown;

    public ChatServer(int port) {
        this.userManager = new UserManager(this, MAX_CHAT_USERS, MAX_GROUP_USERS);
        this.messageDispatcher = new MessageDispatcher(this);
        this.connectionManager = new ConnectionManager(this, port);
        this.shuttingDown = false;
	}

    public void start() {
        messageDispatcher.start();
        connectionManager.start();
    }

	public void shutdown() {
        shuttingDown = true;
        connectionManager.shutdown();
        try {
            connectionManager.join();
        } catch (InterruptedException e) {
        }
        messageDispatcher.shutdown();
        try {
            messageDispatcher.join();
        } catch (InterruptedException e) {
        }
        userManager.shutdown();
    }

   public static void main(String[] args) {
       if (args.length == 1) {
           ChatServer newServer = new ChatServer(new Integer(args[0]));
           newServer.start();
       } else {
           System.err.println("USAGE: java ChatServer port#");
           System.exit(-1);
       }
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

    public ChatServerResponsePair getUserCount(ChatUser user) {
        return userManager.getUserCount(user);
    }

    public ChatServerResponsePair getUserList(ChatUser user) {
        return userManager.getUserList(user);
    }

    public ChatServerResponsePair getGroupCount(ChatUser user) {
        return userManager.getGroupCount(user);
    }

    public ChatServerResponsePair getGroupList(ChatUser user) {
        return userManager.getGroupList(user);
    }

    public ChatServerResponsePair getGroupUserCount(ChatUser user, String groupName) {
        return userManager.getGroupUserCount(user, groupName);
    }

    public ChatServerResponsePair getGroupUserList(ChatUser user, String groupName) {
        return userManager.getGroupUserList(user, groupName);
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }
}

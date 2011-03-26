package edu.berkeley.cs.cs162;

public class ChatServer {

    public static final int MAX_CHAT_USERS = 100;
    public static final int MAX_GROUP_USERS = 10;

    private UserManager userManager;
	private MessageDispatcher messageDispatcher;
    private ConnectionManager connectionManager;
    private boolean shuttingDown;
    private boolean networked;

    public ChatServer(int port) {
        this();
        this.connectionManager = new ConnectionManager(this, port);
        this.networked = true;
	}

    public ChatServer() {
        this.userManager = new UserManager(this, MAX_CHAT_USERS, MAX_GROUP_USERS);
        this.messageDispatcher = new MessageDispatcher(this);
        this.connectionManager = null;
        this.shuttingDown = false;
        this.networked = false;
    }

    public void start() {
        messageDispatcher.start();
        if (networked) {
            connectionManager.start();
        }
    }

	public void shutdown() {
        shuttingDown = true;
        if (networked) {
            connectionManager.shutdown();
            try {
                connectionManager.join();
            } catch (InterruptedException e) {
            }
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
            return new ChatServerResponse(ResponseType.SHUTTING_DOWN);
        } else {
            return userManager.addUser(user);
        }
    }

    public ChatServerResponse logoff(ChatUser user) {
        return userManager.removeUser(user);
    }

    public ChatServerResponse joinGroup(ChatUser user, String groupName) {
        if (shuttingDown) {
            return new ChatServerResponse(ResponseType.SHUTTING_DOWN);
        } else {
            return userManager.addUserToGroup(user, groupName);
        }
    }

    public ChatServerResponse leaveGroup(ChatUser user, String groupName) {
        return userManager.removeUserFromGroup(user, groupName);
    }

	public ChatServerResponse send(Message message) {
        if (shuttingDown) {
            return new ChatServerResponse(ResponseType.SHUTTING_DOWN);
        } else {
            return messageDispatcher.enqueue(message);
        }
    }

    public ChatServerResponse getUserCount(ChatUser user) {
        return userManager.getUserCount(user);
    }

    public ChatServerResponse getUserList(ChatUser user) {
        return userManager.getUserList(user);
    }

    public ChatServerResponse getGroupCount(ChatUser user) {
        return userManager.getGroupCount(user);
    }

    public ChatServerResponse getGroupList(ChatUser user) {
        return userManager.getGroupList(user);
    }

    public ChatServerResponse getGroupUserCount(ChatUser user, String groupName) {
        return userManager.getGroupUserCount(user, groupName);
    }

    public ChatServerResponse getGroupUserList(ChatUser user, String groupName) {
        return userManager.getGroupUserList(user, groupName);
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }
}

package edu.berkeley.cs.cs162;

public class ChatServer {

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
        this.userManager = new UserManager(this);
        this.messageDispatcher = new MessageDispatcher(this);
        this.connectionManager = null;
        this.shuttingDown = false;
        this.networked = false;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public DatabaseManager getDatabaseManager() {
        return userManager.getDatabaseManager();
    }

    public MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
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
           System.exit(-1);
       }
   }

    public ChatServerResponse addUser(String userName, String password) {
        if (shuttingDown) {
            return new ChatServerResponse(ResponseType.SHUTTING_DOWN);
        } else {
            return userManager.addUser(userName, password);
        }
    }

    public ChatServerResponse login(ChatUser user, String password) {
        if (shuttingDown) {
            return new ChatServerResponse(ResponseType.SHUTTING_DOWN);
        } else {
            return userManager.loginUser(user, password);
        }
    }

    public ChatServerResponse logout(ChatUser user) {
        return userManager.logoutUser(user);
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

    public void readLog(ChatUser user) {
        if (!shuttingDown) {
            messageDispatcher.deliverOfflineMessages(user);
        }
    }
}

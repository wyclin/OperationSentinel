package edu.berkeley.cs.cs162;

public class ChatServer {

    private UserManager userManager;
	private MessageDispatcher messageDispatcher;
    private PeerServerManager peerServerManager;
    private ClientConnectionManager clientConnectionManager;
    private ServerConnectionManager serverConnectionManager;
    private String name;
    private boolean shuttingDown;
    private boolean networked;

    public ChatServer(String name, int clientPort, int serverPort) {
        this();
        this.peerServerManager = new PeerServerManager(name);
        this.clientConnectionManager = new ClientConnectionManager(this, clientPort);
        this.serverConnectionManager = new ServerConnectionManager(this, name, serverPort);
        this.name = name;
        this.networked = true;
	}

    public ChatServer() {
        this.userManager = new UserManager(this);
        this.messageDispatcher = new MessageDispatcher(this);
        this.peerServerManager = null;
        this.clientConnectionManager = null;
        this.serverConnectionManager = null;
        this.shuttingDown = false;
        this.networked = false;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public DatabaseManager getDatabaseManager() {
        return userManager.getDatabaseManager();
    }

    public PeerServerManager getPeerServerManager() {
        return peerServerManager;
    }

    public MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }

    public ServerConnectionManager getServerConnectionManager() {
        return serverConnectionManager;
    }

    public void start() {
        messageDispatcher.start();
        if (networked) {
            clientConnectionManager.start();
            serverConnectionManager.start();
        }
    }

	public void shutdown() {
        shuttingDown = true;
        if (networked) {
            clientConnectionManager.shutdown();
            serverConnectionManager.shutdown();
            try {
                clientConnectionManager.join();
                serverConnectionManager.join();
            } catch (InterruptedException e) {}
            peerServerManager.shutdown();
        }
        messageDispatcher.shutdown();
        try {
            messageDispatcher.join();
        } catch (InterruptedException e) {
        }
        userManager.shutdown();
    }

    public static void main(String[] args) {
       if (args.length == 6) {
           ChatServer newServer = new ChatServer(args[1], Integer.valueOf(args[3]), Integer.valueOf(args[5]));
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

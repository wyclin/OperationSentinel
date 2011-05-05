package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

public class PeerServer extends Thread {

    private ChatServer chatServer;
    private MessageDispatcher messageDispatcher;
    private PeerServerManager peerServerManager;
    private PeerServerResponder responder;
    private Socket socket;
    private ObjectInputStream input;
    private LinkedBlockingQueue<ServerMessage> pendingMessages;
    private String localName;
    private String name;
    private boolean wait;

    public PeerServer(ChatServer chatServer, String localName, Socket socket) {
        this.chatServer = chatServer;
        this.messageDispatcher = chatServer.getMessageDispatcher();
        this.peerServerManager = chatServer.getPeerServerManager();
        this.socket = socket;
        this.pendingMessages = new LinkedBlockingQueue<ServerMessage>();
        this.responder = new PeerServerResponder(this, socket, pendingMessages);
        try {
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException f) {}
        }
        this.localName = localName;
        this.name = null;
        this.wait = true;
    }

    public PeerServer(ChatServer chatServer, String localName, Socket socket, String remoteName) {
        this(chatServer, localName, socket);
        this.name = remoteName;
        this.wait = false;
        peerServerManager.addServer(this);
    }

    public void start() {
        responder.start();
        super.start();
    }

    public void shutdown() {
        try {
            socket.close();
        } catch (IOException e) {}
        responder.interrupt();
        interrupt();
    }

    public void run() {
        ServerMessage serverMessage;
        try {
            if (wait) {
                serverMessage = (ServerMessage)input.readObject();
                while (serverMessage.messageType != ServerMessageType.ADD_SERVER) {
                    serverMessage = (ServerMessage)input.readObject();
                }
                name = serverMessage.serverName;
                peerServerManager.addServer(this);
            } else {
                pendingMessages.offer(new ServerMessage(ServerMessageType.ADD_SERVER, localName));
            }

            while(true) {
                serverMessage = (ServerMessage)input.readObject();
                if (serverMessage.messageType == ServerMessageType.SEND_MESSAGE) {
                    messageDispatcher.deliver(serverMessage.serializedMessage.toMessage());
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                input.close();
                socket.close();
                responder.interrupt();
            } catch (Exception f) {}
            peerServerManager.removeServer(this);
        }
    }

    public String getServerName() {
        return name;
    }

    public void sendMessage(String sender, String receiver, TreeSet<String> receivingUsers, Date date, int sqn, String text) {
        pendingMessages.offer(new ServerMessage(ServerMessageType.SEND_MESSAGE, new SerializedMessage(sender, receiver, receivingUsers, date, sqn, text)));
    }
}

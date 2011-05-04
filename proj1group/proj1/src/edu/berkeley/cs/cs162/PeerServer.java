package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class PeerServer extends Thread {

    private ChatServer chatServer;
    private PeerServerManager peerServerManager;
    private PeerServerResponder responder;
    private Socket socket;
    private ObjectInputStream input;
    private LinkedBlockingQueue<ChatClientCommand> pendingResponses;
    private String localName;
    private String name;
    private boolean wait;

    public PeerServer(ChatServer chatServer, String localName, Socket socket) {
        this.chatServer = chatServer;
        this.peerServerManager = chatServer.getPeerServerManager();
        this.socket = socket;
        this.pendingResponses = new LinkedBlockingQueue<ChatClientCommand>();
        this.responder = new PeerServerResponder(this, socket, pendingResponses);
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
        try {
            if (wait) {
                ChatClientCommand command = (ChatClientCommand)input.readObject();
                while (command.commandType != CommandType.ADD_SERVER) {
                    command = (ChatClientCommand)input.readObject();
                }
                name = command.string1;
                peerServerManager.addServer(this);
            } else {
                pendingResponses.add(new ChatClientCommand(CommandType.ADD_SERVER, localName));
            }

            ChatClientCommand command;
            while(true) {
                command = (ChatClientCommand)input.readObject();
                if (command.commandType == CommandType.SEND_MESSAGE) {
                    // Enqueue messages.
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
}

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
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;
    private String name;
    private String hostName;
    private int clientPort;
    private int serverPort;

    public PeerServer(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.peerServerManager = chatServer.getPeerServerManager();
        this.socket = socket;
        this.pendingResponses = new LinkedBlockingQueue<ChatServerResponse>();
        this.responder = new PeerServerResponder(this, socket, pendingResponses);
        try {
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException f) {}
        }
        this.name = null;
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
            ChatClientCommand command = (ChatClientCommand)input.readObject();
            while (command.commandType != CommandType.ADD_SERVER) {
                command = (ChatClientCommand)input.readObject();
            }
            name = command.string1;
            hostName = command.string2;
            clientPort = command.number1;
            serverPort = command.number2;
            peerServerManager.addServer(this);

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
        }
    }

    public String getServerName() {
        return name;
    }
}

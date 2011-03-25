package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;

public class ConnectionManager extends Thread {

    private ChatServer chatServer;
    private int port;
    private boolean shuttingDown;

    public ConnectionManager(ChatServer chatServer, int port) {
        this.chatServer = chatServer;
        this.port = port;
        this.shuttingDown = false;
    }

    public void shutdown() {
        this.shuttingDown = true;
        interrupt();
    }

    public void run() {
        ServerSocket serverSocket = null;
        while(!shuttingDown) {
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + Integer.toString(port));
                System.exit(-1);
            }
            try {
                new ChatUser(chatServer, serverSocket.accept()).start();
            } catch (IOException e) {
                System.err.println("Failed to accept connection.");
                System.exit(-1);
            } catch (Exception e) {
            }
        }
        try {
            serverSocket.close();
        } catch (Exception e) {
        }
    }
}

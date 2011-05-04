package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;

public class ClientConnectionManager extends Thread {

    private ChatServer chatServer;
    private ServerSocket serverSocket;
    private boolean shuttingDown;

    public ClientConnectionManager(ChatServer chatServer, int port) {
        this.chatServer = chatServer;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {}
        this.shuttingDown = false;
    }

    public void shutdown() {
        shuttingDown = true;
        try {
            serverSocket.close();
        } catch (IOException e) {}
    }

    public void run() {
        while(!shuttingDown) {
            try {
                new ChatUser(chatServer, serverSocket.accept()).start();
            } catch (Exception e) {}
        }
        try {
            serverSocket.close();
        } catch (Exception e) {}
    }
}

package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

public class ServerConnectionManager extends Thread {

    private ChatServer chatServer;
    private ServerSocket serverSocket;
    private boolean shuttingDown;
    private String name;
    private int serverPort;

    public ServerConnectionManager(ChatServer chatServer, String name, int serverPort) {
        this.chatServer = chatServer;
        this.serverSocket = null;
        this.shuttingDown = false;
        this.name = name;
        this.serverPort = serverPort;
    }

    public void start() {
        try {
            this.serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {}
        super.start();

        DatabaseManager databaseManager = chatServer.getDatabaseManager();
        LinkedList<HashMap<String, Object>> serverList;
        try {
            serverList = databaseManager.getServerList();
            for (HashMap<String, Object> server : serverList) {
                if (!((String)server.get("name")).equals(name)) {
                    try {
                        new PeerServer(chatServer, name, new Socket((String)server.get("host"), (Integer)server.get("sport")), (String)server.get("name")).start();
                    } catch (Exception f) {}
                }
            }
        } catch (SQLException e) {}
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
                new PeerServer(chatServer, name, serverSocket.accept()).start();
            } catch (Exception e) {}
        }
        try {
            serverSocket.close();
        } catch (Exception e) {}
    }
}

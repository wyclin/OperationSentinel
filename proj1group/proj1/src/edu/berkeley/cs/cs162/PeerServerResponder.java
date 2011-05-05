package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class PeerServerResponder extends Thread {

    private PeerServer peerServer;
    private LinkedBlockingQueue<ServerMessage> pendingMessages;
    private Socket socket;
    private ObjectOutputStream output;

    public PeerServerResponder(PeerServer peerServer, Socket socket, LinkedBlockingQueue<ServerMessage> pendingMessages) {
        this.peerServer = peerServer;
        this.socket = socket;
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            try {
                this.socket.close();
            } catch (IOException f) {}
        }
        this.pendingMessages = pendingMessages;
    }

    public void run () {
        try {
            while(true) {
                output.writeObject(pendingMessages.take());
                output.flush();
            }
        } catch (Exception e) {
        } finally {
            try {
                output.close();
                socket.close();
            } catch (Exception f) {}
        }
    }
}

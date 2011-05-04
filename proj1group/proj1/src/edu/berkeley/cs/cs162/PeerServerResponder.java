package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class PeerServerResponder extends Thread {

    private PeerServer peerServer;
    private LinkedBlockingQueue<ChatClientCommand> pendingResponses;
    private Socket socket;
    private ObjectOutputStream output;

    public PeerServerResponder(PeerServer peerServer, Socket socket, LinkedBlockingQueue<ChatClientCommand> pendingResponses) {
        this.peerServer = peerServer;
        this.socket = socket;
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            try {
                this.socket.close();
            } catch (IOException f) {}
        }
        this.pendingResponses = pendingResponses;
    }

    public void run () {
        try {
            while(true) {
                output.writeObject(pendingResponses.take());
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

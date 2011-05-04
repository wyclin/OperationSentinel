package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class PeerServerResponder extends Thread {

    private PeerServer peerServer;
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;
    private Socket socket;
    private ObjectOutputStream output;

    public PeerServerResponder(PeerServer peerServer, Socket socket, LinkedBlockingQueue<ChatServerResponse> pendingResponses) {
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
        ChatServerResponse response;
        try {
            while(true) {
                response = pendingResponses.take();
                if (response.responseType == ResponseType.MESSAGE_RECEIVED || response.responseType == ResponseType.MESSAGE_DELIVERY_FAILURE) {
                    response.messageDate = response.message.date;
                    response.messageSender = response.message.senderName;
                    response.messageReceiver = response.message.receiver;
                    response.messagesqn = response.message.sqn;
                    response.messageText = response.message.text;
                    response.message = null;
                }
                output.writeObject(response);
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

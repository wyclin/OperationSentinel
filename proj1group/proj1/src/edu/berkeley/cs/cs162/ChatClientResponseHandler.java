package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClientResponseHandler extends Thread {

    private Socket socket;
    private ObjectInputStream remoteInput;
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;

    public ChatClientResponseHandler(ChatClient chatClient, Socket socket, LinkedBlockingQueue<ChatServerResponse> pendingResponses) {
        this.socket = socket;
        this.pendingResponses = pendingResponses;
        try {
            remoteInput = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            chatClient.disconnect();
        }
    }

    public void run() {
        while (true) {
            try {
                pendingResponses.offer((ChatServerResponse)remoteInput.readObject());
            } catch (Exception e) {
            } finally {
                try {
                    remoteInput.close();
                    socket.close();
                } catch (Exception f) {
                }
            }
        }
    }
}

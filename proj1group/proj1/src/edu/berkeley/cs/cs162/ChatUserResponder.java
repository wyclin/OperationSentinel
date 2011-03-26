package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatUserResponder extends Thread {

    private ChatUser chatUser;
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;
    private Socket socket;
    private ObjectOutputStream output;
    private boolean shuttingDown;

    public ChatUserResponder(ChatUser chatUser, Socket socket, LinkedBlockingQueue<ChatServerResponse> pendingResponses) {
        this.chatUser = chatUser;
        this.socket = socket;
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            try {
                this.socket.close();
            } catch (IOException f) {
            }
        }
        this.pendingResponses = pendingResponses;
        this.shuttingDown = false;
    }

    public void shutdown() {
        shuttingDown = true;
    }

    public void run() {
        ChatServerResponse response = null;
        try {
            while (!shuttingDown) {
                response = pendingResponses.take();
                output.writeObject(response);
                output.flush();
            }
        } catch (Exception e) {
        } finally {
            try {
                output.close();
            } catch (Exception f) {
            }
            if (response != null) {
                pendingResponses.offer(response);
            }
            chatUser.interrupt();
        }
    }
}

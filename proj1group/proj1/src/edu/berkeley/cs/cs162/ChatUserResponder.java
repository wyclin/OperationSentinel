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
        try {
            while (!shuttingDown) {
                ChatServerResponse response = pendingResponses.poll();
                while (response != null) {
                    output.writeObject(response);
                    response = pendingResponses.poll();
                }
                output.flush();
            }
            output.close();
            chatUser.interrupt();
        } catch (IOException e) {
        }
    }
}

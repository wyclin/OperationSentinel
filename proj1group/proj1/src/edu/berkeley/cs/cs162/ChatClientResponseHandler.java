package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClientResponseHandler extends Thread {

    private ChatClient chatClient;
    private Socket socket;
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;

    public ChatClientResponseHandler(ChatClient chatClient, Socket socket, LinkedBlockingQueue<ChatServerResponse> pendingResponses) {
        this.chatClient = chatClient;
        this.socket = socket;
        this.pendingResponses = pendingResponses;
    }

    public void shutdown() {
    }
}

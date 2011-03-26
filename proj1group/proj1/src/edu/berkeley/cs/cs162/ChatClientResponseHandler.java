package edu.berkeley.cs.cs162;

import java.io.*;

public class ChatClientResponseHandler extends Thread {

    private ChatClient chatClient;
    private boolean shuttingDown;

    public ChatClientResponseHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.shuttingDown = false;
    }

    public void shutdown() {
        this.shuttingDown = true;
    }
}

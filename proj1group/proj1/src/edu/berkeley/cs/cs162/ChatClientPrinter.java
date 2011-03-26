package edu.berkeley.cs.cs162;

import java.io.*;

public class ChatClientPrinter extends Thread {

    private ChatClient chatClient;
    private PrintWriter output;
    private boolean shuttingDown;

    public ChatClientPrinter(ChatClient chatClient, PrintWriter output) {
        this.chatClient = chatClient;
        this.output = output;
        this.shuttingDown = false;
    }

    public void shutdown() {
        this.shuttingDown = true;
    }
}

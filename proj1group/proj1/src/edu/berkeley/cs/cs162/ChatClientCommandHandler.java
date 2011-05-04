package edu.berkeley.cs.cs162;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClientCommandHandler extends Thread {

    private BufferedReader localInput;
    private LinkedBlockingQueue<String> pendingCommands;

    public ChatClientCommandHandler(BufferedReader localInput, LinkedBlockingQueue<String> pendingCommands) {
        this.localInput = localInput;
        this.pendingCommands = pendingCommands;
    }

    public void run() {
        String commandString;
        try {
            commandString = localInput.readLine();
            while (commandString != null) {
                pendingCommands.offer(commandString);
                commandString = localInput.readLine();
            }
        } catch (IOException e) {}
    }
}

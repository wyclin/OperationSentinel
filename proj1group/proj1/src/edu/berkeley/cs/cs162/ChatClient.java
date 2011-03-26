package edu.berkeley.cs.cs162;

import java.io.*;

public class ChatClient extends Thread {

    private ChatClientPrinter printer;
    private BufferedReader input;
    private PrintWriter output;
    private boolean shuttingDown;
    private boolean connected;

    public ChatClient(BufferedReader input, PrintWriter output) {
        this.printer = null;
        this.input = input;
        this.output = output;
        this.shuttingDown = false;
        this.connected = false;
    }

    public static void main(String[] args) {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
   }

    public void run() {
        try {
            String command = input.readLine();
            while (command != null) {
                executeCommand(parseCommand(command));
            }
        } catch (IOException e) {
        }
    }

    public ChatClientCommand parseCommand(String command) {
        return new ChatClientCommand(CommandType.COMMAND_NOT_FOUND);
    }

    public void executeCommand(ChatClientCommand command) {

    }
}

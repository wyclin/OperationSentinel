package edu.berkeley.cs.cs162;

import java.io.*;
import java.util.regex.*;

public class ChatClient extends Thread {

    private ChatClientResponseHandler responseHandler;
    private BufferedReader input;
    private PrintWriter output;
    private boolean shuttingDown;
    private boolean connected;

    private Pattern connectPattern;
    private Pattern disconnectPattern;
    private Pattern loginPattern;
    private Pattern logoutPattern;
    private Pattern joinPattern;
    private Pattern leavePattern;
    private Pattern sendPattern;
    private Pattern sleepPattern;

    public ChatClient(BufferedReader input, PrintWriter output) {
        this.responseHandler = null;
        this.input = input;
        this.output = output;
        this.shuttingDown = false;
        this.connected = false;

        connectPattern = Pattern.compile("^connect ([^:\\s]+):(\\d{1,5})$");
        disconnectPattern = Pattern.compile("^disconnect$");
        loginPattern = Pattern.compile("^login ([^\\s]+)$");
        logoutPattern = Pattern.compile("^logout$");
        joinPattern = Pattern.compile("^join ([^\\s]+)$");
        leavePattern = Pattern.compile("^leave ([^\\s]+)$");
        sendPattern = Pattern.compile("^send ([^\\s]+) (\\d+) \"([^\"]+)\"$");
        sleepPattern = Pattern.compile("^sleep (\\d+)$");
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
        switch (command.commandType) {
            case CONNECT:
                connect(command.string, command.number);
                break;
            case DISCONNECT:
                disconnect();
                break;
            case LOGIN:
                login(command.string);
                break;
            case LOGOUT:
                logout();
                break;
            case JOIN_GROUP:
                joinGroup(command.string);
                break;
            case LEAVE_GROUP:
                leaveGroup(command.string);
                break;
            case SEND_MESSAGE:
                sendMessage(command.message);
                break;
            case SLEEP:
                sleep(command.number);
                break;
        }
    }

    public void connect(String host, int port) {}

    public void disconnect() {}

    public void login(String userName) {}

    public void logout() {}

    public void joinGroup(String groupName) {}

    public void leaveGroup(String groupName) {}

    public void sendMessage(Message message) {}

    // in milliseconds
    public void sleep(int time) {}
}

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
        Matcher connectMatcher = connectPattern.matcher(command);
        Matcher disconnectMatcher = disconnectPattern.matcher(command);
        Matcher loginMatcher = loginPattern.matcher(command);
        Matcher logoutMatcher = logoutPattern.matcher(command);
        Matcher joinMatcher = joinPattern.matcher(command);
        Matcher leaveMatcher = leavePattern.matcher(command);
        Matcher sendMatcher = sendPattern.matcher(command);
        Matcher sleepMatcher = sleepPattern.matcher(command);
        if (connectMatcher.matches()) {
            return new ChatClientCommand(CommandType.CONNECT, connectMatcher.group(1), Integer.valueOf(connectMatcher.group(2)));
        } else if (disconnectMatcher.matches()) {
            return new ChatClientCommand(CommandType.DISCONNECT);
        } else if (loginMatcher.matches()) {
            return new ChatClientCommand(CommandType.LOGIN, loginMatcher.group(1));
        } else if (logoutMatcher.matches()) {
            return new ChatClientCommand(CommandType.LOGOUT);
        } else if (joinMatcher.matches()) {
            return new ChatClientCommand(CommandType.JOIN_GROUP, joinMatcher.group(1));
        } else if (leaveMatcher.matches()) {
            return new ChatClientCommand(CommandType.LEAVE_GROUP, leaveMatcher.group(1));
        } else if (sendMatcher.matches()) {
            return new ChatClientCommand(CommandType.SEND_MESSAGE, sendMatcher.group(1), Integer.valueOf(sendMatcher.group(2)), sendMatcher.group(3));
        } else if (sleepMatcher.matches()) {
            return new ChatClientCommand(CommandType.SLEEP, Integer.valueOf(sleepMatcher.group(1)));
        } else {
            return new ChatClientCommand(CommandType.COMMAND_NOT_FOUND);
        }
    }

    public void executeCommand(ChatClientCommand command) {
        switch (command.commandType) {
            case CONNECT:
                connect(command.string1, command.number);
                break;
            case DISCONNECT:
                disconnect();
                break;
            case SLEEP:
                sleep(command.number);
                break;
            case LOGIN:
            case LOGOUT:
            case JOIN_GROUP:
            case LEAVE_GROUP:
            case SEND_MESSAGE:
                sendCommand(command);
                break;
            default: // Silently drop unknown command
                break;
        }
    }

    public void sendCommand(ChatClientCommand command) {}

    public void connect(String host, int port) {}

    public void disconnect() {}

    // in milliseconds
    public void sleep(int time) {}
}

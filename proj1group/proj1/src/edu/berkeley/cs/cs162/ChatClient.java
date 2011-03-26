package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.*;

public class ChatClient extends Thread {

    private ChatClientResponseHandler responseHandler;
    private BufferedReader localInput;
    private PrintWriter localOutput;
    private Socket socket;
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;
    private ObjectOutputStream remoteOutput;
    private boolean connected;

    private Pattern connectPattern;
    private Pattern disconnectPattern;
    private Pattern loginPattern;
    private Pattern logoutPattern;
    private Pattern joinPattern;
    private Pattern leavePattern;
    private Pattern sendPattern;
    private Pattern sleepPattern;

    public ChatClient(BufferedReader localInput, PrintWriter localOutput) {
        this.responseHandler = null;
        this.localInput = localInput;
        this.localOutput = localOutput;
        this.socket = null;
        this.pendingResponses = new LinkedBlockingQueue<ChatServerResponse>();
        this.connected = false;

        this.connectPattern = Pattern.compile("^connect ([^:\\s]+):(\\d{1,5})$");
        this.disconnectPattern = Pattern.compile("^disconnect$");
        this.loginPattern = Pattern.compile("^login ([^\\s]+)$");
        this.logoutPattern = Pattern.compile("^logout$");
        this.joinPattern = Pattern.compile("^join ([^\\s]+)$");
        this.leavePattern = Pattern.compile("^leave ([^\\s]+)$");
        this.sendPattern = Pattern.compile("^send ([^\\s]+) (\\d+) \"([^\"]+)\"$");
        this.sleepPattern = Pattern.compile("^sleep (\\d+)$");
    }

    public static void main(String[] args) {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
   }

    public void run() {
        try {
            String command = localInput.readLine();
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
            case COMMAND_NOT_FOUND:
                break;
        }
    }

    public void sendCommand(ChatClientCommand command) {}

    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            remoteOutput = new ObjectOutputStream(socket.getOutputStream());
            connected = true;
            responseHandler = new ChatClientResponseHandler(this, socket, pendingResponses);
            responseHandler.start();
            localOutput.println("connect OK");
        } catch (Exception e) {
            localOutput.println("connect REJECTED");
        }
    }

    public void disconnect() {
        if (connected) {
            connected = false;
            try {
                remoteOutput.close();
                remoteOutput = null;
                socket.close();
                socket = null;
                responseHandler.shutdown();
                responseHandler.join();
                responseHandler = null;
            } catch (Exception e) {
            }
        }
        localOutput.println("disconnect OK");
    }

    public void sleep(int time) {
        sleep(time);
        localOutput.println("sleep " + Integer.toString(time) + " OK");
    }
}

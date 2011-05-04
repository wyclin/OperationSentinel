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
    private Pattern addUserPattern;
    private Pattern loginPattern;
    private Pattern logoutPattern;
    private Pattern joinPattern;
    private Pattern leavePattern;
    private Pattern sendPattern;
    private Pattern readlogPattern;
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
        this.addUserPattern = Pattern.compile("^adduser ([^\\s]+) ([^\\s]+)$");
        this.loginPattern = Pattern.compile("^login ([^\\s]+) ([^\\s]+)$");
        this.logoutPattern = Pattern.compile("^logout$");
        this.joinPattern = Pattern.compile("^join ([^\\s]+)$");
        this.leavePattern = Pattern.compile("^leave ([^\\s]+)$");
        this.sendPattern = Pattern.compile("^send ([^\\s]+) (\\d+) \"([^\"]+)\"$");
        this.readlogPattern = Pattern.compile("^readlog$");
        this.sleepPattern = Pattern.compile("^sleep (\\d+)$");
    }

    public static void main(String[] args) {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
   }

    public void run() {
        String commandString;
        ChatClientCommand command;
        ChatServerResponse pendingResponse = null;
        try {
            commandString = localInput.readLine();
            while (commandString != null) {
                pendingResponse = pendingResponses.poll();
                while (pendingResponse != null) {
                    printResponse(pendingResponse);
                    pendingResponse = pendingResponses.poll();
                }

                command = parseCommand(commandString);
                executeCommand(command);

                if (mustWait(command)) {
                    try {
                        pendingResponse = pendingResponses.take();
                    } catch (Exception e) {
                    }
                    while (!isResponse(command, pendingResponse)) {
                        printResponse(pendingResponse);
                        try {
                            pendingResponse = pendingResponses.take();
                        } catch (Exception e) {
                        }
                    }
                    printResponse(pendingResponse);
                }

                commandString = localInput.readLine();
            }
            try {
                pendingResponse = pendingResponses.take();
            } catch (InterruptedException f) {
            }
            while (true) {
                printResponse(pendingResponse);
                try {
                    pendingResponse = pendingResponses.take();
                } catch (InterruptedException f) {
                }
            }
        } catch (IOException e) {
        }
    }

    public boolean isResponse(ChatClientCommand command, ChatServerResponse response) {
        // Weak test, but should do fine
        return response.command != null && response.command.commandType == command.commandType;
    }

    public void printResponse(ChatServerResponse response) {
        if (response.command == null) { // async
            switch (response.responseType) {
                case USER_LOGGED_IN:
                    localOutput.println("login OK");
                    break;
                case USER_LOGGED_OUT:
                    localOutput.println("logout OK");
                    break;
                case MESSAGE_RECEIVED:
                    localOutput.println("receive " + Long.toString(response.messageDate.getTime()/1000) + "." + Long.toString(response.messageDate.getTime() % 1000) + " " + response.messageSender + " " + response.messageReceiver + " \"" + response.messageText + "\"");
                    break;
                case MESSAGE_DELIVERY_FAILURE:
                    localOutput.println("sendack " + Integer.toString(response.messagesqn) + " FAILED");
                    break;
            }
        } else { // Sync
            switch (response.command.commandType) {
                case ADDUSER:
                    switch (response.responseType) {
                        case USER_ADDED:
                            localOutput.println("adduser OK");
                            break;
                        case SHUTTING_DOWN:
                        case DATABASE_FAILURE:
                        case NAME_CONFLICT:
                            localOutput.println("adduser REJECTED");
                            break;
                    }
                    break;
                case LOGIN:
                    switch (response.responseType) {
                        case USER_LOGGED_IN:
                            localOutput.println("login OK");
                            break;
                        case SHUTTING_DOWN:
                        case DATABASE_FAILURE:
                        case NAME_CONFLICT:
                        case INVALID_NAME_OR_PASSWORD:
                        case USER_ALREADY_LOGGED_IN:
                            localOutput.println("login REJECTED");
                            break;
                    }
                    break;
                case LOGOUT:
                    localOutput.println("logout OK");
                    break;
                case JOIN_GROUP:
                    switch (response.responseType) {
                        case USER_ADDED_TO_NEW_GROUP:
                            localOutput.println("join " + response.command.string1 + " OK_CREATE");
                            break;
                        case USER_ADDED_TO_GROUP:
                        case USER_ALREADY_MEMBER_OF_GROUP:
                            localOutput.println("join " + response.command.string1 + " OK_JOIN");
                            break;
                        case NAME_CONFLICT:
                            localOutput.println("join " + response.command.string1 + " BAD_GROUP");
                            break;
                        case SHUTTING_DOWN: // Unofficial
                        case DATABASE_FAILURE:
                            localOutput.println("join " + response.command.string1 + " FAIL");
                            break;
                    }
                    break;
                case LEAVE_GROUP:
                    switch (response.responseType) {
                        case USER_REMOVED_FROM_GROUP:
                            localOutput.println("leave " + response.command.string1 + " OK");
                            break;
                        case USER_NOT_MEMBER_OF_GROUP:
                            localOutput.println("leave " + response.command.string1 + " NOT_MEMBER");
                            break;
                        case GROUP_NOT_FOUND:
                            localOutput.println("leave " + response.command.string1 + " BAD_GROUP");
                            break;
                        case DATABASE_FAILURE:
                            localOutput.println("leave " + response.command.string1 + " FAIL");
                            break;
                    }
                    break;
                case SEND_MESSAGE:
                    switch (response.responseType) {
                        case MESSAGE_ENQUEUED:
                            localOutput.println("send " + response.command.number1 + " OK");
                            break;
                        case RECEIVER_NOT_FOUND:
                        case USER_NOT_MEMBER_OF_GROUP:
                        case RECEIVER_SAME_AS_SENDER:
                            localOutput.println("send " + response.command.number1 + " BAD_DEST");
                            break;
                        case SHUTTING_DOWN:
                        case DATABASE_FAILURE:
                        case MESSAGE_BUFFER_FULL:
                        case SENDER_NOT_FOUND:
                            localOutput.println("send " + response.command.number1 + " FAIL");
                            break;
                    }
                    break;
            }
        }
    }

    public ChatClientCommand parseCommand(String command) {
        Matcher connectMatcher = connectPattern.matcher(command);
        Matcher disconnectMatcher = disconnectPattern.matcher(command);
        Matcher addUserMatcher = addUserPattern.matcher(command);
        Matcher loginMatcher = loginPattern.matcher(command);
        Matcher logoutMatcher = logoutPattern.matcher(command);
        Matcher joinMatcher = joinPattern.matcher(command);
        Matcher leaveMatcher = leavePattern.matcher(command);
        Matcher sendMatcher = sendPattern.matcher(command);
        Matcher readlogMatcher = readlogPattern.matcher(command);
        Matcher sleepMatcher = sleepPattern.matcher(command);
        if (connectMatcher.matches()) {
            return new ChatClientCommand(CommandType.CONNECT, connectMatcher.group(1), Integer.valueOf(connectMatcher.group(2)));
        } else if (disconnectMatcher.matches()) {
            return new ChatClientCommand(CommandType.DISCONNECT);
        } else if (addUserMatcher.matches()) {
            return new ChatClientCommand(CommandType.ADDUSER, addUserMatcher.group(1), addUserMatcher.group(2));
        } else if (loginMatcher.matches()) {
            return new ChatClientCommand(CommandType.LOGIN, loginMatcher.group(1), loginMatcher.group(2));
        } else if (logoutMatcher.matches()) {
            return new ChatClientCommand(CommandType.LOGOUT);
        } else if (joinMatcher.matches()) {
            return new ChatClientCommand(CommandType.JOIN_GROUP, joinMatcher.group(1));
        } else if (leaveMatcher.matches()) {
            return new ChatClientCommand(CommandType.LEAVE_GROUP, leaveMatcher.group(1));
        } else if (sendMatcher.matches()) {
            return new ChatClientCommand(CommandType.SEND_MESSAGE, sendMatcher.group(1), Integer.valueOf(sendMatcher.group(2)), sendMatcher.group(3));
        } else if (readlogMatcher.matches()) {
            return new ChatClientCommand(CommandType.READLOG);
        } else if (sleepMatcher.matches()) {
            return new ChatClientCommand(CommandType.SLEEP, Integer.valueOf(sleepMatcher.group(1)));
        } else {
            return new ChatClientCommand(CommandType.COMMAND_NOT_FOUND);
        }
    }

    public boolean mustWait(ChatClientCommand command) {
        switch (command.commandType) {
            case CONNECT:
            case READLOG:
            case SLEEP:
            case COMMAND_NOT_FOUND:
                return false;
            default:
                return connected;
        }
    }

    public void executeCommand(ChatClientCommand command) {
        switch (command.commandType) {
            case CONNECT:
                connect(command.string1, command.number1);
                break;
            case DISCONNECT:
                if (connected) {
                    sendCommand(command);
                    ChatServerResponse pendingResponse = null;
                    try {
                        pendingResponse = pendingResponses.take();
                    } catch (Exception e) {
                    }
                    while (pendingResponse.responseType != ResponseType.DISCONNECT) {
                        printResponse(pendingResponse);
                        try {
                            pendingResponse = pendingResponses.take();
                        } catch (Exception e) {
                        }
                    }
                    printResponse(pendingResponse);
                }
                disconnect();
                break;
            case SLEEP:
                sleep(command.number1);
                break;
            case ADDUSER:
            case LOGIN:
            case LOGOUT:
            case JOIN_GROUP:
            case LEAVE_GROUP:
            case SEND_MESSAGE:
            case READLOG:
                if (connected) {
                    sendCommand(command);
                }
                break;
            case COMMAND_NOT_FOUND:
                break;
        }
    }

    public void sendCommand(ChatClientCommand command) {
        try {
            remoteOutput.writeObject(command);
            remoteOutput.flush();
        } catch (Exception e) {}
    }

    public void connect(String host, int port) {
        if (connected) {
            disconnect();
        }
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
                responseHandler.interrupt();
                responseHandler.join();
                responseHandler = null;
            } catch (Exception e) {
            }
        }
        localOutput.println("disconnect OK");
    }

    public void sleep(int time) {
        localOutput.println("sleep " + Integer.toString(time) + " OK");
        try {
            Thread.sleep(time);
        } catch (Exception e) {
        }
    }
}

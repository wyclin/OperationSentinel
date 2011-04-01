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
                    while (pendingResponse.responseType != ResponseType.TIMEOUT && !isResponse(command, pendingResponse)) {
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
        if (response.command == null) {
            switch (response.responseType) {
                case USER_ADDED:
                    localOutput.println("login OK");
                    break;
                case USER_REMOVED:
                    localOutput.println("logout OK");
                    break;
                case USER_REMOVED_FROM_GROUP:
                    localOutput.println("leave " + response.string + " OK");
                    break;
                case MESSAGE_RECEIVED:
                    localOutput.println("receive " + response.messageSender + " " + response.messageReceiver + " \"" + response.messageText + "\"");
                    break;
                case MESSAGE_DELIVERY_FAILURE:
                    localOutput.println("sendack " + Integer.toString(response.messagesqn) + " FAILED");
                    break;
                case TIMEOUT:
                    localOutput.println("timeout");
                    break;
            }
        } else {
            switch (response.command.commandType) {
                case LOGIN:
                    switch (response.responseType) {
                        case USER_ADDED:
                            localOutput.println("login OK");
                            break;
                        case USER_QUEUED:
                            localOutput.println("login QUEUED");
                            break;
                        case SHUTTING_DOWN:
                        case USER_CAPACITY_REACHED:
                        case NAME_CONFLICT:
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
                            localOutput.println("join " + response.command.string1 + " OK_JOIN");
                            break;
                        case NAME_CONFLICT:
                            localOutput.println("join " + response.command.string1 + " BAD_GROUP");
                            break;
                        case GROUP_CAPACITY_REACHED:
                            localOutput.println("join " + response.command.string1 + " FAIL_FULL");
                            break;
                        case SHUTTING_DOWN:
                        case USER_ALREADY_MEMBER_OF_GROUP:
                            localOutput.println("join " + response.command.string1 + " OK_JOIN");
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
                    }
                    break;
                case SEND_MESSAGE:
                    switch (response.responseType) {
                        case MESSAGE_ENQUEUED:
                            localOutput.println("send " + response.command.number + " OK");
                            break;
                        case RECEIVER_NOT_FOUND:
                        case USER_NOT_MEMBER_OF_GROUP:
                        case RECEIVER_SAME_AS_SENDER:
                            localOutput.println("send " + response.command.number + " BAD_DEST");
                            break;
                        case SHUTTING_DOWN:
                        case MESSAGE_BUFFER_FULL:
                        case SENDER_NOT_FOUND:
                            localOutput.println("send " + response.command.number + " FAIL");
                            break;
                    }
                    break;
            }
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

    public boolean mustWait(ChatClientCommand command) {
        switch (command.commandType) {
            case CONNECT:
            case DISCONNECT:
            case COMMAND_NOT_FOUND:
            case SLEEP:
                return false;
            default:
                return true;
        }
    }

    public void executeCommand(ChatClientCommand command) {
        switch (command.commandType) {
            case CONNECT:
                connect(command.string1, command.number);
                break;
            case DISCONNECT:
                sendCommand(command);
                ChatServerResponse pendingResponse = null;
                try {
                    pendingResponse = pendingResponses.take();
                } catch (Exception e) {
                }
                while (pendingResponse.responseType != ResponseType.TIMEOUT && pendingResponse.responseType != ResponseType.DISCONNECT) {
                    printResponse(pendingResponse);
                    try {
                        pendingResponse = pendingResponses.take();
                    } catch (Exception e) {
                    }
                }
                printResponse(pendingResponse);
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

    public void sendCommand(ChatClientCommand command) {
        try {
            remoteOutput.writeObject(command);
            remoteOutput.flush();
        } catch (Exception e) {
            // Failure message?
        }
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

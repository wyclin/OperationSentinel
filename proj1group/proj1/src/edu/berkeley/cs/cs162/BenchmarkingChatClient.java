package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.*;

public class BenchmarkingChatClient extends Thread {

    private ChatClientResponseHandler responseHandler;
    private BufferedReader localInput;
    private PrintWriter localOutput;
    private Socket socket;
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;
    private ObjectOutputStream remoteOutput;
    private boolean connected;

    private HashMap<Integer, Long> messagesSent;
    private ArrayList<Long> roundTripTimes;
    private int clientID;
    private Pattern printRTTPattern;

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

    public BenchmarkingChatClient(BufferedReader localInput, PrintWriter localOutput) {
        this.responseHandler = null;
        this.localInput = localInput;
        this.localOutput = localOutput;
        this.socket = null;
        this.pendingResponses = new LinkedBlockingQueue<ChatServerResponse>();
        this.connected = false;

        this.messagesSent = new HashMap<Integer, Long>();
        this.roundTripTimes = new ArrayList<Long>();
        this.clientID = (int)(Math.random()*Integer.MAX_VALUE);
        this.printRTTPattern = Pattern.compile("^print RTT$");

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
        if (response.command == null) { // async
            switch (response.responseType) {
                case USER_LOGGED_IN:
                    localOutput.println("login OK");
                    break;
                case USER_LOGGED_OUT:
                    localOutput.println("logout OK");
                    break;
                case MESSAGE_RECEIVED:
                    localOutput.println("receive " + response.messageSender + " " + response.messageReceiver + " \"" + response.messageText + "\"");
                    if (OriginatedFromMe(response))
                        finishTimingMessage(response);
                    else
                        returnMessageBackToSender(response);
                    break;
                case MESSAGE_DELIVERY_FAILURE:
                    localOutput.println("sendack " + Integer.toString(response.messagesqn) + " FAILED");
                    break;
                case TIMEOUT:
                    localOutput.println("timeout");
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
                        case USER_QUEUED:
                            localOutput.println("login QUEUED");
                            break;
                        case SHUTTING_DOWN:
                        case DATABASE_FAILURE:
                        case USER_CAPACITY_REACHED:
                        case NAME_CONFLICT:
                        case INVALID_NAME_OR_PASSWORD:
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
                            localOutput.println("send " + response.command.number + " OK");
                            break;
                        case RECEIVER_NOT_FOUND:
                        case USER_NOT_MEMBER_OF_GROUP:
                        case RECEIVER_SAME_AS_SENDER:
                            localOutput.println("send " + response.command.number + " BAD_DEST");
                            break;
                        case SHUTTING_DOWN:
                        case DATABASE_FAILURE:
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
        Matcher addUserMatcher = addUserPattern.matcher(command);
        Matcher loginMatcher = loginPattern.matcher(command);
        Matcher logoutMatcher = logoutPattern.matcher(command);
        Matcher joinMatcher = joinPattern.matcher(command);
        Matcher leaveMatcher = leavePattern.matcher(command);
        Matcher sendMatcher = sendPattern.matcher(command);
        Matcher readlogMatcher = readlogPattern.matcher(command);
        Matcher sleepMatcher = sleepPattern.matcher(command);
        Matcher printRTTMatcher = printRTTPattern.matcher(command);
        if (connectMatcher.matches()) {
            return new ChatClientCommand(CommandType.CONNECT, connectMatcher.group(1), Integer.valueOf(connectMatcher.group(2)));
        } else if (disconnectMatcher.matches()) {
            return new ChatClientCommand(CommandType.DISCONNECT);
        } else if (addUserMatcher.matches()) {
            return new ChatClientCommand(CommandType.ADDUSER, addUserMatcher.group(1), addUserMatcher.group(2));
        } else if (loginMatcher.matches()) {
            return new ChatClientCommand(CommandType.LOGIN, loginMatcher.group(1), encrypt(loginMatcher.group(2)));
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
        } else if (printRTTMatcher.matches()) {
            printRoundTripTimes();
            return new ChatClientCommand(CommandType.COMMAND_NOT_FOUND);
        }
        else {
            return new ChatClientCommand(CommandType.COMMAND_NOT_FOUND);
        }
    }

    public boolean mustWait(ChatClientCommand command) {
        switch (command.commandType) {
            case CONNECT:
            case DISCONNECT:
            case READLOG:
            case SLEEP:
            case COMMAND_NOT_FOUND:
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
            case SEND_MESSAGE:
                startTimingMessage(command);
                sendCommand(command);
                break;
            case ADDUSER:
            case LOGIN:
            case LOGOUT:
            case JOIN_GROUP:
            case LEAVE_GROUP:
            case READLOG:
                sendCommand(command);
                break;
            case COMMAND_NOT_FOUND:
                break;
        }
    }

    private boolean OriginatedFromMe(ChatServerResponse response) {
        String msgtext = response.messageText;
        String lastWordOfMessage = msgtext.substring(msgtext.lastIndexOf(" ") + 1);
        if ((messagesSent.get(Integer.valueOf(response.messagesqn)) != null) && (Integer.valueOf(lastWordOfMessage) == clientID))
            return true;
        return false;
    }

    private void returnMessageBackToSender(ChatServerResponse response) {
        String msgtext = response.messageText + " " + Integer.toString(clientID);
        ChatClientCommand command = new ChatClientCommand(CommandType.SEND_MESSAGE, response.messageSender, response.messagesqn, msgtext);
        sendCommand(command);
    }

    private void startTimingMessage(ChatClientCommand command) {
        messagesSent.put(Integer.valueOf(command.number), new Long(System.currentTimeMillis()));
    }

    private void finishTimingMessage(ChatServerResponse response) {
        try {
            Long startTime = messagesSent.get(Integer.valueOf(response.messagesqn));
            long timeElapsed = System.currentTimeMillis() - startTime.longValue();
            roundTripTimes.add(new Long(timeElapsed));
            messagesSent.remove(Integer.valueOf(response.messagesqn));
        } catch (Exception e) {}
    }

    public void printRoundTripTimes() {
        try {
            //FileWriter file = new FileWriter("~/roundtriptimes_" + clientID + ".txt");
            //BufferedWriter output = new BufferedWriter(file);
            //output.write("Round Trip Times for BenchmarkingChatClient " + clientID + " (in milliseconds):\n");
            System.err.println("Round Trip Times for BenchmarkingChatClient " + clientID + " (in milliseconds):\n");
            for (Long timeElapsed : roundTripTimes) {
                //output.write(timeElapsed.longValue() + "\n");
                System.err.println(timeElapsed.longValue() + "\n");
            }
            //file.close();
        } catch (Exception e) {
            System.err.println("ERROR - COULD NOT WRITE RTT TO FILE");
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

    private String encrypt(String password) {
        // currently a stub method that doesn't encrypt
        return password;
    }
}

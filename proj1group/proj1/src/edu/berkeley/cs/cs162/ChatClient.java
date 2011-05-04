package edu.berkeley.cs.cs162;

import edu.berkeley.cs.cs162.hash.ConsistentHash;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.*;

public class ChatClient extends Thread {

    private ChatClientCommandHandler commandHandler;
    private ChatClientResponseHandler responseHandler;
    private DatabaseManager databaseManager;
    private BufferedReader localInput;
    private PrintWriter localOutput;
    private Socket socket;
    private LinkedBlockingQueue<String> pendingCommands;
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;
    private ObjectOutputStream remoteOutput;
    private boolean connected;
    private AtomicBoolean reconnectFlag;
    private ConsistentHash<String> consistentHash;
    private HashMap<String, HashMap<String, Object>> servers;

    private String userName;
    private String password;

    private Pattern loginPattern;
    private Pattern logoutPattern;
    private Pattern joinPattern;
    private Pattern leavePattern;
    private Pattern sendPattern;
    private Pattern readlogPattern;
    private Pattern sleepPattern;

    public ChatClient(BufferedReader localInput, PrintWriter localOutput) {
        this.databaseManager = new DatabaseManager();
        this.responseHandler = null;
        this.localInput = localInput;
        this.localOutput = localOutput;
        this.socket = null;
        this.pendingCommands = new LinkedBlockingQueue<String>();
        this.pendingResponses = new LinkedBlockingQueue<ChatServerResponse>();
        this.connected = false;
        this.reconnectFlag = new AtomicBoolean(false);

        this.commandHandler = new ChatClientCommandHandler(this.localInput, this.pendingCommands);
        this.commandHandler.start();

        this.consistentHash = new ConsistentHash<String>();
        this.servers = new HashMap<String, HashMap<String, Object>>();
        updateServers();

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
        ChatServerResponse response;
        while(true) {
            response = pendingResponses.poll();
            while (response != null) {
                printResponse(response);
                response = pendingResponses.poll();
            }

            try {
                commandString = pendingCommands.poll();
                if (commandString != null) {
                    command = parseCommand(commandString);
                    executeCommand(command);
                    if (mustWait(command)) {
                        response = pendingResponses.take();
                        while (!isResponse(command, response)) {
                            printResponse(response);
                            response = pendingResponses.take();
                        }
                        printResponse(response);
                    }
                }
            } catch (InterruptedException e) {}

            if (reconnectFlag.getAndSet(false)) {
                disconnect();
                updateServers();
                login(userName,  password);
            }
        }
    }

    public void updateServers() {
        servers.clear();
        consistentHash.clear();
        try {
            LinkedList<HashMap<String, Object>> serverList = databaseManager.getServerList();
            for (HashMap<String, Object> server : serverList) {
                try {
                    Socket socket = new Socket((String)server.get("host"), (Integer)server.get("port"));
                    socket.close();
                    servers.put((String)server.get("name"), server);
                    consistentHash.add((String)server.get("name"));
                } catch (Exception f) {}
            }
        } catch (SQLException e) {}
    }

    public void flagReconnect() {
        reconnectFlag.set(true);
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
                case INTERRUPT:
                    break;
            }
        } else { // Sync
            switch (response.command.commandType) {
             /*
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
                */
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
        Matcher loginMatcher = loginPattern.matcher(command);
        Matcher logoutMatcher = logoutPattern.matcher(command);
        Matcher joinMatcher = joinPattern.matcher(command);
        Matcher leaveMatcher = leavePattern.matcher(command);
        Matcher sendMatcher = sendPattern.matcher(command);
        Matcher readlogMatcher = readlogPattern.matcher(command);
        Matcher sleepMatcher = sleepPattern.matcher(command);
        if (loginMatcher.matches()) {
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
            case LOGIN:
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
            case SLEEP:
                sleep(command.number1);
                break;
            case ADDUSER:
            case LOGIN:
                login(command.string1, command.string2);
                break;
            case LOGOUT:
                logout();
                break;
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

    public void login(String userName, String password) {
        if (connected) {
            disconnect();
        }

        // Connect
        HashMap<String, Object> serverProperties = servers.get(consistentHash.get(userName));
        if (serverProperties == null) {
            localOutput.println("connect REJECTED");
            return;
        }
        try {
            socket = new Socket((String)serverProperties.get("host"), (Integer)serverProperties.get("port"));
            remoteOutput = new ObjectOutputStream(socket.getOutputStream());
            connected = true;
            responseHandler = new ChatClientResponseHandler(this, socket, pendingResponses);
            responseHandler.start();
            localOutput.println("connect OK");
        } catch (Exception e) {
            localOutput.println("connect REJECTED");
            return;
        }

        // Add User
        sendCommand(new ChatClientCommand(CommandType.ADDUSER, userName, password));
        ChatServerResponse response;
        try {
            response = pendingResponses.take();
            if (response.responseType == ResponseType.USER_ADDED) {
                localOutput.println("adduser OK");
            }
        } catch (InterruptedException e) {
            localOutput.println("adduser REJECTED");
            disconnect();
            return;
        }

        // Login
        sendCommand(new ChatClientCommand(CommandType.LOGIN, userName, password));
        try {
            response = pendingResponses.take();
            if (response.responseType == ResponseType.USER_LOGGED_IN) {
                this.userName = userName;
                this.password = password;
                localOutput.println("login OK");
            } else {
                localOutput.println("login REJECTED");
                disconnect();
            }
        } catch (InterruptedException e) {
            localOutput.println("login REJECTED");
            disconnect();
        }
    }

    public void logout() {
        if (connected) {
            sendCommand(new ChatClientCommand(CommandType.DISCONNECT));
            ChatServerResponse response = null;
            try {
                response = pendingResponses.take();
                while (response.responseType != ResponseType.DISCONNECT) {
                    printResponse(response);
                    response = pendingResponses.take();
                }
                printResponse(response);
            } catch (InterruptedException e) {}
        }
        disconnect();
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
            } catch (Exception e) {}
        }
        while (pendingResponses.peek() != null && pendingResponses.peek().responseType == ResponseType.INTERRUPT) {
            pendingResponses.poll();
        }
        reconnectFlag.set(false);
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

package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatUser extends Thread {

    private ChatServer chatServer;
    private ChatUserResponder responder;
    private Socket socket;
    private ObjectInputStream input;
    private boolean networked;
    private String loginName;
    private boolean loggedIn;
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;
    private LinkedBlockingQueue<String> log;
    private SimpleDateFormat dateFormatter;

    public ChatUser(ChatServer chatServer) {
        this.chatServer = chatServer;
        this.networked = false;
        this.loginName = null;
        this.loggedIn = false;
        this.pendingResponses = new LinkedBlockingQueue<ChatServerResponse>();
        this.log = new LinkedBlockingQueue<String>();
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    public ChatUser(ChatServer chatServer, Socket socket) {
        this(chatServer);
        this.networked = true;
        this.socket = socket;
        this.responder = new ChatUserResponder(this, socket, pendingResponses, log);
        try {
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            try {
                socket.close();
            } catch(IOException f) {}
        }
    }

    public ChatServer getChatServer() {
        return chatServer;
    }

    public void start() {
        responder.start();
        super.start();
        log.offer(dateFormatter.format(Calendar.getInstance().getTime()) + " | Client Connected");
    }

    public void shutdown() {
        log.offer(dateFormatter.format(Calendar.getInstance().getTime()) + " | Shutting Down");
        if (networked) {
            responder.shutdown();
        }
    }

    public void run() {
        try {
            while (true) {
                executeCommand((ChatClientCommand)input.readObject());
            }
        } catch (Exception e) {
        } finally {
            forceDisconnect();
            try {
                input.close();
                socket.close();
                responder.interrupt();
            } catch (Exception f) {
            }
        }
    }

    public String getUserName() {
        return loginName;
    }

    public void setUserName(String userName) {
        loginName = userName;
    }

    public void executeCommand(ChatClientCommand command) {
        ChatServerResponse response;
        switch (command.commandType) {
            case MIGRATE:
                migrate();
                return;
            case DISCONNECT:
                disconnect();
                return;
            case ADDUSER:
                response = addUser(command.string1, command.string2);
                break;
            case LOGIN:
                response = login(command.string1, command.string2);
                break;
            case LOGOUT:
                response = logout();
                break;
            case JOIN_GROUP:
                response = joinGroup(command.string1);
                break;
            case LEAVE_GROUP:
                response = leaveGroup(command.string1);
                break;
            case SEND_MESSAGE:
                response = sendMessage(command.string1, command.number1, command.string2);
                break;
            case READLOG:
                readLog();
                return;
            default:
                response = new ChatServerResponse(ResponseType.COMMAND_NOT_FOUND);
                break;
        }
        response.command = command;
        pendingResponses.offer(response);
    }

    public void migrate() {
        if (loggedIn) {
            chatServer.migrate(this);
        }
        pendingResponses.offer(new ChatServerResponse(ResponseType.MIGRATE));
        log.offer(dateFormatter.format(Calendar.getInstance().getTime()) + " | Client Migrated");
        shutdown();
    }

    public void disconnect() {
        if (loggedIn) {
            pendingResponses.offer(logout());
        }
        pendingResponses.offer(new ChatServerResponse(ResponseType.DISCONNECT));
        log.offer(dateFormatter.format(Calendar.getInstance().getTime()) + " | Client Disconnected");
        shutdown();
    }

    public void forceDisconnect() {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            chatServer.logout(this);
            log.offer(dateFormatter.format(time) + " | " + loginName + " has been force logged out.");
            loggedIn = false;
        }
        log.offer(dateFormatter.format(time) + " | Client force disconnected.");
    }

    public ChatServerResponse addUser(String userName, String password) {
        Date time = Calendar.getInstance().getTime();
        ChatServerResponse response = chatServer.addUser(userName, password);
        switch (response.responseType) {
            case SHUTTING_DOWN:
                log.offer(dateFormatter.format(time) + " | Register User Failure | ChatServer is shutting down.");
                break;
            case NAME_CONFLICT:
                log.offer(dateFormatter.format(time) + " | Register User Failure | Name already taken.");
                break;
            case USER_ADDED:
                log.offer(dateFormatter.format(time) + " | Register User Success | Registered as " + userName);
                break;
        }
        return response;
    }

    public ChatServerResponse login(String userName, String password) {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {logout();}
        loginName = userName;
        ChatServerResponse response = chatServer.login(this, password);
        switch (response.responseType) {
            case SHUTTING_DOWN:
                log.offer(dateFormatter.format(time) + " | Login Failure | ChatServer is shutting down.");
                break;
            case DATABASE_FAILURE:
                log.offer(dateFormatter.format(time) + " | Login Failure | Database Failure.");
            case NAME_CONFLICT:
                log.offer(dateFormatter.format(time) + " | Login Failure | Name already taken.");
                break;
            case INVALID_NAME_OR_PASSWORD:
                log.offer(dateFormatter.format(time) + " | Login Failure | Wrong username and/or password.");
                break;
            case USER_LOGGED_IN:
                loggedIn = true;
                log.offer(dateFormatter.format(time) + " | Login Success | Logged in as " + userName);
                break;
        }
        return response;
    }

    public ChatServerResponse logout() {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            ChatServerResponse response = chatServer.logout(this);
            switch (response.responseType) {
                case USER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Logout Failure | " + loginName + " is not logged in.");
                    break;
                case USER_LOGGED_OUT:
                    log.offer(dateFormatter.format(time) + " | Logout Success | " + loginName + " has been logged out.");
                    break;
            }
            loggedIn = false;
            return response;
        } else {
            log.offer(dateFormatter.format(time) + " | Logout Failure | Not logged in.");
            return new ChatServerResponse(ResponseType.USER_NOT_FOUND);
        }
    }

    public ChatServerResponse joinGroup(String groupName) {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            ChatServerResponse response = chatServer.joinGroup(this, groupName);
            switch (response.responseType) {
                case SHUTTING_DOWN:
                    log.offer(dateFormatter.format(time) + " | Join Group Failure | ChatServer is shutting down.");
                    break;
                case USER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Join Group Failure | " + loginName + " is not logged in.");
                    loggedIn = false;
                    break;
                case USER_ADDED_TO_NEW_GROUP:
                    log.offer(dateFormatter.format(time) + " | Join Group Success | " + loginName + " has joined newly created group " + groupName + ".");
                    break;
                case USER_ALREADY_MEMBER_OF_GROUP:
                    log.offer(dateFormatter.format(time) + " | Join Group Failure | " + loginName + " has already joined " + groupName + ".");
                    break;
                case USER_ADDED_TO_GROUP:
                    log.offer(dateFormatter.format(time) + " | Join Group Success | " + loginName + " has joined group " + groupName + ".");
                    break;
            }
            return response;
        } else {
            log.offer(dateFormatter.format(time) + " | Join Group Failure | Not logged in.");
            return new ChatServerResponse(ResponseType.USER_NOT_FOUND);
        }
    }

    public ChatServerResponse leaveGroup(String groupName) {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            ChatServerResponse response = chatServer.leaveGroup(this, groupName);
            switch (response.responseType) {
                case USER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Leave Group Failure | " + loginName + " is not logged in.");
                    break;
                case GROUP_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Leave Group Failure | group " + groupName + " does not exist.");
                    break;
                case USER_NOT_MEMBER_OF_GROUP:
                    log.offer(dateFormatter.format(time) + " | Leave Group Failure | " + loginName + " is not a member of " + groupName + ".");
                    break;
                case USER_REMOVED_FROM_GROUP:
                    log.offer(dateFormatter.format(time) + " | Leave Group Success | " + loginName + " has left group " + groupName + ".");
                    break;
            }
            return response;
        } else {
            log.offer(dateFormatter.format(time) + " | Leave Group Failure | Not logged in.");
            return new ChatServerResponse(ResponseType.USER_NOT_FOUND);
        }
    }

    public ChatServerResponse sendMessage(String receiver, int sqn, String messageText) {
        Date time = Calendar.getInstance().getTime();
        Message message = new Message(time, this, receiver, sqn, messageText);
        log.offer(dateFormatter.format(time) + " | Sending Message | " + loginName + " (" + sqn + ") -> " + receiver + " | " + message.text);
        if (loggedIn) {
            ChatServerResponse response = chatServer.send(message);
            switch (response.responseType) {
                case SHUTTING_DOWN:
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + sqn + ") -> " + receiver + " | ChatServer is shutting down.");
                    break;
                case MESSAGE_ENQUEUED:
                    log.offer(dateFormatter.format(time) + " | Message Queue Success | " + loginName + " (" + sqn + ") -> " + receiver + ".");
                    break;
                case MESSAGE_BUFFER_FULL:
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + sqn + ") -> " + receiver + " | Buffer full.");
                    break;
                case SENDER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + sqn + ") -> " + receiver + " | Sender not found.");
                    break;
                case RECEIVER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + sqn + ") -> " + receiver + " | Receiver not found.");
                    break;
                case RECEIVER_SAME_AS_SENDER:
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + sqn + ") -> " + receiver + " | Sender same as receiver.");
                    break;
                case USER_NOT_MEMBER_OF_GROUP:
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + sqn + ") -> " + receiver + " | Sender not member of group " + receiver + ".");
                    break;
            }
            return response;
        } else {
            log.offer(dateFormatter.format(time) + " | Message Queue Failure | Not logged in.");
            return new ChatServerResponse(ResponseType.USER_NOT_FOUND);
        }
    }

    public void readLog() {
        Date time = Calendar.getInstance().getTime();
        log.offer(dateFormatter.format(time) + " | Retrieving Offline Messages");
        chatServer.readLog(this);
    }

    public void receiveMessage(Message message) {
        Date time = Calendar.getInstance().getTime();
        log.offer(dateFormatter.format(time) + " | Receiving Message | " + message.senderName + " (" + Integer.toString(message.sqn) + ") -> " + message.receiver + " | " + message.text);
        pendingResponses.offer(new ChatServerResponse(ResponseType.MESSAGE_RECEIVED, message));
    }

    public void receiveSendFailure(Message message) {
        Date time = Calendar.getInstance().getTime();
        log.offer(dateFormatter.format(time) + " | Message Send Failure | " + message.sender.getUserName() + " (" + Integer.toString(message.sqn) + ") -> " + message.receiver + ".");
        pendingResponses.offer(new ChatServerResponse(ResponseType.MESSAGE_DELIVERY_FAILURE, message));
    }

    public void printLog() {
        for (String logMessage : log) {
            System.out.println(logMessage);
        }
    }
}

package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatUser extends Thread {
    private ChatServer chatServer;
    private Socket socket;
    private boolean shuttingDown;
    private String loginName;
    private boolean loggedIn;
    private int sendCount;
    private LinkedBlockingQueue<ChatClientCommand> pendingActions;
    private LinkedBlockingQueue<String> log;
    private SimpleDateFormat dateFormatter;

    public ChatUser(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
        this.shuttingDown = false;
        this.loginName = null;
        this.loggedIn = false;
        this.pendingActions = new LinkedBlockingQueue<ChatClientCommand>();
        this.log = new LinkedBlockingQueue<String>();
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        this.sendCount = 0;
    }

    public ChatUser(ChatServer chatServer) {
        this.chatServer = chatServer;
        this.shuttingDown = false;
        this.loginName = null;
        this.loggedIn = false;
        this.pendingActions = new LinkedBlockingQueue<ChatClientCommand>();
        this.log = new LinkedBlockingQueue<String>();
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        this.sendCount = 0;
    }

    public void shutdown() {
        log.offer(dateFormatter.format(Calendar.getInstance().getTime()) + " | Shutting Down");
        this.shuttingDown = true;
        interrupt();
    }

    public void run() {
        // Handle the above interrupt. Non-blocking sockets?
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String command;
            while (true) { // Handle graceful shutdown
            }
            //output.close();
            //input.close();
            //socket.close();
        } catch (IOException e) {
        }
    }

    public String getUserName() {
        return loginName;
    }

    public void disconnect() {
        log.offer(dateFormatter.format(Calendar.getInstance().getTime()) + " | Disconnected");
    }

    public void executeCommand(ChatClientCommand command) {
        switch (command.commandType) {
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
        }
    }

    public void login(String userName) {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            logout();
        }
        loginName = userName;
        switch (chatServer.login(this).responseType) {
            case SHUTTING_DOWN:         // login REJECTED
                TestChatServer.logUserLoginFailed(userName, time, LoginError.USER_REJECTED);
                log.offer(dateFormatter.format(time) + " | Login Failure | ChatServer is shutting down.");
                break;
            case USER_QUEUED:           // login QUEUED
                log.offer(dateFormatter.format(time) + " | Login Queued | Placed on waiting queue.");
                break;
            case USER_CAPACITY_REACHED: // login REJECTED
                TestChatServer.logUserLoginFailed(userName, time, LoginError.USER_DROPPED);
                log.offer(dateFormatter.format(time) + " | Login Failure | ChatServer is full.");
                break;
            case NAME_CONFLICT:         // login REJECTED
                TestChatServer.logUserLoginFailed(userName, time, LoginError.USER_REJECTED);
                log.offer(dateFormatter.format(time) + " | Login Failure | Name already taken.");
                break;
            case USER_ADDED:            // login OK
                loggedIn = true;
                TestChatServer.logUserLogin(userName, time);
                log.offer(dateFormatter.format(time) + " | Login Success | Logged in as " + userName);
                break;
        }
    }

    public void loggedIn() {
        loggedIn = true;
        Date time = Calendar.getInstance().getTime();
        TestChatServer.logUserLogin(loginName, time);
        log.offer(dateFormatter.format(time) + " | Login Success | Logged in as " + loginName);
    }

    public void logout() {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            switch (chatServer.logoff(this).responseType) {
                case USER_NOT_FOUND: // logout OK
                    log.offer(dateFormatter.format(time) + " | Logout Failure | " + loginName + " is not logged in.");
                    break;
                case USER_REMOVED:   // logout OK
                    TestChatServer.logUserLogout(loginName, time);
                    log.offer(dateFormatter.format(time) + " | Logout Success | " + loginName + " has been logged out.");
                    break;
            }
            loggedIn = false;
        } else {
            log.offer(dateFormatter.format(time) + " | Logout Failure | Not logged in.");
            // logout OK
        }
    }

    public void joinGroup(String groupName) {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            switch (chatServer.joinGroup(this, groupName).responseType) {
                case SHUTTING_DOWN:                // join groupName FAIL - UNOFFICIAL
                    log.offer(dateFormatter.format(time) + " | Join Group Failure | ChatServer is shutting down.");
                    break;
                case USER_NOT_FOUND:               // join groupName FAIL - UNOFFICIAL
                    log.offer(dateFormatter.format(time) + " | Join Group Failure | " + loginName + " is not logged in.");
                    loggedIn = false;
                    break;
                case USER_ADDED_TO_NEW_GROUP:      // join groupName OK_CREATE
                    TestChatServer.logUserJoinGroup(groupName, loginName, time);
                    log.offer(dateFormatter.format(time) + " | Join Group Success | " + loginName + " has joined newly created group " + groupName + ".");
                    break;
                case USER_ALREADY_MEMBER_OF_GROUP: // join groupName BAD_GROUP
                    log.offer(dateFormatter.format(time) + " | Join Group Failure | " + loginName + " has already joined " + groupName + ".");
                    break;
                case GROUP_CAPACITY_REACHED:       // join groupName FAIL_FULL
                    log.offer(dateFormatter.format(time) + " | Join Group Failure | " + groupName + " is full.");
                    break;
                case USER_ADDED_TO_GROUP:          // join groupName OK_JOIN
                    TestChatServer.logUserJoinGroup(groupName, loginName, time);
                    log.offer(dateFormatter.format(time) + " | Join Group Success | " + loginName + " has joined group " + groupName + ".");
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Join Group Failure | Not logged in.");
            // join groupName FAIL - UNOFFICIAL
        }
    }

    public void leaveGroup(String groupName) {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            switch (chatServer.leaveGroup(this, groupName).responseType) {
                case USER_NOT_FOUND:           // leave groupName NOT_MEMBER
                    log.offer(dateFormatter.format(time) + " | Leave Group Failure | " + loginName + " is not logged in.");
                    break;
                case GROUP_NOT_FOUND:          // leave groupName BAD_GROUP
                    log.offer(dateFormatter.format(time) + " | Leave Group Failure | group " + groupName + " does not exist.");
                    break;
                case USER_NOT_MEMBER_OF_GROUP: // leave groupName NOT_MEMBER
                    log.offer(dateFormatter.format(time) + " | Leave Group Failure | " + loginName + " is not a member of " + groupName + ".");
                    break;
                case USER_REMOVED_FROM_GROUP:  // leave groupName OK
                    TestChatServer.logUserLeaveGroup(groupName, loginName, time);
                    log.offer(dateFormatter.format(time) + " | Leave Group success | " + loginName + " has left group " + groupName + ".");
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Leave Group Failure | Not logged in.");
            // leave groupName NOT_MEMBER
        }
    }
	
    public void sendMessage(Message message) {
        Date time = Calendar.getInstance().getTime();
        log.offer(dateFormatter.format(time) + " | Sending Message | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + message.receiver + " | " + message.text);
        if (loggedIn) {
            switch (chatServer.send(message).responseType) {
                case SHUTTING_DOWN:       // send sqn FAIL
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + message.receiver + " | ChatServer is shutting down.");
                    break;
                case MESSAGE_ENQUEUED:    // send sqn OK
                    log.offer(dateFormatter.format(time) + " | Message Queue Success | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + message.receiver + ".");
                    break;
                case MESSAGE_BUFFER_FULL: // send sqn FAIL
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + message.receiver + " | Buffer full.");
                    break;
                case SENDER_NOT_FOUND:    // send sqn BAD_DEST
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + message.receiver + " | Sender not found.");
                    break;
                case RECEIVER_NOT_FOUND:  // send sqn FAIL
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + message.receiver + " | Receiver not found.");
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Message Queue Failure | Not logged in.");
            // send sqn FAIL
        }
        TestChatServer.logUserSendMsg(loginName, message.toString());
    }

    public void sendMessage(String receiver, String messageText) {
        sendMessage(new Message(this, receiver, ++sendCount, messageText));
    }
	
    public void receiveMessage(Message message) {
        Date time = Calendar.getInstance().getTime();
        TestChatServer.logUserMsgRecvd(loginName, message.toString(), time);
        log.offer(dateFormatter.format(time) + " | Receiving Message | " + message.sender.getUserName() + " (" + Integer.toString(message.sqn) + ") -> " + message.receiver + " | " + message.text);
        // receive sender receiver "messageText"
    }

    public void receiveSendFailure(Message message) {
        Date time = Calendar.getInstance().getTime();
        log.offer(dateFormatter.format(time) + " | Message Send Failure | " + message.sender.getUserName() + " (" + Integer.toString(message.sqn) + ") -> " + message.receiver + ".");
        // sendack sqn FAILED
    }

    public void getUserCount() {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            ChatServerResponse response = chatServer.getUserCount(this);
            switch (response.responseType) {
                case USER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Get User Count Failure | " + loginName + " is not logged in.");
                    break;
                case DATA_SENT:
                    log.offer(dateFormatter.format(time) + " | Get User Count Success | Server has " + Integer.toString(response.number) + " users.");
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Get User Count Failure | Not logged in.");
        }
    }

    public void getUserList() {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            ChatServerResponse response = chatServer.getUserList(this);
            switch (response.responseType) {
                case USER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Get User List Failure | " + loginName + " is not logged in.");
                    break;
                case DATA_SENT:
                    String users = "";
                    for (String user : response.treeSet) {
                        users += user + ", ";
                    }
                    log.offer(dateFormatter.format(time) + " | Get User Count Success | Users: " + users);
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Get User Count Failure | Not logged in.");
        }
    }

    public void getGroupCount() {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            ChatServerResponse response = chatServer.getGroupCount(this);
            switch (response.responseType) {
                case USER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Get Group Count Failure | " + loginName + " is not logged in.");
                    break;
                case DATA_SENT:
                    log.offer(dateFormatter.format(time) + " | Get Group Count Success | Server has " + Integer.toString(response.number) + " groups.");
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Get Group Count Failure | Not logged in.");
        }
    }

    public void getGroupList() {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            ChatServerResponse response = chatServer.getGroupList(this);
            switch (response.responseType) {
                case USER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Get Group List Failure | " + loginName + " is not logged in.");
                    break;
                case DATA_SENT:
                    String groups = "";
                    for (String group : response.treeSet) {
                        groups += group + ", ";
                    }
                    log.offer(dateFormatter.format(time) + " | Get Group List Success | Groups: " + groups);
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Get Group List Failure | Not logged in.");
        }
    }

    public void getGroupUserCount(String groupName) {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            ChatServerResponse response = chatServer.getGroupUserCount(this, groupName);
            switch (response.responseType) {
                case USER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Get Group User Count Failure | " + loginName + " is not logged in.");
                    break;
                case GROUP_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Get Group User Count Failure | " + groupName + " not found.");
                    break;
                case DATA_SENT:
                    log.offer(dateFormatter.format(time) + " | Get Group User Count Success | " + groupName + " has " + Integer.toString(response.number) + " users.");
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Get Group User Count Failure | Not logged in.");
        }
    }

    public void getGroupUserList(String groupName) {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            ChatServerResponse response = chatServer.getGroupUserList(this, groupName);
            switch (response.responseType) {
                case USER_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Get Group User List Failure | " + loginName + " is not logged in.");
                    break;
                case GROUP_NOT_FOUND:
                    log.offer(dateFormatter.format(time) + " | Get Group User List Failure | " + groupName + " not found.");
                    break;
                case DATA_SENT:
                    String users = "";
                    for (String user : response.treeSet) {
                        users += user + ", ";
                    }
                    log.offer(dateFormatter.format(time) + " | Get Group User List Success | " + groupName + " Users: " + users);
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Get Group User List Failure | Not logged in.");
        }
    }

    public void printLog() {
        for (String logMessage : log) {
            System.out.println(logMessage);
        }
    }
}

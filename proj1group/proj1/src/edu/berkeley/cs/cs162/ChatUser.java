package edu.berkeley.cs.cs162;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatUser extends Thread {
    private ChatServer chatServer;
    private boolean shuttingDown;
    private String loginName;
    private boolean loggedIn;
    private int sendCount;
    private LinkedBlockingQueue<Action> pendingActions;
    private LinkedBlockingQueue<String> log;
    private SimpleDateFormat dateFormatter;

    public ChatUser(ChatServer chatServer) {
        this.chatServer = chatServer;
        this.shuttingDown = false;
        this.loginName = null;
        this.loggedIn = false;
        this.pendingActions = new LinkedBlockingQueue<Action>();
        this.log = new LinkedBlockingQueue<String>();
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        this.sendCount = 0;
    }

    public void shutdown() {
        log.offer(dateFormatter.format(Calendar.getInstance().getTime()) + " | Shutting Down");
        this.shuttingDown = true;
    }

    public String getUserName() {
        return loginName;
    }

    public void disconnect() {
        log.offer(dateFormatter.format(Calendar.getInstance().getTime()) + " | Disconnected");
    }

    public void login(String userName) {
        Date time = Calendar.getInstance().getTime();
        if (loggedIn) {
            logout();
        }
        loginName = userName;
        switch (chatServer.login(this)) {
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
            switch (chatServer.logoff(this)) {
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
            switch (chatServer.joinGroup(this, groupName)) {
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
            switch (chatServer.leaveGroup(this, groupName)) {
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
	
    public void sendMessage(String receiver, String messageText) {
        Date time = Calendar.getInstance().getTime();
        Message message = new Message(this, receiver, ++sendCount, messageText);
        log.offer(dateFormatter.format(time) + " | Sending Message | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + receiver + " | " + messageText);
        if (loggedIn) {
            switch (chatServer.send(message)) {
                case MESSAGE_ENQUEUED:    // send sqn OK
                    log.offer(dateFormatter.format(time) + " | Message Queue Success | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + receiver + ".");
                    break;
                case MESSAGE_BUFFER_FULL: // send sqn FAIL
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + receiver + " | Buffer full.");
                    break;
                case SENDER_NOT_FOUND:    // send sqn BAD_DEST
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + receiver + " | Sender not found.");
                    break;
                case RECEIVER_NOT_FOUND:  // send sqn FAIL
                    log.offer(dateFormatter.format(time) + " | Message Queue Failure | " + loginName + " (" + Integer.toString(sendCount) + ") -> " + receiver + " | Receiver not found.");
                    break;
            }
        } else {
            log.offer(dateFormatter.format(time) + " | Message Queue Failure | Not logged in.");
            // send sqn FAIL
        }
        TestChatServer.logUserSendMsg(loginName, message.toString());
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

    public void run() {
        while (true) {
            // read socket and put an action on the queue
            // execute current action and write to socket
        }
    }

    public void printLog() {
        for (String logMessage : log) {
            System.out.println(logMessage);
        }
    }
}

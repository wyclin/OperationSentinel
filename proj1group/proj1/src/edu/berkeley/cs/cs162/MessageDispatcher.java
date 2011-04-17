package edu.berkeley.cs.cs162;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Calendar;
import java.util.TreeSet;

class MessageDispatcher extends Thread {
    private UserManager userManager;
    private DatabaseManager databaseManager;
    private LinkedBlockingQueue<Message> messages;
    private boolean shuttingDown;

    MessageDispatcher(ChatServer chatServer){
        this.userManager = chatServer.getUserManager();
        this.databaseManager = userManager.getDatabaseManager();
        this.messages = new LinkedBlockingQueue<Message>();
        this.shuttingDown = false;
    }

    public void shutdown() {
        shuttingDown = true;
        interrupt();
    }

    public ChatServerResponse enqueue(Message message) {
        if (message.sender.getUserName().equals(message.receiver)) {
            return new ChatServerResponse(ResponseType.RECEIVER_SAME_AS_SENDER);
        }
        if (userManager.hasLoggedInUser(message.sender.getUserName())) {
            try {
                TreeSet<String> groupUsers = databaseManager.getGroupUserList(message.receiver);
                if (databaseManager.getUser(message.receiver) != null) {
                    message.receivingUsers = new TreeSet<String>();
                    message.receivingUsers.add(message.receiver);
                    if (messages.offer(message)) {
                        return new ChatServerResponse(ResponseType.MESSAGE_ENQUEUED);
                    } else {
                        return new ChatServerResponse(ResponseType.MESSAGE_BUFFER_FULL);
                    }
                } else if (groupUsers != null) {
                    if (groupUsers.contains(message.sender.getUserName())) {
                        message.receivingUsers = groupUsers;
                        if (messages.offer(message)) {
                            return new ChatServerResponse(ResponseType.MESSAGE_ENQUEUED);
                        } else {
                            return new ChatServerResponse(ResponseType.MESSAGE_BUFFER_FULL);
                        }
                    } else {
                        return new ChatServerResponse(ResponseType.USER_NOT_MEMBER_OF_GROUP);
                    }
                } else {
                    return new ChatServerResponse(ResponseType.RECEIVER_NOT_FOUND);
                }
            } catch (SQLException e) {
                return new ChatServerResponse(ResponseType.DATABASE_FAILURE);
            }
        } else {
            return new ChatServerResponse(ResponseType.SENDER_NOT_FOUND);
        }
    }

    private void deliver(Message message) {
        boolean hasFailed = false;
        for (String receivingUserName : message.receivingUsers) {
            try {
                ChatUser receivingUser = userManager.getLoggedInUser(receivingUserName);
                if (receivingUser != null) {
                    receivingUser.receiveMessage(message);
                } else if (databaseManager.getUser(receivingUserName) != null) {
                    databaseManager.logMessage(receivingUserName, message);
                } else {
                    hasFailed = true;
                }
            } catch (SQLException e) {
                hasFailed = true;
            }
        }
        if (hasFailed) {
            message.sender.receiveSendFailure(message);
        }
    }

    public void deliverOfflineMessages(ChatUser user) {
        try {
            LinkedList<HashMap<String, Object>> messages = databaseManager.getOfflineMessages(user.getUserName());
            HashMap<String, Object> m = messages.poll();
            while (m != null) {
                Message message = new Message((Date)m.get("timestamp"), (String)m.get("sender"), (String)m.get("receiver"), (Integer)m.get("sqn"), (String)m.get("text"));
                user.receiveMessage(message);
                m = messages.poll();
            }
        } catch (SQLException e) {}
    }

    public void run() {
        while (!shuttingDown || messages.size() > 0) {
            try {
                deliver(messages.take());
            } catch (InterruptedException e) {
            }
        }
    }
}

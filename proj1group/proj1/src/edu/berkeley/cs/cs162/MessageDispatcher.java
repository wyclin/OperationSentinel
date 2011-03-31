package edu.berkeley.cs.cs162;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.Calendar;
import java.util.TreeSet;

class MessageDispatcher extends Thread {
    private UserManager userManager;
    private LinkedBlockingQueue<Message> messages;
    private boolean shuttingDown;

    MessageDispatcher(ChatServer chatServer){
        this.userManager = chatServer.getUserManager();
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
        TreeSet<String> groupUsers = userManager.getGroupUsers(message.receiver);
        if (userManager.hasUser(message.sender.getUserName())) {
            if (userManager.hasUser(message.receiver)) {
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
        } else {
            return new ChatServerResponse(ResponseType.SENDER_NOT_FOUND);
        }
    }

    private void deliver(Message message) {
        boolean hasFailed = false;
        for (String receivingUserName : message.receivingUsers) {
            ChatUser receivingUser = userManager.getUser(receivingUserName);
            if (receivingUser == null) {
                hasFailed = true;
            } else {
                message.date = Calendar.getInstance().getTime();
                receivingUser.receiveMessage(message);
            }
        }
        if (hasFailed) {
            message.sender.receiveSendFailure(message);
        }
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

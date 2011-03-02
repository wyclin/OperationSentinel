package edu.berkeley.cs.cs162;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.Date;

class MessageDispatcher extends Thread{
    private ChatServer chatServer;
    private LinkedBlockingQueue<Message> messages;

    MessageDispatcher(ChatServer chatServer){
	    this.chatServer = chatServer;
        this.messages = new LinkedBlockingQueue<Message>();
    }

    /* Puts a message into the message queue. */
    synchronized public void enqueue(Message message) {
        try {
            messages.put(message);
        } catch (Exception e) {
            //TODO Message could not be enqueued.
        }
    }

    public void run(){
        while(true){
            if (this.hasMessage()){
                this.deliver(messages.poll());
            }
        }
    }

    /* Attempts to deliver the given message.*/

    // TODO There's a concurrency problem here which we'd brought up
    // What if the user drops after we do our check to see if the
    // user or group is still there.

    private void deliver(Message message) {
        if (!chatServer.hasName(message.receiver)) {
            TestChatServer.logChatServerDropMsg(message.printable(), new Date());
        } else if (chatServer.hasUser(message.receiver)) {
            BaseUser targetUser = chatServer.getUser(message.receiver);
            targetUser.msgReceived(message.printable());
        } else {
            Group targetGroup = chatServer.getGroup(message.receiver);
            targetGroup.messageUsers(message);
        }
    }

    /* Returns true if the message dispatcher has an enqueued message. */
    synchronized public boolean hasMessage() {
        if (messages.size() > 0) {
            return true;
	    } else {
	        return false;
        }
    }
}

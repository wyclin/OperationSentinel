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
    public void enqueue(Message message) {
	if (chatServer.hasGroup(message.receiver)) { //Broadcast Message
		Group targetGroup = chatServer.getGroup(message.receiver);
		if (targetGroup.numUsers() > 0) {
			for (String targetUsername : targetGroup.listUsers()) {
				Message newMessage = new Message(message.sender, targetUsername, message.sqn, message.text, message.date);
				this.enqueue(newMessage);
			}
		}

	} else { // Unicast Message
        	try {
            		messages.put(message);
        	} catch (Exception e) {
        		// Message could not be enqueued.
		}
	}
    }

    /* Runs message dequeue-and-deliver in an infinite loop */
    public void run(){
        while(true){
            try {
                deliver(messages.take());
            } catch (Exception e) {
            }
        }
    }

    /* Attempts to deliver the given message to user. Messages that are sent to groups
     * should have been converted to single message sent to users when being enqueued. */

    private void deliver(Message message) {
        if (!chatServer.hasUser(message.receiver)) { // The user logged off.
            TestChatServer.logChatServerDropMsg(message.toString(), new Date());
            BaseUser senderUser = chatServer.getUser(message.sender);
            try {
                senderUser.messages.put("DROPPED == " + message.toString());
            } catch (Exception e) {
            }
        } else if (chatServer.hasUser(message.receiver)) {
            BaseUser targetUser = chatServer.getUser(message.receiver);
            targetUser.msgReceived(message.toString());
        }
    }

    /* Returns true if the message dispatcher has an enqueued message. */
    public boolean hasMessage() {
        if (messages.size() > 0) {
            return true;
	    } else {
	        return false;
        }
    }
}

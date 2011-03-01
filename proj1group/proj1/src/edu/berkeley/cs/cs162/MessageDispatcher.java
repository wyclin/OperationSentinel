package edu.berkeley.cs.cs162;

import java.util.LinkedBlockingQueue;

class MessageDispatcher extends Thread{
   
    private ChatServer chatServer; 
    private LinkedBlockingQueue<Message> messages;

    MessageDispatcher(ChatServer chatServer){
	    this.chatServer = chatServer;
	    messages = new LinkedBlockingQueue<Message>();
    }

    /* Puts a message into the message queue. */	
    synchronized public void enqueue(Message message) {
		messages.put(message);
    }

    public void run(){
		while(true){
			if (this.hasMessage()){
				this.deliver(messages.poll());
			}
		}
	}

    /* Attempts to deliver the given message.*/
    private void deliver(Message message){
	// TODO Add some type of checking to see if message is well formed?
	
	if (!chatServer.hasName(message.receiver)) {
		// TODO ERROR to sender
	} else 
	if (chatServer.hasUser(message.receiver)) {
                 User targetUser = chatServer.getUser(message.receiver);
		 recipient.msgReceived(message.text);
	} else {
	         Group targetGroup = chatServer.getGroup(message.receiver);
                 
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
    

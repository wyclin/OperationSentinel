package edu.berkeley.cs.cs162;

import java.util.LinkedList;

class MessageDispatcher extends Thread{
    
    LinkedList<Message> messages;
    MessageDispatcher(){
		messages = new LinkedList<Message>();
    }
	
    public void enqueue(Message message) {
	        //TODO need more implementation
		messages.add(message);
	}
	public void run(){
		while(true){
			if (messages.size() != 0){
				this.deliver(messages.getFirst());
			}
		}
	}

    private void deliver(Message message){
	    //TODO Implement Me.
		
	}
    
    /* Returns true if the message dispatcher has an enqueued message. */
    public boolean hasMessage() {
           //TODO Implement correctly
	   return false;
    }
}
    

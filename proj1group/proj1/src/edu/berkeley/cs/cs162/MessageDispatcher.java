package edu.berkeley.cs.cs162;

class MessageDispatcher extends Thread{
    
	Queue<Message> messages;
    MessageDispatcher(){
		messages = new Queue();
	}
	
    public void enqueue(Message message) {
		messages.enqueue(message);
	}
	public void run(){
		while(true){
			if (!messages.empty()){
				this.deliver(messages.dequeue());
			}
		}
	}

    private void deliver(Message message){
		
	}
	
}
    
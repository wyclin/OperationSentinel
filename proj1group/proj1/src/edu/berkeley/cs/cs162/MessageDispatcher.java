package edu.berkeley.cs.cs162;

class MessageDispatcher{
    
	Queue messages;
    MessageDispatcher(){
		messages = new Queue();
	}
	
    public void enqueue(Message message) {
		messages.enqueue(message);
	}

    public void start(){}
    public void stop(){}
    private void deliver(){}
	
}
    
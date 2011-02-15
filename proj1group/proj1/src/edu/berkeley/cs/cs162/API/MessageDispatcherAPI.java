package edu.berkeley.cs.cs162

public class MessageDispatcher

Constructor Detail
    public MessageDispatcher()

Method Detail
	public void start()
		Starts up the message dispatcher.

	public void stop()
		Terminates the message dispatcher.

    public void enqueue(Message message)
		Enqueues an incoming message for the message dispatcher to deliver later.
		Parameters:
			message - the Message object to be delivered later.
	
	private void deliver()
		Delivers the messages waiting in queue in FIFO order.
	

    


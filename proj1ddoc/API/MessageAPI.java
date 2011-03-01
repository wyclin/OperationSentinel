package edu.berkeley.cs.cs162

public class Message

Constructor Detail
	public Message(String sender, 
				   String receiver, 
				   String text)
	Creates and initializes a new Message instance and time-stamps it.
	Parameters:
		sender - the name of the user that sent the message.
		receiver - the name of the user that is to receive the message.
		text - the message body.

Field Detail
	public final String RECEIVER
		The name of the user that is to receive the message.
		
	public final String SENDER
		The name of the user that sent the message.
	
	public final String TEXT
		The message body.
	
	public final TimeStamp TIMESTAMP
		The time the message was created.

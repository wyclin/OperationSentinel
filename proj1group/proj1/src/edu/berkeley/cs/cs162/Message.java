package edu.berkeley.cs.cs162;

import java.util.Date;

class Message{
    public String receiver, sender, text;
    public Date date;

    Message(String sender, String receiver, String text){
		this.receiver = receiver;
		this.sender = sender;
		this.text = text;
		this.date = new Date();
    }
    
    public String printable(){
	   //TODO What is this?
	   return null;
    }
}

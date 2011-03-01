package edu.berkeley.cs.cs162;

import java.util.Date;

class Message{
    public String receiver, sender, text;
    public Date date;
    public int sqn;

    Message(String sender, String receiver, String text, int sqn){
		this.receiver = receiver;
		this.sender = sender;
		this.text = text;
		this.date = new Date();
		this.sqn = sqn;
    }
   
    /* Returns message in printable string format. SRC DST TIMESTAMP_UNIXTIME SQN. */ 
    public String printable(){
	   //TODO Implement
	   return null;
    }
}

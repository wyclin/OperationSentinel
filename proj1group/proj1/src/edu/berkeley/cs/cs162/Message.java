package edu.berkeley.cs.cs162;

import java.util.Date;

class Message{
    public String receiver, sender, text;
    public Date date;
    public int sqn;

    Message(String sender, String receiver, int sqn, String text){
        this.receiver = receiver;
        this.sender = sender;
        this.text = text;
        this.date = new Date();
        this.sqn = sqn;
    }
   
    /* Returns message in printable string format. SRC DST TIMESTAMP_UNIXTIME SQN. */ 
    public String toString() {
        return sender + " " + receiver + " " + Long.toString(date.getTime() / 1000L) + sqn;
    }
}

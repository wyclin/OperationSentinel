package edu.berkeley.cs.cs162;

import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

class Message {
    public String receiver;
    public TreeSet<String> receivingUsers;
    public ChatUser sender;
    public String text;
    public Date date;
    public int sqn;

    Message(ChatUser sender, String receiver, int sqn, String text) {
        this.receiver = receiver;
        this.sender = sender;
        this.text = text;
        this.date = Calendar.getInstance().getTime();
        this.sqn = sqn;
    }

    Message(String receiver, int sqn, String text) {
        this(null, receiver, sqn, text);
    }
   
    /* Returns message in printable string format. SRC DST TIMESTAMP_UNIXTIME MSG SQN. */
    public String toString() {
        return sender.getUserName() + " " + receiver + " " + Long.toString(date.getTime() / 1000L) + " " + Integer.toString(sqn) + " " + text;
    }
}

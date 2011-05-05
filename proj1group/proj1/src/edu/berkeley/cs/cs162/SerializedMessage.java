package edu.berkeley.cs.cs162;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeSet;

public class SerializedMessage implements Serializable {

    public String sender;
    public String receiver;
    public TreeSet<String> receivingUsers;
    public Date date;
    public int sqn;
    public String text;

    public SerializedMessage (String sender, String receiver, TreeSet<String> receivingUsers, Date date, int sqn, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.receivingUsers = receivingUsers;
        this.date = date;
        this.sqn = sqn;
        this.text = text;
    }

    public Message toMessage() {
        return new Message(date, sender, receiver, receivingUsers, sqn, text);
    }
}

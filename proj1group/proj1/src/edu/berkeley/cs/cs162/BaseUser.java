package edu.berkeley.cs.cs162;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Set;

public class BaseUser extends Thread {
    ChatServer chatServer;
    String name;
    ArrayList<Group> joinedGroups;
    LinkedBlockingQueue<String> messages;
    int sendCount;
	
    public BaseUser() {
        super();
    }

    public BaseUser(String name, ChatServer chatServer) {
        super();
        this.name = name;
        this.chatServer = chatServer;
        this.joinedGroups = new ArrayList<Group>();
        this.messages = new LinkedBlockingQueue<String>();
        this.sendCount = 0;
    }
	
    /* Returns the username of the user */
    public String getUsername(){
        return name;
    }

    /* Returns the list of groups a user has joined */
    public ArrayList<Group> listGroups(){
        return joinedGroups;
    }
	
    /**
     * This function is called when the user successfully connect to the server.
     * It also starts the thread that represents this particular user.
     */
    public void connected() {
        start();
    }

    /**
     * @param dest  Destination could be a user or a group.
     * @param msg   Message to be sent.
     *
     * This is used to inject messages from the test harness into
     * your system.  It should send a message to the
     * destination.
     */
    public void send(String dest, String msg) {
        Message message = new Message(name, dest, ++sendCount, msg);
        String logMessage = name + " " + dest + " " + Long.toString(message.date.getTime() / 1000L) + " " + Integer.toString(message.sqn);

        chatServer.send(message);
        TestChatServer.logUserSendMsg(name, logMessage);
        try {
            messages.put("SENT == " + message);
        } catch (Exception e) {
        }
    }
	
    /**
     * @param msg Received message.
     * Called when a message is received by the thread.
     *
     * This is part of the test harness -- in the future you will
     * send the message to the user over a socket.  For now, you
     * should print the message to stdout.  The format is:
     *
     * SRC DST TIMESTAMP_UNIXTIME SQN
     */
    public void msgReceived(String msg){
        TestChatServer.logUserMsgRecvd(name, msg, Calendar.getInstance().getTime());
        try {
            messages.put("RECEIVED == " + msg);
        } catch (Exception e) {
        }
    }

    /* Returns a list of all groups on the chat server */
	public Set<String> getGroupList(){
		return (Set<String>) chatServer.listAllGroups(name);
	}

    /* Returns a list of all users logged onto the chat server */
	public Set<String> getUserList(){
		return (Set<String>) chatServer.listAllUsers(name);
	}

    /* Returns the number of groups on the chat server */
	public int getNumberOfGroups(){
		return chatServer.getNumberOfGroups(name);
	}

    /* Returns the total number of users on the chat server */
	public int getNumberOfUsers(){
		return chatServer.getNumberOfUsers(name);
	}
}

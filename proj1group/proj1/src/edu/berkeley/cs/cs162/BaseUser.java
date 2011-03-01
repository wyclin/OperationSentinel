package edu.berkeley.cs.cs162;

import java.util.Calendar;
import java.util.Set;
import java.util.ArrayList;

public class BaseUser extends Thread {
    ChatServer chatServer;
    String name;
    ArrayList<Group> joinedGroups;
    ArrayList<String> messages;
    int sendCount;
	
    public BaseUser() {
        super();
    }

    public BaseUser(String name, ChatServer chatServer) {
        this.name = name;
        this.chatServer = chatServer;
        this.joinedGroups = new ArrayList<Group>();
        this.messages = new ArrayList<String>();
        this.sendCount = 0;
    }
	
    public String getUsername(){
        return name;
    }
	
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

        messages.add(logMessage);
        chatServer.send(message);
        TestChatServer.logUserSendMsg(name, logMessage);
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
        messages.add(msg);
    }

	public Set<String> getGroupList(){
		return (Set<String>) chatServer.listAllGroups(name);
	}

	public Set<String> getUserList(){
		return (Set<String>) chatServer.listAllUsers(name);
	}

	public int getNumberOfGroups(){
		return chatServer.getNumberOfGroups(name);
	}

	public int getNumberOfUsers(){
		return chatServer.getNumberOfUsers(name);
	}
}

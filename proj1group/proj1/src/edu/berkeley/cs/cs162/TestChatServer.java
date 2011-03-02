package edu.berkeley.cs.cs162;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;
import java.util.Iterator;

public class TestChatServer {

    /* BEGIN Test Cases */

    /* Test basic routines, i.e. server start and stop, login, logout, sending messages */
    public static void basicTest1() throws InterruptedException {
        System.out.println("=== BEGIN TEST Basic Test 1 ===");
        ChatServerInterface s = new ChatServer();
        ExecutorService exe = Executors.newFixedThreadPool(10);
        int i;

        s.login("steve");
        s.login("mike");
        BaseUser bu = s.getUser("mike");

        s.joinGroup(bu, "group1");

        for (i = 0; i < 50; i++) {
            MessageDeliveryTask t = new MessageDeliveryTask(s, "steve", "mike", "hi "+ i);
            MessageDeliveryTask c = new MessageDeliveryTask(s, "steve", "group1", "hig "+ i);
            exe.execute(t);
            exe.execute(c);
        }
        exe.shutdown();

        s.shutdown();
        System.out.println("=== END TEST Basic Test 1 ===\n");
    }

    /* Tests that usernames' uniqueness is enforced */
    public static void testUserNameUniqueness() throws InterruptedException {
        System.out.println("=== BEGIN TEST User Name Uniqueness ===");
        ChatServerInterface chatServer = new ChatServer();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        System.out.println("user1 logs in: " + chatServer.login("user1"));
        System.out.println("user1 logs in: " + chatServer.login("user1"));

        chatServer.shutdown();
        threadPool.shutdown();
        System.out.println("=== END TEST User Name Uniqueness ===\n");
    }

    /* Tests that a chat server's user capacity is enforced*/
    public static void testServerCapacity() throws InterruptedException {
        System.out.println("=== BEGIN TEST Server Capacity ===");
        ChatServerInterface chatServer = new ChatServer();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 100; i++) {
          System.out.println("user" + Integer.toString(i) + " logs in: " + chatServer.login("user" + Integer.toString(i)));
        }

        System.out.println("\nuser101 logs in: " + chatServer.login("user101"));

        chatServer.shutdown();
        threadPool.shutdown();
        System.out.println("=== END TEST Server Capacity ===\n");
    }

    /* Test that a group capacity is enforced*/
    public static void testGroupCapacity() throws InterruptedException {
        System.out.println("=== BEGIN TEST Group Capacity ===");
        ChatServerInterface chatServer = new ChatServer();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 11; i++) {
          System.out.println("user" + Integer.toString(i) + " logs in: " + chatServer.login("user" + Integer.toString(i)));
        }

        System.out.println("");
        for (int i = 1; i <= 10; i++) {
          System.out.println("user" + Integer.toString(i) + " joins group1: " + chatServer.joinGroup(chatServer.getUser("user" + Integer.toString(i)), "group1"));
        }

        System.out.println("\nuser11 joins group1: " + chatServer.joinGroup(chatServer.getUser("user11"), "group1"));

        chatServer.shutdown();
        threadPool.shutdown();
        System.out.println("=== END TEST Group Capacity ===\n");
    }

    /* Tests that users can join multiple groups */
    public static void testUserJoinsMultipleGroups() throws InterruptedException {
        System.out.println("=== BEGIN TEST User Joins Multiple Groups ===");
        ChatServerInterface chatServer = new ChatServer();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        System.out.println("user1 logs in: " + chatServer.login("user1"));

        System.out.println("\nuser1 joins group1: " + chatServer.joinGroup(chatServer.getUser("user1"), "group1"));
        System.out.println("user1 joins group2: " + chatServer.joinGroup(chatServer.getUser("user1"), "group2"));

        chatServer.shutdown();
        threadPool.shutdown();
        System.out.println("=== END TEST User Joins Multiple Groups ===\n");
    }

    /* Tests that a logged on user can get chat server information  */
    public static void testUserGetsServerInfo() throws InterruptedException {
        System.out.println("=== BEGIN TEST User Gets Server Info ===");
        ChatServer chatServer = new ChatServer();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        System.out.println("user1 logs in: " + chatServer.login("user1"));
        System.out.println("user2 logs in: " + chatServer.login("user2"));

        System.out.println("\nuser1 joins group1: " + chatServer.joinGroup(chatServer.getUser("user1"), "group1"));
        System.out.println("user2 joins group1: " + chatServer.joinGroup(chatServer.getUser("user2"), "group1"));

        System.out.println("\nuser1 gets number of groups: " + Integer.toString(chatServer.getNumberOfGroups("user1")));
        System.out.print("user1 gets group list: ");
        Iterator<String> groups = chatServer.listAllGroups("user1").iterator();
        while (groups.hasNext()) {
            System.out.print(groups.next() + " ");
        }
        System.out.println("");

        System.out.println("\nuser1 gets number of users: " + Integer.toString(chatServer.getNumberOfUsers("user1")));
        System.out.print("user1 gets user list: ");
        Iterator<String> users = chatServer.listAllUsers("user1").iterator();
        while (users.hasNext()) {
            System.out.print(users.next() + " ");
        }
        System.out.println("");

        chatServer.shutdown();
        threadPool.shutdown();
        System.out.println("=== END TEST User Gets Server Info ===\n");
    }

    /* Tests that message unicasting works */
    public static void testUnicastMessages() throws InterruptedException {
        System.out.println("=== BEGIN TEST Unicast Messages ===");
        ChatServerInterface chatServer = new ChatServer();
        ExecutorService threadPool = Executors.newFixedThreadPool(500);

        System.out.println("user1 logs in: " + chatServer.login("user1"));
        System.out.println("user2 logs in: " + chatServer.login("user2"));
        System.out.println("user3 logs in: " + chatServer.login("user3"));
        System.out.println("user4 logs in: " + chatServer.login("user4"));
        BaseUser user1 = chatServer.getUser("user1");
        BaseUser user2 = chatServer.getUser("user2");
	BaseUser user3 = chatServer.getUser("user3");
	BaseUser user4 = chatServer.getUser("user4");

		MessageDeliveryTask t1 = new MessageDeliveryTask(chatServer, "user2", "user1", "This is a test message from u2 to u1");
        System.out.println("\nuser2 unicasts to user1");
		
		MessageDeliveryTask t2 = new MessageDeliveryTask(chatServer, "user2", "user1", "u2: Hi user1");
        System.out.println("\nuser2 unicasts to user1");
		
		
        threadPool.execute(t1);
		threadPool.execute(t2);

        MessageDeliveryTask t = new MessageDeliveryTask(chatServer, "user1", "user2", "message1");
        System.out.println("\nuser1 unicasts to user2");
        threadPool.execute(t);
        Thread.currentThread().sleep(100);

        t = new MessageDeliveryTask(chatServer, "user1", "user2", "message2");
        System.out.println("\nuser1 unicasts to user2");
        threadPool.execute(t);
        Thread.currentThread().sleep(100);

        t = new MessageDeliveryTask(chatServer, "user1", "user2", "message3");
        System.out.println("\nuser1 unicasts to user2");
        threadPool.execute(t);


        Thread.currentThread().sleep(1000);

        System.out.println("\n--- Log for user 1 ---");
        String curMessage = user1.messages.poll();
        while (curMessage != null) {
            System.out.println(curMessage);
            curMessage = user1.messages.poll();
        }
        System.out.println("--- ---");

        System.out.println("\n--- Log for user 2 ---");
        curMessage = user2.messages.poll();
        while (curMessage != null) {
            System.out.println(curMessage);
            curMessage = user2.messages.poll();
        }
        System.out.println("--- ---");
        
	System.out.println("\nuser3 unicasts 50 messages to user4 and vice versa");
	
	for (int i = 0; i< 50; i++) {
                MessageDeliveryTask to4 = new MessageDeliveryTask(chatServer, "user3", "user4", "messageTo4: "+i);
		MessageDeliveryTask to3 = new MessageDeliveryTask(chatServer, "user4", "user3", "messageTo3: "+i);
        	threadPool.execute(to4);
		threadPool.execute(to3);
                Thread.currentThread().sleep(10);
	}
        
        System.out.println("\n--- Log for user 3 ---");
        for (int i = 0; i < user3.messages.size(); i++) {
            System.out.println(user3.messages.get(i));
        }


        System.out.println("\n--- Log for user 4 ---");
        for (int i = 0; i < user4.messages.size(); i++) {
            System.out.println(user4.messages.get(i));
        }
        System.out.println("--- ---");
        System.out.println("--- ---");
        chatServer.shutdown();
        threadPool.shutdown();
        System.out.println("=== END TEST Unicast Messages ===\n");
    }
	
	public static void nonExistentUserRequestServer(){
		System.out.println("=== BEGIN TEST User Not In The Server Request Server Information ===");
		
		ChatServer chatServer = new ChatServer();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
		
		System.out.println("user1 is created and not logged in the chat server");
		BaseUser user1 = new BaseUser("user1", null);
		
		
		System.out.println("testing chatServer's methods");
		System.out.println("getUser: " + chatServer.getUser("user1"));
		System.out.println("listAllUsers: " + chatServer.listAllUsers("user1"));
		System.out.println("listAllGroups: " + chatServer.listAllGroups("user1"));
		System.out.println("getNumberOfUsers: " + chatServer.getNumberOfUsers("user1"));
		System.out.println("getNumberOfGroups: " + chatServer.getNumberOfGroups("user1"));		
		
		chatServer.shutdown();
        threadPool.shutdown();
		
		System.out.println("=== END TEST User Not In The Server Request Server Information ===\n");
	}

    public static void testBroadcastMessages() throws InterruptedException {
        System.out.println("=== BEGIN TEST Broadcast Messages ===");
        ChatServerInterface chatServer = new ChatServer();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        System.out.println("user1 logs in: " + chatServer.login("user1"));
        System.out.println("user2 logs in: " + chatServer.login("user2"));
        System.out.println("user3 logs in: " + chatServer.login("user3"));

        BaseUser user1 = chatServer.getUser("user1");
        BaseUser user2 = chatServer.getUser("user2");
        BaseUser user3 = chatServer.getUser("user3");

        System.out.println("user1 joins group1: " + chatServer.joinGroup(chatServer.getUser("user1"), "group1"));
        System.out.println("user2 joins group1: " + chatServer.joinGroup(chatServer.getUser("user2"), "group1"));
        System.out.println("user3 joins group1: " + chatServer.joinGroup(chatServer.getUser("user3"), "group1"));

        MessageDeliveryTask t;
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 10; j++) {
                t = new MessageDeliveryTask(chatServer, "user" + Integer.toString(i), "group1", "message" + Integer.toString(i) + Integer.toString(j));
                System.out.println("user" + Integer.toString(i) + " unicasts to group1");
                threadPool.execute(t);
            }
        }

        Thread.currentThread().sleep(5000);

        System.out.println("\n--- Log for user 1 ---");
        String curMessage = user1.messages.poll();
        while (curMessage != null) {
            System.out.println(curMessage);
            curMessage = user1.messages.poll();
        }
        System.out.println("--- ---");

        System.out.println("\n--- Log for user 2 ---");
        curMessage = user2.messages.poll();
        while (curMessage != null) {
            System.out.println(curMessage);
            curMessage = user2.messages.poll();
        }
        System.out.println("--- ---");

        System.out.println("\n--- Log for user 3 ---");
        curMessage = user3.messages.poll();
        while (curMessage != null) {
            System.out.println(curMessage);
            curMessage = user3.messages.poll();
        }
        System.out.println("--- ---");

        chatServer.shutdown();
        threadPool.shutdown();
        System.out.println("=== END TEST Broadcast Messages ===\n");
    }

    /* END Test Cases */

    /**
     * Runs test cases
     *
     * @param args
     * @throws InterruptedException
     */
	public static void main(String [] args) throws InterruptedException {
        testUserNameUniqueness();
        testServerCapacity();
        testGroupCapacity();
        testUserJoinsMultipleGroups();
        testUserGetsServerInfo();
        testUnicastMessages();
        testBroadcastMessages();
		nonExistentUserRequestServer();
	}

	/**
	 * Logs the events of a user logging into the ChatServer.  This should only be called AFTER
	 * the user has been accepted by the ChatServer.
	 *
	 * @param username user logging the event.
	 * @param time time of the event.
	 */
	public static void logUserLogin(String username, Date time) {
	}

	/**
	 *  Logs a login-failed event.  Should be called AFTER you are certain that the user has been rejected by
	 *  by the ChatServer.
	 *
	 * @param username user logging the event.
	 * @param time time of the event.
	 * @param e login error
	 */
	public static void logUserLoginFailed(String username, Date time, LoginError e) {
	}

	/**
	 * Logs the logout event.  This should only be called AFTER the user has been released by the
	 * ChatServer successfully.
	 *
	 * @param username user logging the event
	 * @param time time of the event
	 */
	public static void logUserLogout(String username, Date time) {
	}

	/**
	 * Logs the events of a user logging into the group.  This should only be called AFTER
	 * the user has been accepted by the group.
	 *
	 * @param groupname name of the group.
	 * @param username user logging the event.
	 * @param time time of the event.
	 */
	public static void logUserJoinGroup(String groupname, String username, Date time) {
	}

	/**
	 * Logs the events of a user logging out of the group.
	 *
	 * @param groupname name of the group.
	 * @param username user logging the event.
	 * @param time time of the event.
	 */
	public static void logUserLeaveGroup(String groupname, String username, Date time) {
	}


	/**
	 * This should be called when the user attempts to send a message to the chat server
	 * (after the call is made).
	 *
	 * @param username the name of the user
	 * @param msg the string representation of the message.
	 * 			SRC DST TIMESTAMP_UNIXTIME SQN
	 * 		example: alice bob 1298489721 23
	 */
	public static void logUserSendMsg(String username, String msg) {
	}

	/**
	 * If, for any reason, the ChatServer determines that the message cannot be delivered.  This
	 * message should be called to log that event.
	 *
	 * @param msg string representation of the message
	 * 			SRC DST TIMESTAMP_UNIXTIME SQN
	 * 		example: alice bob 1298489721 23
	 * @param time time when the event occurred.
	 */
	public static void logChatServerDropMsg(String msg, Date time) {
	}

	/**
	 * When the user receives a message, this method should be called.
	 *
	 * @parapm username name of the user
	 * @param msg string representation of the message.
	 * 			SRC DST TIMESTAMP_UNIXTIME SQN
	 * 		example: alice bob 1298489721 23
	 * @param time time when the event occurred.
	 */
	public static void logUserMsgRecvd(String username, String msg, Date time) {
	}
}

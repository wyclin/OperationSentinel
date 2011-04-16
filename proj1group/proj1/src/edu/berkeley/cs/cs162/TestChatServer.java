package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

public class TestChatServer {

    public static void printSet(Collection<String> set) {
        for (String string : set) {
            System.out.println(string);
        }
    }

	public static void main(String[] args) throws Exception {
        // Database Tests
        //testEmptyDatabase();
        //testAddUsersAndGroups();

        // Non-Networked Tests
        //testBasic();
        //testLogout();
        //testUserNameUniqueness();
        //testServerCapacity();
        //testLoginQueue();
        //testGroupCapacity();
        //testUserJoinsMultipleGroups();
        //testUnicastMessages();
        //testBroadcastMessages();
        //testSelfUnicast();
        //testNonMemberBroadcast();
        //testFailUnicast();
        //testServerShutdown();
        //testUserManager();

        // Networked Tests
        //testNetworkLogin();
        //testNetworkLoginTimeout();
        //testNetworkUnexpectedDisconnectBeforeLogin();
        //testNetworkUnexpectedDisconnectAfterLogin();
        //testNetworkLoginQueue();
        //testNetworkSendReceive();
        //testNetworkFailUnicast();

        // Client-Server Tests
        //testClientBasic();
        //testClientLogout();
        //testClientDisconnect();
        //testClientReconnect();
        //testClientTimeout();
        //testClientLoginQueue();
        //testClientGroupCapacity();
        //testClientSendMsgFailNotify();
        //testClientDisconnectsWhileInLoginWaitQueue();
        //testClientDoesNotNormallyTimeout();
        //testClientDisconnectHandledAfterLogoff();
        //testTimerRestartedAfterEveryFailedLoginAttempt();
        //testCertainClientCommandsRejectedWhenNotConnectedOrLoggedIn();
        //testInvalidClientCommandsAreSkipped();
	}

    // Database Tests

    public static void testEmptyDatabase() throws SQLException {
        System.out.println("=== BEGIN DATABASE TEST Empty Database  ===");
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.emptyDatabase();

        System.out.println("User Count: " + databaseManager.getUserCount());
        System.out.println("Group Count: " + databaseManager.getGroupCount());
        System.out.println("\nUser List");
        printSet(databaseManager.getUserList());
        System.out.println("\nGroup List");
        printSet(databaseManager.getGroupList());

        databaseManager.emptyDatabase();
        System.out.println("=== END DATABASE TEST Empty Database  ===\n");
    }

    public static void testAddUsersAndGroups() throws SQLException {
        System.out.println("=== BEGIN DATABASE TEST Add Users and Groups  ===");
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.emptyDatabase();

        databaseManager.addUser("user1", "password");
        HashMap<String, Object> user1 = databaseManager.getUser("user1");
        System.out.println("Added User: " + user1.get("name") + ", " + user1.get("password"));
        databaseManager.addUser("user2", "password");
        HashMap<String, Object> user2 = databaseManager.getUser("user2");
        System.out.println("Added User: " + user2.get("name") + ", " + user2.get("password"));

        databaseManager.addGroup("group1");
        HashMap<String, Object> group1 = databaseManager.getGroup("group1");
        databaseManager.addGroup("group2");
        System.out.println("Added Group: " + group1.get("name"));
        HashMap<String, Object> group2 = databaseManager.getGroup("group2");
        System.out.println("Added Group: " + group2.get("name"));

        databaseManager.addUserToGroup("user1", "group1");
        databaseManager.addUserToGroup("user2", "group1");
        databaseManager.addUserToGroup("user1", "group2");

        System.out.println("User Count: " + databaseManager.getUserCount());
        System.out.println("Group Count: " + databaseManager.getGroupCount());
        System.out.println("group1 User Count: " + databaseManager.getGroupUserCount("group1"));
        System.out.println("group2 User Count: " + databaseManager.getGroupUserCount("group2"));

        System.out.println("\nUser List");
        printSet(databaseManager.getUserList());
        System.out.println("\nGroup List");
        printSet(databaseManager.getGroupList());
        System.out.println("\ngroup1 User List");
        printSet(databaseManager.getGroupUserList("group1"));
        System.out.println("\ngroup2 User List");
        printSet(databaseManager.getGroupUserList("group2"));

        System.out.println("\nRemoving user1 from group2");
        databaseManager.removeUserFromGroup("user1", "group2");
        System.out.println("User List");
        printSet(databaseManager.getUserList());
        System.out.println("\nGroup List");
        printSet(databaseManager.getGroupList());
        System.out.println("\ngroup1 User List");
        printSet(databaseManager.getGroupUserList("group1"));
        System.out.println("\ngroup2 User List");

        System.out.println("\nRemoving user1");
        databaseManager.removeUser("user1");
        System.out.println("User List");
        printSet(databaseManager.getUserList());
        System.out.println("\nGroup List");
        printSet(databaseManager.getGroupList());
        System.out.println("\ngroup1 User List");
        printSet(databaseManager.getGroupUserList("group1"));
        System.out.println("\ngroup2 User List");

        System.out.println("\nRemoving user2");
        databaseManager.removeUser("user2");
        System.out.println("User List");
        printSet(databaseManager.getUserList());
        System.out.println("\nGroup List");
        printSet(databaseManager.getGroupList());
        System.out.println("\ngroup1 User List");
        printSet(databaseManager.getGroupUserList("group1"));
        System.out.println("\ngroup2 User List");

        System.out.println("\nRemoving group1");
        databaseManager.removeGroup("group1");
        System.out.println("Group List");
        printSet(databaseManager.getGroupList());

        System.out.println("\nGroup Count: " + databaseManager.getGroupCount());

        databaseManager.emptyDatabase();
        System.out.println("=== END DATABASE TEST Add Users and Groups  ===\n");
    }

/*
    // Non-Networked Server Tests (Project 1)

    public static void testBasic() throws InterruptedException {
        System.out.println("=== BEGIN TEST Basic Test ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        ChatUser user2 = new ChatUser(chatServer);
        ChatUser user3 = new ChatUser(chatServer);

        user1.login("User 1");
        user2.login("User 2");
        user3.login("User 3");

        user1.sendMessage("User 2", "Message 1");
        Thread.currentThread().sleep(50);
        user2.sendMessage("User 1", "Message 2");
        Thread.currentThread().sleep(50);
        user1.sendMessage("User 2", "Message 3");
        Thread.currentThread().sleep(50);
        user2.sendMessage("User 1", "Message 4");
        Thread.currentThread().sleep(50);

        user1.joinGroup("Group 1");
        user2.joinGroup("Group 1");
        user3.joinGroup("Group 1");

        user1.sendMessage("Group 1", "Message 5");
        Thread.currentThread().sleep(50);
        user2.sendMessage("Group 1", "Message 6");
        Thread.currentThread().sleep(50);
        user3.sendMessage("Group 1", "Message 7");
        Thread.currentThread().sleep(50);

        user1.logout();
        user2.logout();
        user3.logout();

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");
        System.out.println("\n== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==");
        System.out.println("\n== BEGIN LOG user3 ==");
        user3.printLog();
        System.out.println("== END LOG user3 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST Basic test ===\n");
    }

    public static void testLogout() throws InterruptedException {
        System.out.println("=== BEGIN TEST Logout ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        ChatUser user2 = new ChatUser(chatServer);
        user1.login("User 1");
        user2.login("User 2");
        user1.joinGroup("Group 1");
        user2.joinGroup("Group 1");
        user1.joinGroup("Group 2");
        user1.joinGroup("Group 3");
        user1.logout();

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST Logout ===\n");
    }

    public static void testUserNameUniqueness() throws InterruptedException {
        System.out.println("=== BEGIN TEST User Name Uniqueness ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        ChatUser user2 = new ChatUser(chatServer);

        user1.login("Name");
        user2.login("Name");

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");
        System.out.println("\n== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST User Name Uniqueness ===\n");
    }

    public static void testServerCapacity() throws InterruptedException {
        System.out.println("=== BEGIN TEST Server Capacity ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser[] users = new ChatUser[112];
        for (int i = 1; i <= 111; i++) {
            users[i] = new ChatUser(chatServer);
            users[i].login("User " + Integer.toString(i));
        }

        for (int i = 1; i <= 111; i++) {
            System.out.println("\n== BEGIN LOG user[" + Integer.toString(i) + "] ==");
            users[i].printLog();
            System.out.println("== END LOG user[" + Integer.toString(i) + "] ==");
        }

        chatServer.shutdown();
        System.out.println("=== END TEST Server Capacity ===\n");
    }

    public static void testLoginQueue() throws InterruptedException {
        System.out.println("=== BEGIN TEST Login Queue ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser[] users = new ChatUser[111];
        for (int i = 1; i <= 110; i++) {
            users[i] = new ChatUser(chatServer);
            users[i].login("User " + Integer.toString(i));
        }

        users[110].logout();

        for (int i = 1; i <= 10; i++) {
            users[i].logout();
        }

        for (int i = 1; i <= 110; i++) {
            System.out.println("\n== BEGIN LOG user[" + Integer.toString(i) + "] ==");
            users[i].printLog();
            System.out.println("== END LOG user[" + Integer.toString(i) + "] ==");
        }

        chatServer.shutdown();
        System.out.println("=== END TEST Login Queue ===\n");
    }

    public static void testGroupCapacity() throws InterruptedException {
        System.out.println("=== BEGIN TEST Group Capacity ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser[] users = new ChatUser[12];
        for (int i = 1; i <= 11; i++) {
            users[i] = new ChatUser(chatServer);
            users[i].login("User " + Integer.toString(i));
            users[i].joinGroup("Group 1");
        }

        for (int i = 1; i <= 11; i++) {
            System.out.println("\n== BEGIN LOG user[" + Integer.toString(i) + "] ==");
            users[i].printLog();
            System.out.println("== END LOG user[" + Integer.toString(i) + "] ==");
        }

        chatServer.shutdown();
        System.out.println("=== END TEST Group Capacity ===\n");
    }

    public static void testUserJoinsMultipleGroups() throws InterruptedException {
        System.out.println("=== BEGIN TEST User Joins Multiple Groups ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        user1.login("User 1");
        user1.joinGroup("Group 1");
        user1.joinGroup("Group 2");
        user1.joinGroup("Group 3");

        user1.printLog();

        chatServer.shutdown();
        System.out.println("=== END TEST User Joins Multiple Groups ===\n");
    }

    public static void testUnicastMessages() throws InterruptedException {
        System.out.println("=== BEGIN TEST Unicast Messages ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        ChatUser user2 = new ChatUser(chatServer);
        user1.login("User 1");
        user2.login("User 2");

        for (int i = 1; i <= 10; i++) {
            user1.sendMessage("User 2", "Message " + Integer.toString(i));
            Thread.currentThread().sleep(50);
            user2.sendMessage("User 1", "Response " + Integer.toString(i));
            Thread.currentThread().sleep(50);
        }

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");
        System.out.println("\n== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST Unicast Messages ===\n");
    }

    public static void testBroadcastMessages() throws InterruptedException {
        System.out.println("=== BEGIN TEST Broadcast Messages ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        ChatUser user2 = new ChatUser(chatServer);
        ChatUser user3 = new ChatUser(chatServer);
        user1.login("User 1");
        user2.login("User 2");
        user3.login("User 3");
        user1.joinGroup("Group 1");
        user2.joinGroup("Group 1");
        user3.joinGroup("Group 1");

        for (int i = 1; i <= 10; i++) {
            user1.sendMessage("Group 1", "Message " + Integer.toString(i));
            Thread.currentThread().sleep(50);
            user2.sendMessage("Group 1", "Response " + Integer.toString(i));
            Thread.currentThread().sleep(50);
            user3.sendMessage("Group 1", "Another Response " + Integer.toString(i));
            Thread.currentThread().sleep(50);
        }

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");
        System.out.println("\n== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==");
        System.out.println("\n== BEGIN LOG user3 ==");
        user3.printLog();
        System.out.println("== END LOG user3 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST Broadcast Messages ===\n");
    }

    public static void testSelfUnicast() throws InterruptedException {
        System.out.println("=== BEGIN TEST Non-Member Broadcast ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        user1.login("User 1");
        user1.sendMessage("User 1", "Message 1");

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST Non-Member Broadcast ===\n");
    }

    public static void testNonMemberBroadcast() throws InterruptedException {
        System.out.println("=== BEGIN TEST Non-Member Broadcast ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        ChatUser user2 = new ChatUser(chatServer);
        user1.login("User 1");
        user2.login("User 2");
        user2.joinGroup("Group 1");
        user1.sendMessage("Group 1", "Message 1");

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");
        System.out.println("\n== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST Non-Member Broadcast ===\n");
    }

    public static void testFailUnicast() throws InterruptedException {
        System.out.println("=== BEGIN TEST Fail Unicast ===");
        ChatServer chatServer = new ChatServer();
        MessageDispatcher messageDispatcher = chatServer.getMessageDispatcher();
        chatServer.start();

        messageDispatcher.suspend();
        ChatUser user1 = new ChatUser(chatServer);
        ChatUser user2 = new ChatUser(chatServer);
        user1.login("User 1");
        user2.login("User 2");
        user1.sendMessage("User 2", "Message 1");
        user2.logout();
        messageDispatcher.resume();

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");
        System.out.println("\n== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Fail Unicast ===\n");
    }

    public static void testServerShutdown() throws InterruptedException {
        System.out.println("=== BEGIN TEST Server Shutdown ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        ChatUser user2 = new ChatUser(chatServer);
        ChatUser user3 = new ChatUser(chatServer);
        user1.login("User 1");
        user2.login("User 2");
        user2.joinGroup("Group 2");

        chatServer.shutdown();
        user1.sendMessage("User 2", "Message 1");
        user1.joinGroup("Group 1");
        user3.login("User 3");

        user1.logout();
        user2.leaveGroup("Group 2");

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");
        System.out.println("\n== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==");
        System.out.println("\n== BEGIN LOG user3 ==");
        user3.printLog();
        System.out.println("== END LOG user3 ==");

        System.out.println("=== END TEST Server Shutdown ===\n");
    }

    public static void testUserManager() throws InterruptedException {
        System.out.println("=== BEGIN TEST UserManager ===");
        ChatServer chatServer = new ChatServer();
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        ChatUser user2 = new ChatUser(chatServer);
        ChatUser user3 = new ChatUser(chatServer);
        ChatUser user4 = new ChatUser(chatServer);
        user1.login("User 1");
        user2.login("User 2");
        user3.login("User 3");

        user4.getUserCount();
        user4.getUserList();
        user4.getGroupCount();
        user4.getGroupList();

        user4.login("User 4");
        user4.logout();
        user4.getUserCount();
        user4.getUserList();
        user4.getGroupCount();
        user4.getGroupList();

        user1.getGroupUserList("Group 1");
        user1.leaveGroup("Group 1");
        user1.getGroupUserList("Group 1");
        user2.joinGroup("Group 1");
        user1.leaveGroup("Group 1");
        user1.getGroupUserList("Group 1");
        user2.leaveGroup("Group 1");
        user1.getGroupUserList("Group 1");

        user2.joinGroup("Group 1");
        user2.joinGroup("Group 2");
        user2.joinGroup("Group 3");
        user1.getGroupUserList("Group 1");
        user1.getGroupUserList("Group 2");
        user1.getGroupUserList("Group 3");
        user2.logout();
        user1.getGroupUserList("Group 1");
        user1.getGroupUserList("Group 2");
        user1.getGroupUserList("Group 3");

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==");
        System.out.println("\n== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==");
        System.out.println("\n== BEGIN LOG user3 ==");
        user3.printLog();
        System.out.println("== END LOG user3 ==");
        System.out.println("\n== BEGIN LOG user4 ==");
        user4.printLog();
        System.out.println("== END LOG user4 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST UserManager ===\n");
    }

    // Networked Server Tests (Project 2)

    public static void testNetworkLogin() throws Exception {
        System.out.println("=== BEGIN TEST Remote Login ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        Socket user1Socket = new Socket("localhost", 8080);
        ObjectOutputStream user1Requests = new ObjectOutputStream(user1Socket.getOutputStream());
        ObjectInputStream user1Responses = new ObjectInputStream(user1Socket.getInputStream());

        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGIN, "user1"));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        ChatUser user1 = chatServer.getUserManager().getUser("user1");

        user1Requests.writeObject(new ChatClientCommand(CommandType.JOIN_GROUP, "group1"));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.LEAVE_GROUP, "group1"));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGOUT));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.DISCONNECT));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        user1Requests.close();
        user1Responses.close();
        user1Socket.close();

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Remote Login ===\n");
    }

    public static void testNetworkLoginTimeout() throws Exception {
        System.out.println("=== BEGIN TEST Login Timeout ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        Socket user1Socket = new Socket("localhost", 8080);
        ObjectOutputStream user1Requests = new ObjectOutputStream(user1Socket.getOutputStream());
        ObjectInputStream user1Responses = new ObjectInputStream(user1Socket.getInputStream());

        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGIN, "user1"));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        ChatUser user1 = chatServer.getUserManager().getUser("user1");

        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGOUT));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        Thread.currentThread().sleep(21000);
        user1Requests.close();
        user1Responses.close();
        user1Socket.close();

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Login Timeout ===\n");
    }

    public static void testNetworkUnexpectedDisconnectBeforeLogin() throws Exception {
        System.out.println("=== BEGIN TEST Unexpected Disconnect Before Login ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        Socket user1Socket = new Socket("localhost", 8080);
        ObjectOutputStream user1Requests = new ObjectOutputStream(user1Socket.getOutputStream());
        ObjectInputStream user1Responses = new ObjectInputStream(user1Socket.getInputStream());

        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGIN, "user1"));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        ChatUser user1 = chatServer.getUserManager().getUser("user1");

        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGOUT));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        user1Requests.close();
        user1Responses.close();
        user1Socket.close();
        Thread.currentThread().sleep(50);

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Unexpected Disconnect Before Login ===\n");
    }

    public static void testNetworkUnexpectedDisconnectAfterLogin() throws Exception {
        System.out.println("=== BEGIN TEST Unexpected Disconnect After Login ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        Socket user1Socket = new Socket("localhost", 8080);
        ObjectOutputStream user1Requests = new ObjectOutputStream(user1Socket.getOutputStream());
        ObjectInputStream user1Responses = new ObjectInputStream(user1Socket.getInputStream());

        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGIN, "user1"));
        user1Requests.flush();
        Thread.currentThread().sleep(50);

        ChatUser user1 = chatServer.getUserManager().getUser("user1");

        user1Requests.close();
        user1Responses.close();
        user1Socket.close();
        Thread.currentThread().sleep(50);

        System.out.println("\n== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Unexpected Disconnect After Login ===\n");
    }

    public static void testNetworkLoginQueue() throws Exception {
        System.out.println("=== BEGIN TEST Login Queue ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        ChatUser[] users = new ChatUser[101];
        for (int i = 1; i <= 100; i++) {
            users[i] = new ChatUser(chatServer);
            users[i].login("user" + Integer.toString(i));
        }

        Socket user1Socket = new Socket("localhost", 8080);
        ObjectOutputStream user1Requests = new ObjectOutputStream(user1Socket.getOutputStream());
        ObjectInputStream user1Responses = new ObjectInputStream(user1Socket.getInputStream());

        System.out.println("\n== BEGIN Simulated Client ==");
        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGIN, "user101"));
        user1Requests.flush();
        System.out.println("-> " + CommandType.LOGIN);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);

        users[1].logout();
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);

        ChatUser user101 = chatServer.getUserManager().getUser("user101");

        user1Requests.writeObject(new ChatClientCommand(CommandType.DISCONNECT));
        user1Requests.flush();
        System.out.println("-> " + CommandType.DISCONNECT);
        Thread.currentThread().sleep(50);

        user1Requests.close();
        user1Responses.close();
        user1Socket.close();
        Thread.currentThread().sleep(50);
        System.out.println("== END Simulated Client ==\n");

        for (int i = 1; i <= 100; i++) {
            System.out.println("\n== BEGIN LOG user[" + Integer.toString(i) + "] ==");
            users[i].printLog();
            System.out.println("== END LOG user[" + Integer.toString(i) + "] ==");
        }
        System.out.println("\n== BEGIN LOG user101 ==");
        user101.printLog();
        System.out.println("== END LOG user101 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST Login Queue ===\n");
    }

    public static void testNetworkSendReceive() throws Exception {
        System.out.println("=== BEGIN TEST Send and Receive ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        Socket user1Socket = new Socket("localhost", 8080);
        ObjectOutputStream user1Requests = new ObjectOutputStream(user1Socket.getOutputStream());
        ObjectInputStream user1Responses = new ObjectInputStream(user1Socket.getInputStream());

        System.out.println("\n== BEGIN Simulated Client ==");
        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGIN, "user1"));
        user1Requests.flush();
        System.out.println("-> " + CommandType.LOGIN);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        ChatUser user1 = chatServer.getUserManager().getUser("user1");

        ChatUser user2 = new ChatUser(chatServer);
        user2.login("user2");

        user2.sendMessage("user1", "Unicast 1");
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.SEND_MESSAGE, "user2", 1, "Unicast 2"));
        user1Requests.flush();
        System.out.println("-> " + CommandType.SEND_MESSAGE);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.JOIN_GROUP, "group1"));
        user1Requests.flush();
        System.out.println("-> " + CommandType.JOIN_GROUP);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.SEND_MESSAGE, "group1", 1, "Broadcast 1"));
        user1Requests.flush();
        System.out.println("-> " + CommandType.SEND_MESSAGE);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.LEAVE_GROUP, "group1"));
        user1Requests.flush();
        System.out.println("-> " + CommandType.LEAVE_GROUP);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.SEND_MESSAGE, "group1", 1, "Broadcast 1"));
        user1Requests.flush();
        System.out.println("-> " + CommandType.SEND_MESSAGE);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGOUT));
        user1Requests.flush();
        System.out.println("-> " + CommandType.LOGOUT);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.DISCONNECT));
        user1Requests.flush();
        System.out.println("-> " + CommandType.DISCONNECT);
        Thread.currentThread().sleep(50);

        user1Requests.close();
        user1Responses.close();
        user1Socket.close();
        Thread.currentThread().sleep(50);
        System.out.println("== END Simulated Client ==\n");

        System.out.println("== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==\n");
        System.out.println("== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Send and Receive ===\n");
    }

    public static void testNetworkFailUnicast() throws Exception {
        System.out.println("=== BEGIN TEST Fail Unicast ===");
        ChatServer chatServer = new ChatServer(8080);
        MessageDispatcher messageDispatcher = chatServer.getMessageDispatcher();
        chatServer.start();

        Socket user1Socket = new Socket("localhost", 8080);
        ObjectOutputStream user1Requests = new ObjectOutputStream(user1Socket.getOutputStream());
        ObjectInputStream user1Responses = new ObjectInputStream(user1Socket.getInputStream());

        System.out.println("\n== BEGIN Simulated Client ==");
        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGIN, "user1"));
        user1Requests.flush();
        System.out.println("-> " + CommandType.LOGIN);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        ChatUser user1 = chatServer.getUserManager().getUser("user1");

        ChatUser user2 = new ChatUser(chatServer);
        user2.login("user2");

        messageDispatcher.suspend();
        user1Requests.writeObject(new ChatClientCommand(CommandType.SEND_MESSAGE, "user2", 1, "Unicast 1"));
        user1Requests.flush();
        System.out.println("-> " + CommandType.SEND_MESSAGE);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        user2.logout();
        messageDispatcher.resume();
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);

        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGOUT));
        user1Requests.flush();
        System.out.println("-> " + CommandType.LOGOUT);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);
        Thread.currentThread().sleep(50);

        user1Requests.writeObject(new ChatClientCommand(CommandType.DISCONNECT));
        user1Requests.flush();
        System.out.println("-> " + CommandType.DISCONNECT);
        Thread.currentThread().sleep(50);

        user1Requests.close();
        user1Responses.close();
        user1Socket.close();
        Thread.currentThread().sleep(50);
        System.out.println("== END Simulated Client ==\n");

        System.out.println("== BEGIN LOG user1 ==");
        user1.printLog();
        System.out.println("== END LOG user1 ==\n");
        System.out.println("== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Fail Unicast ===\n");
    }

    public static void testClientBasic() throws Exception {
        System.out.println("=== BEGIN TEST Client Basic ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        String commands = "" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "join group1\n" +
            "leave group1\n" +
            "join user1\n" +
            "leave user1\n" +
            "logout\n" +
            "disconnect";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
        Thread.currentThread().sleep(5000);

        chatServer.shutdown();
        System.out.println("=== END TEST Client Basic ===\n");
    }

    public static void testClientLogout() throws Exception {
        System.out.println("=== BEGIN TEST Client Logout ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        String commands = "" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "join group1\n" +
            "join group2\n" +
            "join group3\n" +
            "logout";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
        Thread.currentThread().sleep(2000);

        chatServer.shutdown();
        System.out.println("=== END TEST Client Logout ===\n");
    }

    public static void testClientDisconnect() throws Exception {
        System.out.println("=== BEGIN TEST Client Disconnect ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        String commands = "" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "join group1\n" +
            "join group2\n" +
            "join group3\n" +
            "disconnect";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
        Thread.currentThread().sleep(2000);

        chatServer.shutdown();
        System.out.println("=== END TEST Disconnect ===\n");
    }

    public static void testClientReconnect() throws Exception {
        System.out.println("=== BEGIN TEST Client Reconnect ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        String commands = "" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "join group1\n" +
            "leave group1\n" +
            "logout\n" +
            "disconnect\n" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "join group1\n" +
            "logout\n" +
            "disconnect";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
        Thread.currentThread().sleep(5000);

        chatServer.shutdown();
        System.out.println("=== END TEST Client Reconnect ===\n");
    }

    public static void testClientTimeout() throws Exception {
        System.out.println("=== BEGIN TEST Client Basic ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        String commands = "" +
            "connect localhost:8080\n" +
            "sleep 21000";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
        Thread.currentThread().sleep(22000);

        chatServer.shutdown();
        System.out.println("=== END TEST Client Basic ===\n");
    }

    public static void testClientLoginQueue() throws Exception {
        System.out.println("=== BEGIN TEST Client Login Queue ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        ChatUser[] users = new ChatUser[101];
        for (int i = 1; i <= 100; i++) {
            users[i] = new ChatUser(chatServer);
            users[i].login("user" + Integer.toString(i));
        }

        System.out.println("\n== BEGIN user101 ==");
        String commands = "" +
            "connect localhost:8080\n" +
            "login user101\n" +
            "sleep 1000\n" +
            "disconnect";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
        Thread.currentThread().sleep(500);

        users[1].logout();
        Thread.currentThread().sleep(1000);
        System.out.println("== END user101 ==");

        chatServer.shutdown();
        System.out.println("=== END TEST Client Login Queue ===\n");
    }

    public static void testClientGroupCapacity() throws Exception {
        System.out.println("=== BEGIN TEST Client Group Capacity ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        ChatUser[] users = new ChatUser[11];
        for (int i = 1; i <= 10; i++) {
            users[i] = new ChatUser(chatServer);
            users[i].login("user" + Integer.toString(i));
            users[i].joinGroup("group1");
        }

        System.out.println("\n== BEGIN user11 ==");
        String commands = "" +
            "connect localhost:8080\n" +
            "login user11\n" +
            "join group1\n" +
            "sleep 3000\n" +
            "join group1\n" +
            "disconnect";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();
        Thread.currentThread().sleep(1000);

        users[1].leaveGroup("group1");
        Thread.currentThread().sleep(3000);
        System.out.println("== END user101 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Client Group Capacity ===\n");
    }

    public static void testClientSendMsgFailNotify() throws Exception {
        System.out.println("=== BEGIN TEST Client Send Message Failure Notification ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        Socket user2Socket = new Socket("localhost", 8080);
        ChatUser user2 = new ChatUser(chatServer, user2Socket);
        //ChatUser user2 = new ChatUser(chatServer);
        user2.login("user2");
        user2.start();
        user2Socket.close();

        String commands = "" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "send user2 2 \"Hello user2!\"\n" +
            "send user3 3 \"Hello user3!\"\n" +
            "disconnect";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();

        Thread.currentThread().sleep(500);

        System.out.println("\n== BEGIN LOG user2 ==");
        user2.printLog();
        System.out.println("== END LOG user2 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Client Send Message Failure Notification ===");
    }

    public static void testClientDisconnectsWhileInLoginWaitQueue() throws Exception {
        System.out.println("=== BEGIN TEST Client Disconnects While In Login Wait Queue ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        ChatUser[] users = new ChatUser[101];
        for (int i = 1; i <= 100; i++) {
            users[i] = new ChatUser(chatServer);
            users[i].login("user" + Integer.toString(i));
        }

        Socket user1Socket = new Socket("localhost", 8080);
        ObjectOutputStream user1Requests = new ObjectOutputStream(user1Socket.getOutputStream());
        ObjectInputStream user1Responses = new ObjectInputStream(user1Socket.getInputStream());

        System.out.println("\n== BEGIN Simulated Client ==");
        user1Requests.writeObject(new ChatClientCommand(CommandType.LOGIN, "user101"));
        user1Requests.flush();

        System.out.println("-> " + CommandType.LOGIN);
        Thread.currentThread().sleep(50);
        System.out.println("<- " + ((ChatServerResponse)user1Responses.readObject()).responseType);

        user1Requests.close();
        user1Responses.close();
        user1Socket.close();

        Thread.currentThread().sleep(50);
        users[99].logout();

        ChatUser user101 = chatServer.getUserManager().getUser("user101");
        if (user101 == null)
            System.out.println("user101 was NOT added to chat server.  Hooray, test passed!");

        Thread.currentThread().sleep(50);
        System.out.println("== END Simulated Client ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Client Disconnects While In Login Wait Queue ===");
    }

    public static void testClientDoesNotNormallyTimeout() throws Exception {
        System.out.println("=== BEGIN TEST Client Does Not Normally Timeout ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        ChatUser[] users = new ChatUser[101];
        for (int i = 1; i <= 100; i++) {
            users[i] = new ChatUser(chatServer);
            users[i].login("user" + Integer.toString(i));
        }

        String commands = "" +
            "connect localhost:8080\n" +
            "login user101\n" +
            "sleep 25000\n" +
            "join group1\n" +
            "sleep 25000\n";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();

        Thread.currentThread().sleep(3000);

        users[100].logout();

        Thread.currentThread().sleep(55000);

        System.out.println("\n== BEGIN LOG user101 ==");
        ChatUser user101 = chatServer.getUserManager().getUser("user101");
        user101.printLog();
        System.out.println("== END LOG user101 ==\n");

        chatServer.shutdown();
        System.out.println("=== END TEST Client Does Not Normally Timeout ===\n");

    }

    public static void testClientDisconnectHandledAfterLogoff() throws Exception {
        System.out.println("=== BEGIN TEST Client Disconnect is Handled After Logout ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        String commands = "" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "logout\n";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();

        Thread.currentThread().sleep(21000);
        chatServer.shutdown();
        System.out.println("=== END TEST Client Disconnect is Handled After Logout ===");
    }

    public static void testTimerRestartedAfterEveryFailedLoginAttempt() throws Exception {
        System.out.println("=== BEGIN TEST Timer is Restarted After Every Failed Login Attempt ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        ChatUser user;
        for(int i=1; i<=3; i++) {
            user= new ChatUser(chatServer);
            user.login("user" + Integer.toString(i));
        }

        String commands = "" +
            "connect localhost:8080\n" +
            "sleep 19000\n" +
            "login user1\n" +
            "sleep 19000\n" +
            "login user2\n" +
            "sleep 19000\n" +
            "login user3\n" +
            "login user4\n" +
            "logout\n";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();

        Thread.currentThread().sleep(60000);
        chatServer.shutdown();

        System.out.println("=== END TEST Timer is Restarted After Every Failed Login Attempt ===");
    }

    public static void testCertainClientCommandsRejectedWhenNotConnectedOrLoggedIn() throws Exception {
        System.out.println("=== BEGIN TEST Reject Certain Client Commands When Disconnected or Not Logged In ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        ChatUser user1 = new ChatUser(chatServer);
        user1.login("user1");

        String commands = "" +
            //"login user2\n" +
            //"join group1\n" +
            //"leave group1\n" +
            //"send user1 1 \"Hello user1!\"\n" +
            "disconnect\n" +
            "sleep 500\n" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "join group1\n" +
            "leave group1\n" +
            "send user2 1 \"Hello user1!\"\n" +
            "disconnect\n";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();

        Thread.currentThread().sleep(20000);
        chatServer.shutdown();

        System.out.println("=== BEGIN TEST Reject Certain Client Commands When Disconnected or Not Logged In ===");

    }

    public static void testInvalidClientCommandsAreSkipped() throws Exception {
        System.out.println("=== BEGIN TEST Invalid Client Commands Are Gracefully Skipped ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        String commands = "" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "joing group1\n" +
            "leeve group1\n" +
            "joing group1\n" +
            "join group1\n" +
            "loggouts\n" +
            "Chuck Norris\n" +
            "logout\n" +
            "disconnect\n";
        BufferedReader input = new BufferedReader(new StringReader(commands));
        PrintWriter output = new PrintWriter(System.out, true);
        ChatClient chatClient = new ChatClient(input, output);
        chatClient.start();

        Thread.currentThread().sleep(5000);
        chatServer.shutdown();
        System.out.println("=== BEGIN TEST Invalid Client Commands Are Gracefully Skipped ===");
    }
*/

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

    public static void logUserRegistration(String username, Date time){
    }

    public static void logUserRegistrationFailed(String username, Date time){
    }
}

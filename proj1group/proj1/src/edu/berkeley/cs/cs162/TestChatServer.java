package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.Date;

public class TestChatServer {

	public static void main(String[] args) throws Exception {
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
        //testServerShutdown();
        //testUserManager();

        // Networked Tests
        //testNetworkLogin();
        //testNetworkLoginTimeout();
        //testNetworkUnexpectedDisconnectBeforeLogin();
        //testNetworkUnexpectedDisconnectAfterLogin();
        //testNetworkSendReceive();

        // Client-Server Tests
        //testClientBasic();
        //testClientTimeout();
        // testClientLoginQueue();  // NOT PASSING

        // Group Tests
        //testClientJoinGroup();
        //testClientGroupCapacity();
        testClientRetryGroupJoin();   // NOT PASSING
	}

    /* Non-Networked Server Tests (Project 1) */

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

    /* Networked Server Tests (Project 2) */

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

        BufferedReader input;
        PrintWriter output;
        String commands;

        ChatClient[] clients = new ChatClient[102];
        for (int i = 1; i <= 101; i++) {
            commands = "" +
            "connect localhost:8080\n" +
            "login user" + i + "\n";

            if (i == 100)
                commands += "" +
                "sleep 2000\n" +
                "logout\n";
            if (i == 101)
                commands += "" +
                "sleep 22000\n" +
                "join group1\n";

            input = new BufferedReader(new StringReader(commands));
            output = new PrintWriter(System.out, true);
            clients[i] = new ChatClient(input, output);
            clients[i].start();
        }

        Thread.currentThread().sleep(10000);
        chatServer.shutdown();

        System.out.println("=== END TEST Client Login Queue ===\n");
    }

    public static void testClientJoinGroup() throws Exception {
        System.out.println("=== BEGIN TEST Client Join Group ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        String commands1 = "" +
            "connect localhost:8080\n" +
            "login user1\n" +
            "join group1\n";
        BufferedReader input1 = new BufferedReader(new StringReader(commands1));
        PrintWriter output1 = new PrintWriter(System.out, true);
        ChatClient chatClient1 = new ChatClient(input1, output1);

        String commands2 = "" +
            "connect localhost:8080\n" +
            "login user2\n" +
            "join group1\n";
        BufferedReader input2 = new BufferedReader(new StringReader(commands2));
        PrintWriter output2 = new PrintWriter(System.out, true);
        ChatClient chatClient2 = new ChatClient(input2, output2);

        chatClient1.start();
        chatClient2.start();
        Thread.currentThread().sleep(5000);

        chatServer.shutdown();
        System.out.println("=== END TEST Client Join Group ===\n");
    }

    public static void testClientGroupCapacity() throws Exception {
        System.out.println("=== BEGIN TEST Client Group Capacity ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        BufferedReader input;
        PrintWriter output;
        String commands;

        ChatClient[] clients = new ChatClient[12];
        for (int i = 1; i <= 11; i++) {
            commands = "" +
            "connect localhost:8080\n" +
            "login user" + i + "\n" +
            "join group1\n";
            input = new BufferedReader(new StringReader(commands));
            output = new PrintWriter(System.out, true);
            clients[i] = new ChatClient(input, output);

            clients[i].start();
        }

        Thread.currentThread().sleep(5000);
        chatServer.shutdown();
        System.out.println("=== END TEST Client Group Capacity ===\n");
    }

    public static void testClientRetryGroupJoin() throws Exception {
        System.out.println("=== BEGIN TEST Client Retry Group Join ===");
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();

        BufferedReader input;
        PrintWriter output;
        String commands;

        ChatClient[] clients = new ChatClient[12];
        for (int i = 1; i <= 11; i++) {
            commands = "" +
            "connect localhost:8080\n" +
            "login user" + i + "\n" +
            "join group1\n";

            if (i == 10 || i == 9)
                commands += "" +
                "sleep 5000\n" +
                "leave group1\n";
            if (i == 11)
                commands += "" +
                "sleep 8000\n" +
                "join group1\n";

            input = new BufferedReader(new StringReader(commands));
            output = new PrintWriter(System.out, true);
            clients[i] = new ChatClient(input, output);

            clients[i].start();

        }

        Thread.currentThread().sleep(10000);
        chatServer.shutdown();

        System.out.println("=== END TEST Client Retry Group Join ===\n");
    }

    public static void testClientSleep() throws Exception {}




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

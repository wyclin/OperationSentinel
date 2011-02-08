package edu.berkeley.cs.cs162;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class TestChatServer {
	
	public static void main(String [] args) throws InterruptedException {
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
		System.out.println("done \n");
	}
}

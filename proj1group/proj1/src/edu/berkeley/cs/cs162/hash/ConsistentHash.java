
package edu.berkeley.cs.cs162.hash;

import java.util.*;
import java.security.*;

public class ConsistentHash<T> {
	
	private final int numberOfReplicas;
	private final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();

    public ConsistentHash() {
        this.numberOfReplicas = 200;
    }
	
	public ConsistentHash(int numberOfReplicas, Collection<T> nodes) {
		this.numberOfReplicas = numberOfReplicas;
		for (T node : nodes) {
			add(node);
		}
	}
	
	public void add(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.put(MD5.hash(node.toString() + i), node);
		}
	}
	
	public void remove(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.remove(MD5.hash(node.toString() + i));
		}
	}

    public void clear() {
        circle.clear();
    }
	
	public T get(String key) {
		if (circle.isEmpty()) {
			return null;
		}
		int hash = MD5.hash(key);
		if (!circle.containsKey(hash)) {
			SortedMap<Integer, T> tailMap = circle.tailMap(hash);
			hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		}
		return circle.get(hash);
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
        
		String[] clients={"chanan","tapan","Amar","santosh","deepak", "philip","vinson","wayne","benson","jacky"};
		
		ArrayList<String> myServers = new ArrayList<String>();
		
		myServers.add("ec2-10-16-127-141.compute-1.amazonaws.com:8080");
		myServers.add("ec2-50-16-127-143.compute-1.amazonaws.com:9463");
		myServers.add("ec2-50-16-147-141.compute-1.amazonaws.com:4747");
		
		
		ConsistentHash<String> ring = new ConsistentHash<String>(200, myServers);
		System.out.println();
		
		for (String client : clients){
			System.out.println(client + " connects to " + ring.get(client));
		}
		
		
		System.out.println();
		System.out.println("===== now server 8080 is down ======");
		ring.remove("ec2-10-16-127-141.compute-1.amazonaws.com:8080");
		
		for (String client : clients){
			System.out.println(client + " connects to " + ring.get(client));
		}
		
		System.out.println();
		System.out.println("===== now server 8080 is back ======");
		ring.add("ec2-10-16-127-141.compute-1.amazonaws.com:8080");
		for (String client : clients){
			System.out.println(client + " connects to " + ring.get(client));
		}
		System.out.println();
	}
	
}
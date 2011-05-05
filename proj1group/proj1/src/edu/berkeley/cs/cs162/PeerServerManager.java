package edu.berkeley.cs.cs162;

import edu.berkeley.cs.cs162.hash.ConsistentHash;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PeerServerManager {

    private HashMap<String, PeerServer> peerServers;
    private ConsistentHash<String> consistentHash;
    private ReentrantReadWriteLock rwLock;
    private String serverName;

    public PeerServerManager(String serverName) {
        this.peerServers = new HashMap<String, PeerServer>();
        this.consistentHash = new ConsistentHash<String>();
        this.rwLock = new ReentrantReadWriteLock();
        this.serverName = serverName;
        this.consistentHash.add(serverName);
    }

    public void shutdown() {
        rwLock.readLock().lock();
        HashSet<PeerServer> servers = new HashSet<PeerServer>(peerServers.values());
        rwLock.readLock().unlock();
        for (PeerServer server : servers) {
            server.shutdown();
        }
        for (PeerServer server : servers) {
            try {
                server.join();
            } catch (InterruptedException e) {}
        }
    }

    public String getServerName() {
        return serverName;
    }

    public HashSet<PeerServer> getServers() {
        HashSet<PeerServer> result;
        rwLock.readLock().lock();
        result = new HashSet<PeerServer>(peerServers.values());
        rwLock.readLock().unlock();
        return result;
    }

    public void addServer(PeerServer server) {
        rwLock.writeLock().lock();
        peerServers.put(server.getServerName(), server);
        consistentHash.add(server.getServerName());
        rwLock.writeLock().unlock();
    }

    // For debugging
    public void addName(String name) {
        rwLock.writeLock().lock();
        consistentHash.add(name);
        rwLock.writeLock().unlock();
    }

    public void removeServer(PeerServer server) {
        rwLock.writeLock().lock();
        peerServers.remove(server.getServerName());
        consistentHash.remove(server.getServerName());
        rwLock.writeLock().unlock();
    }

    public String findUser(String userName) {
        String result;
        rwLock.readLock().lock();
        result = consistentHash.get(userName);
        rwLock.readLock().unlock();
        return result;
    }
}

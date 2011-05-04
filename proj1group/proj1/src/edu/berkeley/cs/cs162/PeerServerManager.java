package edu.berkeley.cs.cs162;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PeerServerManager {

    private HashMap<String, PeerServer> peerServers;
    private ReentrantReadWriteLock rwLock;

    public PeerServerManager() {
        this.peerServers = new HashMap<String, PeerServer>();
        this.rwLock = new ReentrantReadWriteLock();
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

    public void addServer(PeerServer server) {
        peerServers.put(server.getServerName(), server);
    }
}

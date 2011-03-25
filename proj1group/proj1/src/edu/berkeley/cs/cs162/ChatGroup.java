package edu.berkeley.cs.cs162;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;

public class ChatGroup {

    public final String name;
    public HashMap<String, ChatUser> users;

    public ChatGroup(String groupName) {
        this.name = groupName;
        this.users = new HashMap<String, ChatUser>();
    }
}

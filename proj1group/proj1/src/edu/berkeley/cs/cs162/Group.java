package edu.berkeley.cs.cs162;

import java.util.Set;
import java.util.HashMap;

public class Group{
    private HashMap<String, BaseUser> users;
    private String name;

            
    public Group(String groupName) {
		this.name = groupName;
		users = new HashMap<String, BaseUser>();
    }

    public Group(String groupName, int maxNumUsers){}

    /** Add a user object to the group. */	
    public boolean addUser(BaseUser user){
		if (!this.isFull() || this.hasUser(user.getName())){
			return false;
		}
		users.put(user.getName(), user);
		return true;
	}
	
    /** Remove user with given name from the group. */
    public void removeUser(String username){
		if (!users.containsKey(username)){
			return;
		}
		users.remove(username);
    }
    
    /** Returns Set of all usernames in the group. */
    public Set<String> listUsers(){
		return users.keySet();
    }


    /** Returns number of users in the group. */	
    public int numUsers(){
		return users.size();
    }

    /** Sends the given message to all users in the group. */
    public void messageUsers(Message message){
           for (BaseUser user: users.values()) {
	   	user.msgReceived(message.printable());
	   }
    }

	
    /** Returns true if group contains user with given username. */
    public boolean hasUser(String username) {
		return users.containsKey(username); 
    }
	
    /** Returns true if group is at maximum capacity. */	
    public boolean isFull(){
		return this.numUsers() >= ChatServer.MAX_GROUP_USERS;
    }

}

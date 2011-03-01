package edu.berkeley.cs.cs162;

import java.util.Set;
import java.util.HashMap;

class UserManager{
    private HashMap<String, BaseUser> users;
    private HashMap<String, Group> groups;
    public ChatServer myServer;
	
    public UserManager(ChatServer chatServer){
		this.myServer = chatServer;
		users = new HashMap<String, BaseUser>();
		groups = new HashMap<String, Group>();  
    }

    /* Attempts to add user with @username.*/
    synchronized public LoginError addUser(String username){
	if (getNumUsers() >= ChatServer.MAX_CHAT_USERS) {
		return LoginError.USER_DROPPED;
	} else
	if (hasName(username)) {
		return LoginError.USER_REJECTED;
	} else {
		BaseUser newUser = new BaseUser(username, myServer);
		users.put(username, newUser);
		return LoginError.USER_ACCEPTED;
	}
    }

    /* Returns true if we successfully remove with user with username.*/ 
    synchronized public boolean removeUser(String username){
    	if (hasName(username)) {
		users.remove(username);
		for (String groupName: groups.keySet()) {
			removeUserFromGroup(username, groupName);
		}
		return true;
	} else {
		return false;
	}
    }
    

    /* Returns true if createGroup is successful.*/
    synchronized public boolean createGroup(String groupName){
	if (!hasName(groupName)) {
		Group newGroup = new Group(groupName);
		groups.put(groupName, newGroup);
		return true;
	} else {
		return false;
	}
    }

    /* Returns true if a group is successfully deleted. */
    synchronized public boolean deleteGroup(String groupName){
	    if (!hasGroup(groupName)){
		    return false;
	    } else {
		    Group groupToRemove = groups.get(groupName);
		    if (groupToRemove.numUsers() > 0) {
			    return false;
		    } else {
			    groups.remove(groupName);
			    return true;
		    }
	    }
    }


    /* Returns true if user is successfully added to group. Returns false if group does not exist. */
    synchronized public boolean addUserToGroup(BaseUser user, String groupName) {
	    if (!hasGroup(groupName)) {
		    return false;
	    } else {
		    Group targetGroup = groups.get(groupName);
		    String username = user.getUsername();

		    if (targetGroup.hasUser(username)) {
			    return false;
		    }
		    if (targetGroup.isFull()) {
			    return false;
		    }
		    
		   return targetGroup.addUser(user);
	    }
    }

    /* Returns true if user is successfully removed from group.*/
    synchronized public boolean removeUserFromGroup(String username, String groupName){
	    
	    if (!hasGroup(groupName)) {
		    return false;
	    }

	    Group targetGroup = groups.get(groupName);
	    if (!targetGroup.hasUser(username)) {
		    return false;
	    }
	    
	    targetGroup.removeUser(username);
	    if (targetGroup.numUsers() <= 0) {
		    deleteGroup(groupName);
	    }
	    return true;
    }

    /* Returns a set contains the names of all groups currently on the server. */
    synchronized public Set<String> listGroups(){
	    return groups.keySet();
    }


    /* Returns a set containing the names of all users in a group with groupName. */
    synchronized public Set<String> listUsersOfGroup(String groupName){
	    if (!hasGroup(groupName)) {
		    return null;
	    } else {
		    return groups.get(groupName).listUsers();
	    }
    }

    /* Returns the BaseUser object with given username */
    public BaseUser getUser(String username){
	   return users.get(username);
    }

    /* Returns a set of the usernames currently on the server. */
    public Set<String> getUserList(){
	    return users.keySet();
    }

    /* Removes all users and all groups. */
    public void removeAll(){
	    users.clear();
	    groups.clear();
    }

    /* Returns true if a user with the username exists. */
    public boolean hasUser(String username) {
	    return users.containsKey(username);
    }

    /* Returns true if a group with the group name exists. */
    public boolean hasGroup(String groupName) {
	    return groups.containsKey(groupName);
    }

    /* Returns true if a group OR user with the give name exists. */
    public boolean hasName(String name) {
	    return hasGroup(name) || hasUser(name);
    }

    /* Return num users on the server. */
    public int getNumUsers(){
	    return users.size();
    }

    // DO WE NEED THESE?
    //public boolean start(){}
    //public void stop(){}

}

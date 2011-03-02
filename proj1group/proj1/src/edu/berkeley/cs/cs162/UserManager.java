package edu.berkeley.cs.cs162;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class UserManager {
    public ChatServer chatServer;
    private HashMap<String, BaseUser> users;
    private HashMap<String, Group> groups;
    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	
    public UserManager(ChatServer chatServer) {
        this.chatServer = chatServer;
        this.users = new HashMap<String, BaseUser>();
        this.groups = new HashMap<String, Group>();
    }

    /* Attempts to add user with @userName. */
    public LoginError addUser(String userName) {
        rwLock.writeLock().lock();
        LoginError result;
        if (getNumUsers() >= ChatServer.MAX_CHAT_USERS) {
            TestChatServer.logUserLoginFailed(userName, Calendar.getInstance().getTime(), LoginError.USER_DROPPED);
            result = LoginError.USER_DROPPED;
        } else if (hasName(userName)) {
            TestChatServer.logUserLoginFailed(userName, Calendar.getInstance().getTime(), LoginError.USER_REJECTED);
            result = LoginError.USER_REJECTED;
        } else {
            BaseUser newUser = new BaseUser(userName, chatServer);
            users.put(userName, newUser);
            TestChatServer.logUserLogin(userName, Calendar.getInstance().getTime());
            result = LoginError.USER_ACCEPTED;
        }
        rwLock.writeLock().unlock();
        return result;
    }

    /* Returns true if we successfully remove with user with userName. */
    public boolean removeUser(String userName) {
        rwLock.writeLock().lock();
        boolean result;
        if (hasUser(userName)) {
            users.remove(userName);
            for (String groupName: groups.keySet()) {
                removeUserFromGroup(userName, groupName);
            }
            TestChatServer.logUserLogout(userName, Calendar.getInstance().getTime());
            result = true;
        } else {
            result = false;
	    }
        rwLock.writeLock().unlock();
        return result;
    }
    

    /* Returns true if createGroup is successful. */
    public boolean createGroup(String groupName) {
        rwLock.writeLock().lock();
        boolean result;
        if (!hasName(groupName)) {
            Group newGroup = new Group(groupName);
            groups.put(groupName, newGroup);
            result = true;
        } else {
            result = false;
        }
        rwLock.writeLock().unlock();
        return result;
    }

    /* Returns true if a group is successfully deleted. */
    public boolean deleteGroup(String groupName) {
        rwLock.writeLock().lock();
        boolean result;
        if (!hasGroup(groupName)){
            result = false;
        } else {
            Group groupToRemove = groups.get(groupName);
            if (groupToRemove.numUsers() > 0) {
               result = false;
            } else {
                groups.remove(groupName);
                result = true;
            }
        }
        rwLock.writeLock().unlock();
        return result;
    }

    /* Returns true if user is successfully added to group. Returns false if group does not exist. */
    public boolean addUserToGroup(BaseUser user, String groupName) {
        rwLock.writeLock().lock();
        boolean result;
        if (!hasGroup(groupName)) {
            result = false;
        } else {
            Group targetGroup = groups.get(groupName);
            result = targetGroup.addUser(user);
        }
        rwLock.writeLock().unlock();
        return result;
    }

    /* Returns true if user is successfully removed from group.*/
    public boolean removeUserFromGroup(String userName, String groupName) {
        rwLock.writeLock().lock();
        boolean result;
        if (hasGroup(groupName)) {
            Group targetGroup = groups.get(groupName);
            if (targetGroup.hasUser(userName)) {
                targetGroup.removeUser(userName);
                if (targetGroup.numUsers() <= 0) {
                    deleteGroup(groupName);
                }
                result = true;
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        rwLock.writeLock().unlock();
        return result;
    }

    /* Removes all users and all groups. */
    public void removeAll() {
        rwLock.writeLock().lock();
        users.clear();
        groups.clear();
        rwLock.writeLock().unlock();
    }

    /* Returns true if a user with the userName exists. */
    public boolean hasUser(String userName) {
        rwLock.readLock().lock();
        boolean result;
        result = users.containsKey(userName);
        rwLock.readLock().unlock();

        return result;
    }

    /* Returns true if a group with the group name exists. */
    public boolean hasGroup(String groupName) {
        rwLock.readLock().lock();
        boolean result;
        result = groups.containsKey(groupName);
        rwLock.readLock().unlock();

        return result;
    }

    /* Returns true if a group OR user with the give name exists. */
    public boolean hasName(String name) {
        rwLock.readLock().lock();
        boolean result;
        result = hasGroup(name) || hasUser(name);
        rwLock.readLock().unlock();

        return result;
    }

    /* Returns a set contains the names of all groups currently on the server. */
    public Set<String> listGroups() {
        rwLock.readLock().lock();
        Set<String> result;
        result = groups.keySet();
        rwLock.readLock().unlock();

        return result;
    }


    /* Returns a set containing the names of all users in a group with groupName. */
    public Set<String> listUsersOfGroup(String groupName) {
        rwLock.readLock().lock();
        Set<String> result = null;
        if (hasGroup(groupName)) {
            result = groups.get(groupName).listUsers();
        }
        rwLock.readLock().unlock();

        return result;
    }

    /* Returns the BaseUser object with given userName */
    public BaseUser getUser(String userName) {
        rwLock.readLock().lock();
        BaseUser result;
        result = users.get(userName);
        rwLock.readLock().unlock();

        return result;
    }

    /* Returnss the Group object with the given groupName */
    public Group getGroup(String groupName) {
        rwLock.readLock().lock();
        Group result;
        result = groups.get(groupName);
        rwLock.readLock().unlock();
        return result;
    }

    /* Returns a set of the userNames currently on the server. */
    public Set<String> listUsers() {
        rwLock.readLock().lock();
        Set<String> result;
        result = users.keySet();
        rwLock.readLock().unlock();

        return result;
    }

    /* Return num users on the server. */
    public int getNumUsers() {
        rwLock.readLock().lock();
        int result;
        result = users.size();
        rwLock.readLock().unlock();

        return result;
    }
	
    /* Return num users on the server. */
    public int getNumGroups() {
        rwLock.readLock().lock();
        int result;
        result = groups.size();
        rwLock.readLock().unlock();

        return result;
    }
}

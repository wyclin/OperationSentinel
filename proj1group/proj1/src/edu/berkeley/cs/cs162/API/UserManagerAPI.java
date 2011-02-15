package edu.berkeley.cs.cs162

public class UserManager

Constructor Detail
    public UserManager()
		Creates and initializes an instance of UserManager

Method Detail
    public boolean addUser(String username)
		Adds a new user to the chat server.
		Parameters:
			username - the user name of the User to be create and added into the chat server.
		Returns:
			true, if the user was successfully added to chat server.

	public boolean addUserToGroup(BaseUser user, String groupname)
		Adds a user to a chat group.
		Parameters:
			user - the object representing the User that wants to join the group.
			groupname - the name of the group.
		Returns:
			true, if the user was successfully added to the chat group.

	public void removeUserFromGroup(BaseUser, String groupname)
		Adds a user to a chat group.
		Parameters:
			user - the object representing the User that wants to leave the group.
			groupname - the name of the group.

	public void listGroups()
		Lists the groups present on chat server.
		Returns:
			a list of groups, along with their descriptions and attributes.

	public void createGroup(String groupname)
		Creates a new chat group in the chat server.
		Parameters:
			groupname - the name assigned to the group after group creation.


	public void deleteGroup(String groupname)
		Deletes a chat group from the chat server.
		Parameters:
			groupname - the name of the group.

	public String listUsersOfGroup(String groupname)
		Lists the users in a group.
		Parameters:
			groupname - the name of the group.
		Returns:
			a list of users that belong in the group, with additional descriptions and attributes.

	public void removeUser(String username)
		Removes a user from chat server
		Parameters:
			username - the name of the user.

	public BaseUser getUserByName(String username)
		Finds and returns a user by username
		Parameters:
			username - the name of the user.
		Returns:
			a BaseUser, or null if no user with the specified username is found.
		
	public int getNumberOfUsers()
		Returns the number of users logged into the server.
		Returns:
			the number of users logged into the server.

	public ArrayList getListOfUsers()
		Returns a list of users logged into the server.
		Returns:
			an ArrayList of all BaseUsers logged into the server.

	public boolean start()
		Starts up the user manager.

	public void stop()
		Terminates the user manager.


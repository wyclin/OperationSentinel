package edu.berkeley.cs.cs162

public class Group

Constructor Detail
	public Group()
		Cover method, using 10 as the default max number of users 
    public Group(int maxNumUsers)
		Creates and initializes an instance of Group with a specified max number of users.
		Parameters:
			maxNumUsers - the max number of users allowed in the group.

Field Detail
	public final NAME
		The name of the group.
			
Method Detail
    public boolean addUser(BaseUser user)
		Adds a new user into group.
		Parameters:
			user - the user object to be added into the group.
		Returns:
			true, if the user was successfully added into the group.

    public void removeUser(BaseUser user)
		Adds a new user into group.
		Parameters:
			user - the user object to be removed from the group.

    public String listUsers()
		Lists the users in the group.
		Returns:
			a list of users by name and including description.

    public int userCount()
    	Returns the number of users in the group.
		Returns:
			the number of users in the group.

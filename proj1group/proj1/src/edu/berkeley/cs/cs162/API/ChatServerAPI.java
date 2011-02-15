package edu.berkeley.cs.cs162

public class ChatServer 
extends Thread 
implements ChatServerInterface 

Constructor Detail
	public ChatServer()
		Creates and initializes an instance of ChatServer

Method Detail
	public LoginError login(String username)
		Logs a new user into the chat server, subject to the constraint that user name 
		shall be unique and max number of user is not reached.
		Specified by: 
			login in interface ChatServerInterface
		Parameters:
			username - the user name of the User to be logged in.
		Returns:
			a LoginError message describing success or one of the failure modes.

	public boolean logoff(String username)
		Logs a specified user out of the chat server.
		Specified by: 
			logoff in interface ChatServerInterface
		Parameters:
			username - the user name of the User to be logged off.
		Returns:
			true if the logoff was successful.

	public boolean joinGroup(BaseUser user, 
							 String groupname)
		Adds user into a chat group named groupname. Creates new group if no such group named groupname exists.
		Specified by: 
			joinGroup in interface ChatServerInterface
		Parameters:
			user - the object representing the User that wants to join the group.
			groupname - the name of the group.
		Returns:
			true if the user was successfully added into group.

	public boolean leaveGroup(BaseUser user, String groupname)
		Removes user from chat group.  Deletes the group if group is empty afterwards.
		Specified by: 
			leaveGroup in interface ChatServerInterface
		Parameters:
			user - the object representing the User that wants to leave the group.
			groupname - the name of the group.
		Returns:
			true if the user was successfully removed from group.
			
	public void shutdown()
		Shuts down the chat server and kills all subserver threads.
		Specified by: 
			shutdown in interface ChatServerInterface
		
	public BaseUser getUser(String username)
		Fetches the user with username username from the pool of users.
		Specified by: 
			getUser in interface ChatServerInterface
		Returns:
			the user with username username, or null if no such user exists.
			
    public void start()
		Starts up the chat server and spawns the MessageDispatcher and UserManager.

    private void createGroup(String groupname)
		Creates a new chat group.
		Parameters:
			groupname - the name assigned to the group after group creation.

    public void destroyGroup(String groupname)
		Destroys a chat group.
		Parameters:
			groupname - the name of the group.

    public void send(Message message)
		Receives a message and passes it to the MessageDispatcher.
		Parameters:
			message - the Message object to be passed to the MessageDispatcher.

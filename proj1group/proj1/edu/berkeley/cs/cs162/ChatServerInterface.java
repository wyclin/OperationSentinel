package edu.berkeley.cs.cs162;



public interface ChatServerInterface {
	
	/**
	 * @param username  Login to the chat server using a specified user name, 
	 * 					subject to the constraint that user name shall be unique and
	 * 					max number of user is not reached.
	 * @return LoginError Indicate success or one of the predefined failure modes.
	 */
	public abstract LoginError login(String username);

	/**
	 * @param username  Disconnect the specified user from the chat server.
	 * @return Whether logging off was successful or not
	 */
	public abstract boolean logoff(String username);

	/**
	 * @param user The user joining a group
	 * @param groupname a unique group name, if the group does not exist yet, create a group.
	 * @return whether the joining was successful.
	 */
	public abstract boolean joinGroup(BaseUser user, String groupname);

	/**
	 * @param user The user leaving a group
	 * @param groupname a unique group name, must already exist.
	 * @return whether leaving the group was successful. Fail if the group does not exist or the user is not in the group.
	 */
	public abstract boolean leaveGroup(BaseUser user, String groupname);

	/**
	 * shut down the server. Disconnect all users and finish sending all messages. 
	 * Also stop accepting new messages.
	 */
	public abstract void shutdown();
	
	/**
	 * @param username
	 * @return The object BaseUser if it exist, otherwise null is returned
	 */
	public abstract BaseUser getUser(String username);

}
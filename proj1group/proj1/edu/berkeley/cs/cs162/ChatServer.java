package edu.berkeley.cs.cs162;


public class ChatServer extends Thread implements ChatServerInterface {

	@Override
	public LoginError login(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean logoff(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean joinGroup(BaseUser user, String groupname) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean leaveGroup(BaseUser user, String groupname) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BaseUser getUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}
}

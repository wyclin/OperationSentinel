package edu.berkeley.cs.cs162;

/**
 * This is the core of the chat server.  Put the management of groups
 * and users in here.  You will need to control all of the threads,
 * and respond to requests from the test harness.
 *
 * It must implement the ChatServerInterface Interface, and you should
 * not modify that interface; it is necessary for testing.
 */

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
		// shutdown msgdispatcher and usermanager
	}

	@Override
	public BaseUser getUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

    public void start(){}//start msgdispatcher and usermanager
    private void createGroup(){}
    public void destroyGroup(){}
    public void send(Message message){}
}

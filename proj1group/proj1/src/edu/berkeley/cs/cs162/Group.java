package edu.berkeley.cs.cs162;

class Group{
    private HashMap<String, BaseUser> users;
    private String name;
        
    public Group(String groupName) {
		this.name = groupName;
		users = new HashMap<String, BaseUser>;
    }

    public Group(String groupName, int maxNumUsers){}

	
    public boolean addUser(BaseUser user){
		if (!this.isFull() || this.hasUser()){
			return false;
		}
		users.put(user.getName(), user);
		return true;
	}
	
    public void removeUser(String username){
		if (!users.ContainsKey(username)){
			return false;
		}
		users.remove(username);
	}
    
	public Set<String> listUsers(){
		return users.keySet()
	}
	
    public int numUsers(){
		return users.size();
	}
	
    public boolean hasUser(String username) {
		return return containsKey(username); 
	}
	
    public boolean isFull(){
		return this.numUsers() >= MAX_GROUP_USERS;
	}

}

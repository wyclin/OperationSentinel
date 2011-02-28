class Group{
    private HashMap<String, User> users;
    private String name;
        
    public Group(String groupName) {
	this.name = groupName;
    }

    public Group(String groupName, int maxNumUsers){}

    public boolean addUser(BaseUser user){}
    public void removeUser(String username){}
    public void listUsers(){}
    public void numUsers(){}
    public boolean hasUser(String username) {}
    public boolean isFull(){}

}

class Message{
    public String receiver, sender, text;
    public TimeStamp timeStamp;

    Message(String sender, String receiver, String text){
	this.receiver = receiver;
	this.sender = sender;
	this.text = text;
	this.TimeStamp = new TimeStamp(currentTimeMillis());
    }
}
package edu.berkeley.cs.cs162;

public class Action {
    public ChatUserAction action;
    public String string;
    public Message message;

    Action(ChatUserAction action) {
        this.action = action;
    }

    Action(ChatUserAction action, String userName) {
        this.action = action;
        this.string = userName;
    }

    Action(ChatUserAction action, Message message) {
        this.action = action;
        this.message = message;
    }
}

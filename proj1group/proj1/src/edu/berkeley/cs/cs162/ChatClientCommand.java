package edu.berkeley.cs.cs162;

import java.io.Serializable;

public class ChatClientCommand implements Serializable {

    public CommandType commandType;
    public String string;
    public Message message;

    ChatClientCommand(CommandType commandType) {
        this.commandType = commandType;
    }

    ChatClientCommand(CommandType commandType, String userName) {
        this.commandType = commandType;
        this.string = userName;
    }

    ChatClientCommand(CommandType commandType, Message message) {
        this.commandType = commandType;
        this.message = message;
    }
}

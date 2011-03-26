package edu.berkeley.cs.cs162;

import java.io.Serializable;

public class ChatClientCommand implements Serializable {

    public CommandType commandType;
    public String string;
    public int number;
    public Message message;

    ChatClientCommand(CommandType commandType) {
        this.commandType = commandType;
    }

    ChatClientCommand(CommandType commandType, String string, int number) {
        this.commandType = commandType;
        this.string = string;
        this.number = number;
    }

    ChatClientCommand(CommandType commandType, String string) {
        this.commandType = commandType;
        this.string = string;
    }

    ChatClientCommand(CommandType commandType, Message message) {
        this.commandType = commandType;
        this.message = message;
    }

    ChatClientCommand(CommandType commandType, int number) {
        this.commandType = commandType;
        this.number = number;
    }
}

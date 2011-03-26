package edu.berkeley.cs.cs162;

import java.io.Serializable;

public class ChatClientCommand implements Serializable {

    public CommandType commandType;
    public String string1;
    public String string2;
    public int number;

    ChatClientCommand(CommandType commandType) {
        this.commandType = commandType;
    }

    ChatClientCommand(CommandType commandType, String string1, int number) {
        this.commandType = commandType;
        this.string1 = string1;
        this.number = number;
    }

    ChatClientCommand(CommandType commandType, String string1) {
        this.commandType = commandType;
        this.string1 = string1;
    }

    ChatClientCommand(CommandType commandType, String string1, int number, String string2) {
        this.commandType = commandType;
        this.string1 = string1;
        this.number = number;
        this.string2 = string2;
    }

    ChatClientCommand(CommandType commandType, int number) {
        this.commandType = commandType;
        this.number = number;
    }
}

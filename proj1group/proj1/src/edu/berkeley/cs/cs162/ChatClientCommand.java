package edu.berkeley.cs.cs162;

import java.io.Serializable;

public class ChatClientCommand implements Serializable {

    public CommandType commandType;
    public String string1;
    public String string2;
    public int number1;
    public int number2;

    ChatClientCommand(CommandType commandType) {
        this.commandType = commandType;
    }

    ChatClientCommand(CommandType commandType, String string1, int number1) {
        this.commandType = commandType;
        this.string1 = string1;
        this.number1 = number1;
    }

    ChatClientCommand(CommandType commandType, String string1) {
        this.commandType = commandType;
        this.string1 = string1;
    }

    ChatClientCommand(CommandType commandType, String string1, String string2) {
        this.commandType = commandType;
        this.string1 = string1;
        this.string2 = string2;
    }

    ChatClientCommand(CommandType commandType, String string1, int number1, String string2) {
        this.commandType = commandType;
        this.string1 = string1;
        this.number1 = number1;
        this.string2 = string2;
    }

    ChatClientCommand(CommandType commandType, String string1, String string2, int number1, int number2) {
        this.commandType = commandType;
        this.string1 = string1;
        this.string2 = string2;
        this.number1 = number1;
        this.number2 = number2;
    }

    ChatClientCommand(CommandType commandType, int number1) {
        this.commandType = commandType;
        this.number1 = number1;
    }
}

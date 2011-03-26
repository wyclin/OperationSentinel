package edu.berkeley.cs.cs162;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeSet;

public class ChatServerResponse implements Serializable {

    ChatClientCommand command;
    ResponseType responseType;
    TreeSet<String> treeSet;
    int number;
    Message message;

    Date messageDate;
    String messageSender;
    String messageReceiver;
    int messagesqn;
    String messageText;

    ChatServerResponse(ResponseType responseType) {
        this.responseType = responseType;
    }

    ChatServerResponse(ResponseType responseType, TreeSet<String> treeSet) {
        this.responseType = responseType;
        this.treeSet = treeSet;
    }

    ChatServerResponse(ResponseType responseType, int number) {
        this.responseType = responseType;
        this.number = number;
    }

    ChatServerResponse(ResponseType responseType, Message message) {
        this.responseType = responseType;
        this.message = message;
    }
}

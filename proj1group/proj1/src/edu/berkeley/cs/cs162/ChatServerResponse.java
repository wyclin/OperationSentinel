package edu.berkeley.cs.cs162;

import java.io.Serializable;
import java.util.TreeSet;

public class ChatServerResponse implements Serializable {
    ResponseType responseType;
    TreeSet<String> treeSet;
    int number;

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
}

package edu.berkeley.cs.cs162;

public class ChatServerResponsePair {
    ChatServerResponse response;
    Object data;

    ChatServerResponsePair(ChatServerResponse response, Object data) {
        this.response = response;
        this.data = data;
    }
}

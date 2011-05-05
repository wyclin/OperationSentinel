package edu.berkeley.cs.cs162;

import java.io.Serializable;

public class ServerMessage implements Serializable {

    ServerMessageType messageType;
    SerializedMessage serializedMessage;
    String serverName;

    public ServerMessage(ServerMessageType messageType, String serverName) {
        this.messageType = messageType;
        this.serverName = serverName;
    }

    public ServerMessage(ServerMessageType messageType, SerializedMessage serializedMessage) {
        this.messageType = messageType;
        this.serializedMessage = serializedMessage;
    }
}

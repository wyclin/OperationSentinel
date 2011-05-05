package edu.berkeley.cs.cs162;

public enum CommandType {
    CONNECT,
    DISCONNECT,
    MIGRATE,
    ADDUSER,
    READLOG,
    LOGIN,
    LOGOUT,
    JOIN_GROUP,
    LEAVE_GROUP,
    SEND_MESSAGE,
    SLEEP,

    ADD_SERVER,

    COMMAND_NOT_FOUND
}

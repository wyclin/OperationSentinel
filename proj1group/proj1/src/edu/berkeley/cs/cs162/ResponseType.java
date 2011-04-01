package edu.berkeley.cs.cs162;

public enum ResponseType {
    SHUTTING_DOWN,

    USER_ADDED,
    USER_QUEUED,
    USER_REMOVED,
    USER_ADDED_TO_GROUP,
    USER_ADDED_TO_NEW_GROUP,
    USER_REMOVED_FROM_GROUP,
    USER_NOT_FOUND,
    GROUP_NOT_FOUND,
    NAME_CONFLICT,
    USER_CAPACITY_REACHED,
    GROUP_CAPACITY_REACHED,
    USER_ALREADY_MEMBER_OF_GROUP,
    USER_NOT_MEMBER_OF_GROUP,
    DATA_SENT,

    MESSAGE_ENQUEUED,
    MESSAGE_BUFFER_FULL,
    SENDER_NOT_FOUND,
    RECEIVER_NOT_FOUND,
    RECEIVER_SAME_AS_SENDER,

    MESSAGE_RECEIVED,
    MESSAGE_DELIVERY_FAILURE,

    DISCONNECT,
    TIMEOUT,

    COMMAND_NOT_FOUND
}

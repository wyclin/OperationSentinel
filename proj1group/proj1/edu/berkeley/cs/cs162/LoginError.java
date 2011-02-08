package edu.berkeley.cs.cs162;


/**
 * Potential error codes you can get when trying to add a new user to the server.
 * 
 *
 */
public enum LoginError {
	USER_ACCEPTED, /* user is in and can start sending/receiving messages */
	USER_QUEUED,   /* the server is busy now but will be added later */
	USER_DROPPED,  /* the server can't even queue you*/
	USER_REJECTED; /* authentication failed */
}

package edu.berkeley.cs.cs162;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ChatClientResponseHandler extends Thread {

    private ChatClient chatClient;
    private Socket socket;
    private ObjectInputStream remoteInput;
    private LinkedBlockingQueue<ChatServerResponse> pendingResponses;

    private BenchmarkingChatClient benchmarkingChatClient;
    private ConcurrentHashMap<Integer, Long> messagesSent;
    private ArrayList<Long> roundTripTimes;
    private int clientID;

    public ChatClientResponseHandler(ChatClient chatClient, Socket socket, LinkedBlockingQueue<ChatServerResponse> pendingResponses) {
        this.chatClient = chatClient;
        this.socket = socket;
        this.pendingResponses = pendingResponses;
        try {
            remoteInput = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            chatClient.disconnect();
        }
    }

    public ChatClientResponseHandler(BenchmarkingChatClient chatClient, Socket socket, LinkedBlockingQueue<ChatServerResponse> pendingResponses, ConcurrentHashMap<Integer, Long> msgSent, ArrayList<Long> rtt, int cid) {
        this.benchmarkingChatClient = chatClient;
	this.messagesSent = msgSent;
	this.roundTripTimes = rtt;
	this.clientID = cid;

	this.socket = socket;
        this.pendingResponses = pendingResponses;
        try {
            remoteInput = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            chatClient.disconnect();
        }
    }

    public void run() {
        try {
            while (true) {
                ChatServerResponse response = (ChatServerResponse)remoteInput.readObject();
                if (benchmarkingChatClient instanceof BenchmarkingChatClient) {
                    if (response.responseType == ResponseType.MESSAGE_RECEIVED) {
                        if (OriginatedFromMe(response))
                            finishTimingMessage(response);
                    }
                }
		
                pendingResponses.offer(response);
            }
        } catch (Exception e) {
        } finally {
            try {
                remoteInput.close();
                socket.close();
            } catch (Exception f) {}
            chatClient.flagReconnect();
            pendingResponses.offer(new ChatServerResponse(ResponseType.INTERRUPT));
        }
    }
    
    private boolean OriginatedFromMe(ChatServerResponse response) {
        String msgtext = response.messageText;
        String lastWordOfMessage = msgtext.substring(msgtext.lastIndexOf(" ") + 1);
        if ((messagesSent.get((Integer)response.messagesqn) != null) && (Integer.valueOf(lastWordOfMessage) == clientID))
            return true;
        return false;
    }

        private void finishTimingMessage(ChatServerResponse response) {
        try {
            Long startTime = messagesSent.get(Integer.valueOf(response.messagesqn));
            long timeElapsed = System.currentTimeMillis() - startTime.longValue();
            roundTripTimes.add(new Long(timeElapsed));
            //messagesSent.remove((Integer)response.messagesqn);
        } catch (Exception e) {}
    }

}

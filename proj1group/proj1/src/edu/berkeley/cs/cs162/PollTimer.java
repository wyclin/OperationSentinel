package edu.berkeley.cs.cs162;

import java.util.Timer;
import java.util.TimerTask;

public class PollTimer {

    Timer timer;

    public PollTimer(ChatClient chatClient, int seconds) {
        timer = new Timer();
        timer.schedule(new PollTimerTask(chatClient), seconds * 1000);
    }

    public void cancel() {
        timer.cancel();
    }

    class PollTimerTask extends TimerTask {

        ChatClient chatClient;

        public PollTimerTask(ChatClient chatClient) {
            super();
            this.chatClient = chatClient;
        }

        public void run() {
            chatClient.flagPoll();
            timer.cancel();
        }
    }
}

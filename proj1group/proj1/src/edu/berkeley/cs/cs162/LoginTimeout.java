package edu.berkeley.cs.cs162;

import java.util.Timer;
import java.util.TimerTask;

public class LoginTimeout {

    Timer timer;

    public LoginTimeout(ChatUser chatUser, int seconds) {
        timer = new Timer();
        timer.schedule(new LoginTimeoutTask(chatUser), seconds * 1000);
    }

    public void cancel() {
        timer.cancel();
    }

    class LoginTimeoutTask extends TimerTask {

        ChatUser chatUser;

        public LoginTimeoutTask(ChatUser chatUser) {
            super();
            this.chatUser = chatUser;
        }

        public void run() {
            chatUser.timeout();
            timer.cancel();
        }
    }
}

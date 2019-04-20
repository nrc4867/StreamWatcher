package client;

import org.jibble.pircbot.PircBot;

public class SentFinalMessage extends Thread {
    private final PircBot bot;

    public static void Disconnect(PircBot bot) {
        new SentFinalMessage(bot);
    }

    private SentFinalMessage(PircBot bot){
        this.bot = bot;
        this.start();
    }

    @Override
    public void run() {
        while (bot.getOutgoingQueueSize() > 0){
            try {
                sleep(10);
            } catch (InterruptedException e) {
            }
        }
        bot.disconnect();
    }
}

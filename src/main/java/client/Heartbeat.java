package client;

import util.ClosableThread;

/**
 * Manages a heartbeat
 */
public class Heartbeat extends ClosableThread {

    /**
     * The time allowed for the client to respond
     */
    public static final int RESPOND = 2 * 60 * 1000;

    private final Client client;

    public Heartbeat(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            sleep(RESPOND);
            System.out.println("Heartbeat Failed: Closing Connection");
            client.close();
        } catch (InterruptedException e) {
            if (client.isRunning())
                System.out.println("Heartbeat: Received " + client);
            else
                System.out.println("Heartbeat: Closed " + client);
        }
    }

    /**
     * Stop waiting for a heartbeat
     */
    @Override
    public void close() {
        interrupt();
    }
}

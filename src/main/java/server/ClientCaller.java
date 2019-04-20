package server;

import client.ClientManager;
import util.ClosableThread;

import java.util.Set;

/**
 * Sends heartbeats to clients
 */
public class ClientCaller extends ClosableThread {

    public static final int interval = 2 * 60 * 1000;

    private final CheeseServer server;

    public ClientCaller(CheeseServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("Heartbeats enabled on: " + server.getPort());
        while (server.isRunning()) {
            try {
                sleep(interval);
                callClients();
            } catch (InterruptedException e) {
                System.out.println("Heartbeat interrupted");
            }
        }
        System.out.println("No longer looking for beats");
    }

    /**
     * Ask the clients if their sockets are active
     */
    private void callClients() {
        Set<ClientManager> clients = server.getClients();
        System.out.println("Sending beats to " + clients.size() + " connections.");
        for (ClientManager client: clients)
            client.sendBeat();
    }

    /**
     * do not send the next beat
     */
    @Override
    public void close() {
        interrupt();
    }
}

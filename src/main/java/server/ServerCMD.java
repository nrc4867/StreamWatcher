package server;

import client.ClientManager;
import util.ClosableThread;
import util.ServerMessages;

import java.util.Scanner;
import java.util.Set;

class ServerCMD extends ClosableThread {

    private static final Scanner scanner = new Scanner(System.in);

    private final Server server;

    public ServerCMD(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("Running Image CMD");
        while (server.isRunning()) {
            if(scanner.hasNextLine()) read(scanner.nextLine());
        }
    }

    private void read(String message) {
        Set<ClientManager> clients = server.getClients();
        switch (message) {
            case ServerMessages.Cheese:
                fakeImage(clients);
                break;
            case ServerMessages.Rad:
                fakeRadImage(clients);
                break;
            case ServerMessages.Close:
                closeClients(clients);
                break;
            case ServerMessages.HeartbeatSend:
                sendBeats(clients);
                break;
            default:
                System.out.println("Unknown command");
                break;
        }
    }

    private void fakeImage(Set<ClientManager> clients) {
        for(ClientManager client: clients)
            client.sendNormalImage();
    }

    private void fakeRadImage(Set<ClientManager> clients) {
        for(ClientManager client: clients)
            client.sendRadImage();
    }

    private void closeClients(Set<ClientManager> clients) {
        for(ClientManager client: clients)
            client.close();
    }

    private void sendBeats(Set<ClientManager> clients) {
        for (ClientManager client: clients)
            client.sendBeat();
    }


    @Override
    public void close() {
        scanner.close();
    }
}

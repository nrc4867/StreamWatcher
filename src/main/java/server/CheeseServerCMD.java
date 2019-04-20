package server;

import client.ClientManager;
import util.ClosableThread;
import util.ServerMessages;

import java.util.Scanner;
import java.util.Set;

class CheeseServerCMD extends ClosableThread {

    private static final Scanner scanner = new Scanner(System.in);

    private final CheeseServer server;

    public CheeseServerCMD(CheeseServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("Running Cheese CMD");
        while (server.isRunning()) {
            if(scanner.hasNextLine()) read(scanner.nextLine());
        }
    }

    private void read(String message) {
        Set<ClientManager> clients = server.getClients();
        switch (message) {
            case ServerMessages.Cheese:
                fakeCheese(clients);
                break;
            case ServerMessages.Rad:
                fakeRadCheese(clients);
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

    private void fakeCheese(Set<ClientManager> clients) {
        for(ClientManager client: clients)
            client.sendNormalCheese();
    }

    private void fakeRadCheese(Set<ClientManager> clients) {
        for(ClientManager client: clients)
            client.sendRadCheese();
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

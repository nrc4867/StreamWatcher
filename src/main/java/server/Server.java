package server;

import client.BotClient;
import client.Client;
import client.ClientManager;
import util.ClosableThread;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Server extends ClosableThread {

    public final static int defaultPort = 8032;
    public final static boolean terminalInput = false;

    private final int port;
    private final ServerSocket server;

    private final Set<ClientManager> clients = new HashSet<>();

    /**
     * Create a server
     * @param port the port to run on
     */
    public Server(int port) throws IOException {
        this.port = port;
        server = new ServerSocket(port);
    }

    @Override
    public void run() {
        System.out.println("Server started on: " + port);
        if (server.isClosed()) return;
        while (!server.isClosed()) {
            try {
                addClient();
            } catch (IOException e) {
                System.out.println("Server Closed");
            }
        }
    }

    private void addClient() throws IOException {
        ClientManager client = new ClientManager(server.accept(), this);
        synchronized (clients) {
            clients.add(client);
        }
        client.start();
    }

    private synchronized void addClients(ClientManager... clients) {
        this.clients.addAll(Arrays.asList(clients));
    }

    /**
     * Remove a dead client from the server
     * @param client the client to remove
     * @return true if the client is removed
     */
    public boolean removeClient(ClientManager client) {
        if (client.isRunning()) return false;
        synchronized (clients) {
            return clients.remove(client);
        }
    }

    public synchronized Set<ClientManager> getClients() {
        return new HashSet<>(clients);
    }

    @Override
    public void close() {
        if (server.isClosed()) return;
        System.out.println("Server Shutdown.");

        synchronized (clients) {
            HashSet<ClientManager> clients = new HashSet<>(this.clients);
            for (Client client: clients) client.close();
        }
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public synchronized boolean isRunning() {
        return !server.isClosed();
    }

    public static void main(String... args) {

        int port = defaultPort;

        Server server = null;
        ClientCaller clientCaller = null;

        try {
            server = new Server(port);
            clientCaller = new ClientCaller(server);

            server.start();
            clientCaller.start();

            BotClient charcollector = new BotClient(server, new File("D:\\Documents\\sneakyMouse\\char.txt")) {
                @Override
                public void sendNormalImage() {}

                @Override
                public void sendRadImage() {
                    messageRelay.sendMouse();
                    System.out.println(toString());
                }
            };
            server.addClients(charcollector);

        } catch (IOException e) {
            System.out.println("Server already running on port: " + port);
            e.printStackTrace();
            System.exit(1);
        }

        Runtime runtime = Runtime.getRuntime();
        Shutdown shutdown = new Shutdown(server, clientCaller);

        ServerCMD cmd = new ServerCMD(server);
        shutdown.addClosableThreads(cmd);
        cmd.start();

        StreamRetriever retriever = new StreamRetriever(server);
        shutdown.addClosableThreads(retriever);
        retriever.start();


        runtime.addShutdownHook(shutdown);
    }
}

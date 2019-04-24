package client;

import server.CheeseServer;
import util.ClosableThread;
import util.ClientMessages;
import util.ServerMessages;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class ClientManager extends Client {
    protected final CheeseServer server;

    public ClientManager(CheeseServer server) {
        this.server = server;
    }

    public ClientManager(Socket socket, CheeseServer server) {
        setSocket(socket);
        this.server = server;
    }


    @Override
    protected void read(String message) {
        switch (message) {
            case ClientMessages.Disconnect:
                close();
                break;
            case ClientMessages.HeartbeatRespond:
                submitBeats();
                break;
            case ClientMessages.HeartbeatSend:
                receiveBeat();
                break;
            default:
                System.out.println("Broken message: " + message);
        }
    }

    /**
     * The client asks the server if it is alive
     */
    private  synchronized void receiveBeat() {
        writeMessage(ServerMessages.HeatbeatRespond);
    }

    /**
     * The server tells the client there is normal cheese
     */
    public synchronized void sendNormalCheese() {
        writeMessage(ServerMessages.Cheese);
    }

    /**
     * The server tells the client there is rad cheese
     */
    public synchronized void sendRadCheese() {
        writeMessage(ServerMessages.Rad);
    }

    /**
     * The server tells the client to close
     */
    private synchronized void sendClose() {
        writeMessage(ServerMessages.Close);
    }

    /**
     * Close the socket and cleanup
     */
    public synchronized void close() {
        if (socket.isClosed()) return;
        System.out.println(socket.getRemoteSocketAddress() + " Closed.");
        try {
            sendClose();
            socket.close();
            server.removeClient(this);
            submitBeats(); // kill all active heartbeats
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

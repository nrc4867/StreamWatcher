package client;

import server.Server;
import util.ClientMessages;
import util.ServerMessages;

import java.io.*;
import java.net.Socket;

public class ClientManager extends Client {
    protected final Server server;

    public ClientManager(Socket socket, Server server) {
        super(socket);
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
    public synchronized void sendNormalImage() {
        writeMessage(ServerMessages.Cheese);
    }

    /**
     * The server tells the client there is rad cheese
     */
    public synchronized void sendRadImage() {
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

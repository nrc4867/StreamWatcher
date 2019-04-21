package client;

import util.ClosableThread;
import util.ServerMessages;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public abstract class Client extends ClosableThread implements Serializable{

    protected final Socket socket;

    protected final ArrayList<Heartbeat> heartbeats = new ArrayList<>();

    protected final OutputStreamWriter outputStream;
    protected final InputStreamReader inputStream;

    public Client(Socket socket) {
        this.socket = socket;

        OutputStreamWriter outputStream = null;
        InputStreamReader inputStream = null;

        try {
            outputStream = new OutputStreamWriter(socket.getOutputStream());
            inputStream = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.outputStream = outputStream;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        System.out.println("Client started: " + socket.getRemoteSocketAddress());
        try(Scanner scanner = new Scanner(inputStream)) {
            while (!socket.isClosed()) {
                if(scanner.hasNextLine()) read(scanner.nextLine());
            }
        }
    }

    /**
     * Read a message from the client
     * @param message an input from the client socket
     */
    protected abstract void read(String message);

    /**
     * The client told the server that it is connected
     */
    protected void submitBeats() {
        for (Heartbeat heartbeat: heartbeats)
            heartbeat.interrupt();
        heartbeats.clear();
    }

    /**
     * Ask the client if it is still connected
     */
    public synchronized void sendBeat() {
        writeMessage(ServerMessages.HeartbeatSend);
        Heartbeat heartbeat = new Heartbeat(this);
        heartbeat.start();
        heartbeats.add(heartbeat);
    }

    /**
     * The server tells the client a message
     * @param message message to send
     */
    protected void writeMessage(String message) {
        if (!isRunning()) return;
        try {
            outputStream.write(message + "\n");
            outputStream.flush();
        } catch (IOException e) {
        }
    }

    /**
     * Close the socket and cleanup
     */
    public abstract void close();

    /**
     * @return is the socket alive
     */
    public boolean isRunning() {
        return !socket.isClosed();
    }

    @Override
    public int hashCode() {
        return socket.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientManager && socket.equals(obj);
    }

    @Override
    public String toString() {
        return "Client: " + socket.getRemoteSocketAddress();
    }
}

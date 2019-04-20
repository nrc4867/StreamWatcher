package client;

import org.jibble.pircbot.IrcException;
import server.CheeseServer;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class BotClient extends ClientManager  {

    private boolean isRunning = true;

    private final MessageRelay messageRelay;
    private final File userInfo;

    /**
     * Create a bot to collect cheese
     * @param server the server the bot lives on
     * @param userInfo channel - Twitch IRC
     *                 username - Twitch Username
     *                 password - Twitch Password
     */
    public BotClient(CheeseServer server, File userInfo) throws IOException {
        super(new Socket("localhost" , server.getPort()), server);

        this.userInfo = userInfo;
        MessageRelay relay = null;

        try (Scanner reader = new Scanner(new FileInputStream(userInfo))) {

            String channel = reader.nextLine();
            String username = reader.nextLine();
            String password = reader.nextLine();

            relay = new MessageRelay(channel, username, password);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }catch (NoSuchElementException e) {
            System.out.println("Incorrect file format, Must match: Channel \n Username \n Password");
            e.printStackTrace();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }

        messageRelay = relay;
    }

    @Override
    public void run() {
    }

    @Override
    public synchronized void sendBeat() {}

    @Override
    public synchronized void sendNormalCheese() {
        messageRelay.sendMouse();
    }

    @Override
    public synchronized void sendRadCheese() {
        messageRelay.sendDanger();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public synchronized void close() {
        isRunning = false;
        messageRelay.close();
        server.removeClient(this);
    }

    @Override
    public boolean equals(Object obj) {
       if(!(obj instanceof BotClient)) return false;
       BotClient botClient = (BotClient) obj;
       return userInfo.equals(botClient.userInfo);
    }

    @Override
    public int hashCode() {
        return userInfo.hashCode();
    }
}

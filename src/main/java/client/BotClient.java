package client;

import org.jibble.pircbot.IrcException;
import server.Server;

import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class BotClient extends ClientManager  {

    private boolean isRunning = true;

    protected final MessageRelay messageRelay;
    private final File userInfo;
    private final String username;

    /**
     * Create a bot to watch for images
     * @param server the server the bot lives on
     * @param userInfo channel - Twitch IRC
     *                 username - Twitch Username
     *                 password - Twitch Password
     */
    public BotClient(Server server, File userInfo) throws IOException {
        super(new Socket(), server);

        this.userInfo = userInfo;
        MessageRelay relay = null;

        String user = null;

        try (Scanner reader = new Scanner(new FileInputStream(userInfo))) {

            String channel = reader.nextLine();
            user = reader.nextLine();
            String password = reader.nextLine();

            relay = new MessageRelay(channel, user, password);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }catch (NoSuchElementException e) {
            System.out.println("Incorrect file format, Must match: Channel \n Username \n Password");
            e.printStackTrace();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }

        this.username = user;
        messageRelay = relay;
    }

    @Override
    public synchronized void sendBeat() {}

    @Override
    public abstract void sendNormalImage();

    @Override
    public abstract void sendRadImage();

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public synchronized void close() {
        isRunning = false;
        messageRelay.close();
        server.removeClient(this);
        System.out.printf("%s Closed%n", this);
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

    @Override
    public String toString() {
        return "Bot: " + username;
    }
}

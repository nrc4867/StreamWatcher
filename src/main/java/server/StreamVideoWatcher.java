package server;

import client.ClientManager;
import org.openimaj.image.DisplayUtilities;
import util.Cheese;
import util.CheeseAnalyzer;
import util.ObjectSaver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Set;

public class StreamVideoWatcher extends Thread {
    private final CheeseServer server;
    private final InputStream stream;

    private boolean seenNormal = false;
    private boolean seenRadical = false;

    private final Cheese radical;
    private final Cheese normal;

    StreamVideoWatcher(CheeseServer server, InputStream stream) throws IOException {
        this.server = server;
        this.stream = stream;

        this.radical = (Cheese) ObjectSaver.load("./Purple.cheesedata");
        radical.setFreedom(5.0f);
        radical.setPercentMatch(0.25f);

        this.normal = (Cheese) ObjectSaver.load("./Yellow.cheesedata");
        normal.setFreedom(5.0f);
        normal.setPercentMatch(0.25f);
    }

    @Override
    public void run() {
        JFrame frame = new JFrame();
        while (server.isRunning()) {
            try {
                BufferedImage image = ImageIO.read(stream);
                if (image == null) {
                    // We read every other image because of ImageIO leaves garbage behind and I dont feel like fixing it
                    stream.skip(stream.available());
                    continue;
                }


                if (!seenNormal && normal.compare(image)) {
                    Set<ClientManager> clientList = server.getClients();
                    System.out.println("Sending Normal Cheese to: " + clientList.size() + " clients");
                    for (ClientManager client: clientList) {
                        client.sendNormalCheese();
                    }

                    seenRadical = false;
                    seenNormal = true;
                }

                if (seenNormal && !seenRadical && radical.compare(image)) {
                    Set<ClientManager> clientList = server.getClients();
                    System.out.println("Sending Radical Cheese to: " + clientList.size() + " clients");
                    for (ClientManager client: clientList) {
                        client.sendRadCheese();
                    }

                    seenRadical = true;
                    seenNormal = false;
                }

                DisplayUtilities.display(image, frame);
                image.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e) {
                // sometimes this happens

            }
        }
    }

}

package server;

import client.ClientManager;
import org.openimaj.image.DisplayUtilities;
import util.CheeseAnalyzer;

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

    StreamVideoWatcher(CheeseServer server, InputStream stream) throws IOException {
        this.server = server;
        this.stream = stream;
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


                if (!seenNormal & CheeseAnalyzer.scanNormalCheese(image)) {
                    Set<ClientManager> clientList = server.getClients();
                    System.out.println("Sending Normal Cheese to: " + clientList.size() + " clients");
                    for (ClientManager client: clientList) {
                        client.sendNormalCheese();
                    }

                    seenRadical = false;
                    seenNormal = true;
                }

                if (seenNormal && !seenRadical && CheeseAnalyzer.scanRadCheese(image)) {
                    Set<ClientManager> clientList = server.getClients();
                    System.out.println("Sending Radical Cheese to: " + clientList.size() + " clients");
                    for (ClientManager client: clientList) {
                        client.sendRadCheese();
                    }

                    seenRadical = true;
                    seenNormal = false;
                }

//                DisplayUtilities.display(image, frame);
                image.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e) {
                // sometimes this happens

            }
        }
    }

}

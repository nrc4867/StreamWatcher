package server;

import client.ClientManager;
import util.FixedImageSaver;
import util.ObjectSaver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Set;

public class StreamVideoWatcher extends Thread {
    private final Server server;
    private final Process process;
    private final InputStream stream;

    private boolean seenNormal = false;
    private boolean seenRadical = false;

    private final FixedImageSaver radical;
    private final FixedImageSaver normal;

    StreamVideoWatcher(Server server, Process process) throws IOException {
        this.server = server;
        this.process = process;
        this.stream = process.getInputStream();

        this.radical = (FixedImageSaver) ObjectSaver.load("./Purple.cheesedata");
        radical.setFreedom(5.0f);
        radical.setPercentMatch(0.25f);

        this.normal = (FixedImageSaver) ObjectSaver.load("./Yellow.cheesedata");
        normal.setFreedom(5.0f);
        normal.setPercentMatch(0.25f);
    }

    @Override
    public void run() {
        while (process.isAlive()) {
            try {
                BufferedImage image = ImageIO.read(stream);
                if (image == null) {
                    // We read every other image because of ImageIO leaves garbage behind and I dont feel like fixing it
                    stream.skip(stream.available());
                    continue;
                }


                if (!seenNormal && normal.compare(image)) {
                    Set<ClientManager> clientList = server.getClients();
                    System.out.println("Sending Normal Image to: " + clientList.size() + " clients");
                    for (ClientManager client: clientList) {
                        client.sendNormalImage();
                    }

                    seenRadical = false;
                    seenNormal = true;
                }

                if (seenNormal && !seenRadical && radical.compare(image)) {
                    Set<ClientManager> clientList = server.getClients();
                    System.out.println("Sending Radical Image to: " + clientList.size() + " clients");
                    for (ClientManager client: clientList) {
                        client.sendRadImage();
                    }

                    seenRadical = true;
                    seenNormal = false;
                }

//                DisplayUtilities.display(image, frame);
                image.flush();
            } catch (IOException e) {
                System.out.println("Somehow Im here");
            } catch (ArrayIndexOutOfBoundsException e) {
                // sometimes this happens

            }
        }
    }

}

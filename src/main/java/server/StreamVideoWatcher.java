package server;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class StreamVideoWatcher extends Thread {
    private final CheeseServer server;
    private final InputStream stream;


    StreamVideoWatcher(CheeseServer server, InputStream stream) {
        this.server = server;
        this.stream = stream;
    }

    @Override
    public void run() {

        JFrame frame = new JFrame();
        while (true) {
            try {
                BufferedImage image = ImageIO.read(stream);
                if (image == null) {
                    // We read every other image because of ImageIO leaves garbage behind and I dont feel like fixing it
                    stream.skip(stream.available());
                    continue;
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

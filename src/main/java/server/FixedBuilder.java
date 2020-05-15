package server;

import org.openimaj.image.DisplayUtilities;
import util.FixedImageSaver;
import util.ObjectSaver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class FixedBuilder extends Thread {

    private final InputStream stream;
    private final String name;

    private final int amount = 300;

    private final int startX = 3;
    private final int startY = 137;

    private final int sizeX = 21;
    private final int sizeY = 18;

    private float[][][] overallPixelData = new float[sizeX][sizeY][3];



    public FixedBuilder(String name, InputStream stream) throws IOException {
        this.stream = stream;
        this.name = name;
    }

    @Override
    public void run() {

        JFrame frame = new JFrame();

        int amount = this.amount;
        while (amount > 0) {
            try {
                BufferedImage image = ImageIO.read(stream);
                if (image == null) {
                    // We read every other image because of ImageIO leaves garbage behind and I dont feel like fixing it
                    stream.skip(stream.available());
                    continue;
                }

                addPixelData(image);

                DisplayUtilities.display(image, frame);
                amount--;

            } catch (IOException | ArrayIndexOutOfBoundsException e){}

        }

        export(averagePixels(this.amount));
    }

    private void addPixelData(BufferedImage image) throws ArrayIndexOutOfBoundsException {
        int newpixel = 0;
        newpixel = 255 << 16;
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                int pixelValue = image.getRGB(x + startX, y + startY);


                // Square the new values
                int r = (pixelValue >> 16) & 0xff; r *= r;
                int g = (pixelValue >> 8) & 0xff; g *= g;
                int b  = (pixelValue) & 0xff; b *= b;

                overallPixelData[x][y][0] += r;
                overallPixelData[x][y][1] += g;
                overallPixelData[x][y][2] += b;
                if (x == 0 || y == 0 || x + 1 == sizeX || y + 1 == sizeY)
                    image.setRGB(x+startX,y+startY,newpixel);
            }
        }
    }

    private int[][] averagePixels(int amount) {
        int[][] pixelData = new int[sizeX][sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                final int pr = (int)Math.sqrt(overallPixelData[x][y][0] / amount);
                final int pg = (int)Math.sqrt(overallPixelData[x][y][1] / amount);
                final int pb = (int)Math.sqrt(overallPixelData[x][y][2] / amount);

                pixelData[x][y] = (pr << 16) | (pg << 8) | pb;
            }
        }
        return pixelData;
    }

    private void export(int[][] pixelData) {
        ObjectSaver.save(new FixedImageSaver(name, startX, startY, sizeX, sizeY, pixelData), "./" + name + ".cheesedata");
    }

}

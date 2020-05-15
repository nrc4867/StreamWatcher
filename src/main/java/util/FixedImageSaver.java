package util;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class FixedImageSaver implements Serializable{

    transient private float percentMatch;
    transient private float freedom;

    private final String name;

    private final int startX;
    private final int startY;

    private final int sizeX;
    private final int sizeY;

    private final int size;

    private final int[][] pixelData;

    public static FixedImageSaver loadImage(String fileLocation) {
        return (FixedImageSaver) ObjectSaver.load(fileLocation);
    }

    public FixedImageSaver(String name, int startX, int startY, int sizeX, int sizeY, int[][] pixelData) {
        this.name = name;
        this.startX = startX;
        this.startY = startY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.pixelData = pixelData;

        this.size = sizeX * sizeY;
    }

    public int[][] getPixelData() {
        return pixelData;
    }

    public boolean compare(BufferedImage image) {
        int matches = 0;
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (scanPixel(image.getRGB(x + startX, y + startY), pixelData[x][y])) matches++;
            }
        }
        return ((float)matches / size) >= percentMatch;
    }

    public void setFreedom(float freedom) {
        this.freedom = 100.0f - freedom;
    }

    public void setPercentMatch(float percentMatch) {
        this.percentMatch = percentMatch;
    }

    private boolean scanPixel(int pixel, int ImagePixel) {
        int pr = (pixel >> 16) & 0xff;
        int pg = (pixel >> 8) & 0xff;
        int pb  = (pixel) & 0xff;

        int cr = (ImagePixel >> 16) & 0xff;
        int cg = (ImagePixel >> 8) & 0xff;
        int cb  = (ImagePixel) & 0xff;

        return colorDistance(pr, pg, pb, cr, cg, cb) >= freedom;
    }

    private float colorDistance(int pr, int pg, int pb, int r, int g, int b) {
        final int diffR = Math.abs(r - pr);
        final int diffG = Math.abs(g - pg);
        final int diffB = Math.abs(b - pb);

        final float pctDiffRed = (float) diffR / 255;
        final float pctDiffGreen = (float) diffB / 255;
        final float pctDiffBlue = (float) diffG / 255;

        return 100.0f - ((pctDiffRed + pctDiffBlue + pctDiffGreen) / 3 * 100);
    }

    private void log(String message) {
        System.out.println("Image-" + name + ": " + message);
    }

    @Override
    public String toString() {
        return name;
    }
}

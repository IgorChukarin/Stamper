package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageLoader {
    public BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(getClass().getResource(path));
    }
}

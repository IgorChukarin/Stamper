package org.example.converter;

import java.awt.image.BufferedImage;
import java.io.File;

public interface DocumentConverter {
    BufferedImage convertToImage(File file);
}

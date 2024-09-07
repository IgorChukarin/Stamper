package org.example.converters;

import com.spire.doc.Document;
import com.spire.doc.documents.ImageType;

import java.awt.image.BufferedImage;
import java.io.File;

public class DocxConverter {
    public static BufferedImage docxToImage(File docxFile) {
        Document document = new Document();
        document.loadFromFile(docxFile.getAbsolutePath());
        BufferedImage[] image = document.saveToImages(0, 1, ImageType.Bitmap, 150, 150);
        return image[0];
    }
}

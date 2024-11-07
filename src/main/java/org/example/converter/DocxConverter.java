package org.example.converter;

import com.spire.doc.Document;
import com.spire.doc.documents.ImageType;

import java.awt.image.BufferedImage;
import java.io.File;

public class DocxConverter implements DocumentConverter{

    @Override
    public BufferedImage convertToImage(File docxFile) {
        Document document = new Document();
        document.loadFromFile(docxFile.getAbsolutePath());
        BufferedImage[] image = document.saveToImages(0, 1, ImageType.Bitmap, 150, 150);
        return image[0];
    }
}

package org.example;

import com.spire.doc.Document;
import com.spire.doc.documents.ImageType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileConverter {

    public BufferedImage pdfToImage(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            return pdfRenderer.renderImageWithDPI(0, 150, org.apache.pdfbox.rendering.ImageType.RGB);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public BufferedImage docxToImage(File docxFile) {
        Document document = new Document();
        document.loadFromFile(docxFile.getAbsolutePath());
        BufferedImage[] image = document.saveToImages(0, 1, ImageType.Bitmap, 150, 150);
        return image[0];
    }
}

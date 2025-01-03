package org.example.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfConverter implements DocumentConverter{

    @Override
    public BufferedImage convertToImage(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            return pdfRenderer.renderImageWithDPI(0, 150, org.apache.pdfbox.rendering.ImageType.RGB);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

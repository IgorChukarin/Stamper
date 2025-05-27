package com.example.stamper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

public class SealApplier {
    public static void applySeal() {
        String filePath = "testFiles/document.pdf";
        String sealPath = "testFiles/seal.PNG";
        String outputPath = "testFiles/output.pdf";
        int pageIndex = 0;
        float x = 100;
        float y = 100;
        float width = 150;
        float height = 150;

        try {
            File file = new File(filePath);
            PDDocument document = PDDocument.load(file);
            PDPage page = document.getPage(pageIndex);
            PDImageXObject pdImage = PDImageXObject.createFromFile(sealPath, document);
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            contentStream.drawImage(pdImage, x, y, width, height);
            contentStream.close();
            document.save(outputPath);
            document.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
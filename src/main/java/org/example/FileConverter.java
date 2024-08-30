package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileConverter {
    public static File pdfToJpg(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IllegalArgumentException("Файл не существует или неверно указан путь.");
        }

        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();

            // Создаем выходной файл (для первой страницы)
            File jpgFile = new File(pdfFile.getParent(), pdfFile.getName().replace(".pdf", ".jpg"));

            // Конвертация первой страницы в изображение (если нужно все страницы, используйте цикл)
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            ImageIO.write(bim, "jpg", jpgFile);

            return jpgFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

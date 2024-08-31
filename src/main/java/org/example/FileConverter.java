package org.example;

import com.itextpdf.io.source.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileConverter {
    public BufferedImage pdfToBufferedImage(File pdfFile) {
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
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            return bufferedImage;
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

    public static void saveImageAsPDF(BufferedImage image, File outputPdfFile) throws IOException {
        // Создаем новый PDF-документ
        PDDocument document = new PDDocument();

        // Создаем страницу с размерами изображения
        PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
        document.addPage(page);

        // Преобразуем BufferedImage в PDImageXObject
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, convertBufferedImageToBytes(image), "image");

        // Рисуем изображение на странице
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true)) {
            contentStream.drawImage(pdImage, 0, 0, image.getWidth(), image.getHeight());
        }

        // Сохраняем документ в файл
        document.save(outputPdfFile);

        // Закрываем документ
        document.close();
    }

    // Вспомогательный метод для конвертации BufferedImage в байтовый массив
    private static byte[] convertBufferedImageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos); // Вы можете использовать другой формат, если необходимо
        return baos.toByteArray();
    }
}

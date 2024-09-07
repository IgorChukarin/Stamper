package org.example;

import com.itextpdf.io.source.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaveService {
    public static void saveImageAsPDF(BufferedImage image, File outputPdfFile) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, convertBufferedImageToBytes(image), "image");

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true)) {
            PDRectangle pageSize = PDRectangle.A4;
            float imageAspectRatio = (float) image.getWidth() / (float) image.getHeight();
            float pageAspectRatio = pageSize.getWidth() / pageSize.getHeight();

            float drawWidth, drawHeight;
            if (imageAspectRatio > pageAspectRatio) {
                drawWidth = pageSize.getWidth();
                drawHeight = drawWidth / imageAspectRatio;
            } else {
                drawHeight = pageSize.getHeight();
                drawWidth = drawHeight * imageAspectRatio;
            }

            float offsetX = (pageSize.getWidth() - drawWidth) / 2;
            float offsetY = (pageSize.getHeight() - drawHeight) / 2;

            contentStream.drawImage(pdImage, offsetX, offsetY, drawWidth, drawHeight);
        }

        document.save(outputPdfFile);

        document.close();
    }

    // Вспомогательный метод для конвертации BufferedImage в байтовый массив
    private static byte[] convertBufferedImageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(1.0f); // Задаем уровень компрессии (0.75 - компрессия, 1.0 - без компрессии)

        writer.write(null, new IIOImage(image, null, null), param);
        writer.dispose();
        return baos.toByteArray();
    }
}

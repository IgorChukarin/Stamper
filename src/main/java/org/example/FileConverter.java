package org.example;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.spire.doc.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;


import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileConverter {


    public BufferedImage pdfToImage(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            return pdfRenderer.renderImageWithDPI(0, 300, org.apache.pdfbox.rendering.ImageType.RGB);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public BufferedImage docxToImage (File docxFile) {
        Document document = new Document();
        document.loadFromFile(docxFile.getAbsolutePath());
        BufferedImage[] image= document.saveToImages(0, 1, com.spire.doc.documents.ImageType.Bitmap, 300, 300);
        return image[0];
    }


    public static void saveImageAsPDF(BufferedImage image, File outputPdfFile) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
        document.addPage(page);

        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, convertBufferedImageToBytes(image), "image");

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true)) {
            contentStream.drawImage(pdImage, 0, 0, image.getWidth(), image.getHeight());
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
        param.setCompressionQuality(0.5f); // Задаем уровень компрессии (0.75 - компрессия, 1.0 - без компрессии)

        writer.write(null, new IIOImage(image, null, null), param);
        writer.dispose();
        return baos.toByteArray();
    }
}

package org.example.converter;

import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class XlsxConverter implements DocumentConverter{

    @Override
    public BufferedImage convertToImage(File xlsxFile) {
        File tempPdfFile = null;
        try {

            tempPdfFile = File.createTempFile("temp", ".pdf");
            tempPdfFile.deleteOnExit();
            System.out.println("Temporary file created at: " + tempPdfFile.getAbsolutePath());

            String xlsxPath = xlsxFile.getAbsolutePath();

            Workbook originalWorkbook = new Workbook(xlsxPath);
            Workbook newWorkbook = new Workbook();
            WorksheetCollection originalSheets = originalWorkbook.getWorksheets();
            Worksheet firstSheet = originalSheets.get(0);

            Worksheet newSheet = newWorkbook.getWorksheets().get(0);
            newSheet.copy(firstSheet);

            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            newWorkbook.save(tempPdfFile.getAbsolutePath(), pdfSaveOptions);

            PdfConverter pdfConverter = new PdfConverter();
            return pdfConverter.convertToImage(tempPdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (tempPdfFile != null && tempPdfFile.exists()) {
                try {
                    Files.delete(tempPdfFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package org.example.converters;

import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

import java.awt.image.BufferedImage;
import java.io.File;


public class XlsxConverter {

    public static BufferedImage xlsxToImage(File xlsxFile) {
        try {
            String xlsxPath = xlsxFile.getAbsolutePath();
            String pdfPath = "C:/Users/Zigor/Desktop/TestFiles/output.pdf";

            // Открытие исходного Workbook
            Workbook originalWorkbook = new Workbook(xlsxPath);

            // Создание нового Workbook для сохранения только первого листа
            Workbook newWorkbook = new Workbook();
            WorksheetCollection originalSheets = originalWorkbook.getWorksheets();
            Worksheet firstSheet = originalSheets.get(0);

            // Клонирование первого листа в новый Workbook
            Worksheet newSheet = newWorkbook.getWorksheets().get(0);
            newSheet.copy(firstSheet);

            // Сохранение нового Workbook в PDF
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            newWorkbook.save(pdfPath, pdfSaveOptions);

            File pdfFile = new File(pdfPath);
            return PdfConverter.pdfToImage(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

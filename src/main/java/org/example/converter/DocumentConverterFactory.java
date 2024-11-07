package org.example.converter;

public class DocumentConverterFactory {
    public static DocumentConverter getDocumentConverter(String fileName) {
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return new PdfConverter();
        } else if (fileName.toLowerCase().endsWith(".docx")) {
            return new DocxConverter();
        } else if (fileName.toLowerCase().endsWith(".xlsx")) {
            return new XlsxConverter();
        } else {
            throw new UnsupportedOperationException("Unsupported file type.");
        }
    }
}

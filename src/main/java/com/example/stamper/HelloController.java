package com.example.stamper;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;



import java.io.File;

public class HelloController {

    @FXML
    private ImageView imageView;

    @FXML
    private Button btnPrev, btnNext;

    @FXML
    private Button btnOpenFile;

    @FXML
    private Label pageNumberLabel;

    private Stage stage;
    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage = 0;
    private int totalPages = 0;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    protected void onOpenFileButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                if (document != null) {
                    document.close();
                }
                document = PDDocument.load(file);
                renderer = new PDFRenderer(document);
                totalPages = document.getNumberOfPages();
                currentPage = 0;
                renderPage(currentPage);
                updateButtons();
                btnOpenFile.setManaged(false);
                btnOpenFile.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onPrevPage() {
        if (currentPage > 0) {
            currentPage--;
            renderPage(currentPage);
            updateButtons();
        }
    }

    @FXML
    private void onNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            renderPage(currentPage);
            updateButtons();
        }
    }

    private void renderPage(int pageIndex) {
        try {
            BufferedImage bufferedImage = renderer.renderImageWithDPI(pageIndex, 150);
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(fxImage);
            pageNumberLabel.setText(pageIndex + 1 + " из " + totalPages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateButtons() {
        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable(currentPage == totalPages - 1);
    }

    public void close() {
        try {
            if (document != null) {
                document.close();
            }
        } catch (Exception ignored) {}
    }
}
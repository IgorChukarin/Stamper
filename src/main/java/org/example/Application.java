package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

public class Application extends JFrame {

    private BufferedImage leftClickImage;
    private BufferedImage rightClickImage;
    private Point leftClickPosition;
    private Point rightClickPosition;

    private static final String EVKLID_SIGN_PATH = "/images/evklid/sign.PNG";
    private static final String EVKLID_STAMP_PATH = "/images/evklid/stamp.PNG";
    private static final String SPELS_SIGN_PATH = "/images/spels/sign.PNG";
    private static final String SPELS_STAMP_PATH = "/images/spels/stamp.PNG";

    private ImageLoader imageLoader;
    private FileConverter fileConverter;
    private ImagePanel imagePanel;


    public Application() throws IOException {
        imageLoader = new ImageLoader();
        fileConverter = new FileConverter();
        initializeUI();
        initializeMenuBar();
        initializeImagePanel();
        initializeImages();
        initializeButtons();
    }


    private void initializeImagePanel() {
        imagePanel = new ImagePanel();
        add(imagePanel, BorderLayout.CENTER);
        
        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    leftClickPosition.setLocation(e.getPoint());
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    rightClickPosition.setLocation(e.getPoint());
                }
                imagePanel.repaint();
            }
        });
    }


    private void initializeUI() throws IOException {
        setTitle("Stamper");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(ImageIO.read(getClass().getResource("/images/icon.PNG")));
    }


    private void initializeImages() throws IOException {
        rightClickPosition = new Point(-1, -1);
        leftClickPosition = new Point(-1, -1);
        leftClickImage = ImageIO.read(getClass().getResource(EVKLID_SIGN_PATH));
        rightClickImage = ImageIO.read(getClass().getResource(EVKLID_STAMP_PATH));
        imagePanel.setLeftClickImage(leftClickImage);
        imagePanel.setRightClickImage(rightClickImage);
    }


    private void initializeMenuBar() throws IOException {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem openMenuItem = new JMenuItem("Открыть");
        JMenuItem saveMenuItem = new JMenuItem("Сохранить");

        openMenuItem.addActionListener(e -> openFile());
        saveMenuItem.addActionListener(e -> saveFile());

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }


    private void initializeButtons() {
        JPanel buttonPanel = new JPanel();
        JButton evklidButton = new JButton("Евклид");
        JButton spelsButton = new JButton("Спелс");

        evklidButton.addActionListener(e -> loadStampAndSign(EVKLID_STAMP_PATH, EVKLID_SIGN_PATH));
        spelsButton.addActionListener(e -> loadStampAndSign(SPELS_STAMP_PATH, SPELS_SIGN_PATH));

        buttonPanel.add(evklidButton);
        buttonPanel.add(spelsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void loadStampAndSign(String stampPath, String signPath) {
        try {
            rightClickImage = imageLoader.loadImage(stampPath);
            leftClickImage = imageLoader.loadImage(signPath);
            imagePanel.setRightClickImage(rightClickImage);
            imagePanel.setLeftClickImage(leftClickImage);
            imagePanel.repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading images.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void openFile() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        int returnValue = fileChooser.showOpenDialog(this);
        BufferedImage documentImage = null;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                documentImage = fileConverter.pdfToImage(selectedFile);
            } else if (selectedFile != null && selectedFile.getName().toLowerCase().endsWith(".docx")) {
                documentImage = fileConverter.docxToImage(selectedFile);
            } else if (selectedFile != null && selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                System.out.println("this is xlsx file");
            }
            imagePanel.setDocumentImage(documentImage);
            imagePanel.repaint();
            setLocation(getLocation());
        }
    }


    private void saveFile() {
        BufferedImage documentImage = imagePanel.getDocumentImage();
        if (documentImage == null) {
            JOptionPane.showMessageDialog(this, "Документ не выбран", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            BufferedImage resultImage = new BufferedImage(documentImage.getWidth(), documentImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            recalculateImage(resultImage);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("document.pdf"));
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    file = new File(file.getAbsolutePath() + ".pdf");
                }
                SaveService.saveImageAsPDF(resultImage, file);
                JOptionPane.showMessageDialog(this, "Файл сохранен.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Не удалось сохранить файл.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void recalculateImage(BufferedImage resultImage) {
        BufferedImage documentImage = imagePanel.getDocumentImage();
        Graphics2D g2d = resultImage.createGraphics();
        g2d.drawImage(documentImage, 0, 0, null);

        int panelWidth = imagePanel.getWidth();
        int panelHeight = imagePanel.getHeight();

        double imageAspectRatio = (double) documentImage.getWidth() / documentImage.getHeight();
        double panelAspectRatio = (double) panelWidth / panelHeight;

        int drawWidth, drawHeight;
        int drawX = 0, drawY = 0;
        if (imageAspectRatio > panelAspectRatio) {
            drawWidth = panelWidth;
            drawHeight = (int) (panelWidth / imageAspectRatio);
            drawY = (panelHeight - drawHeight) / 2;
        } else {
            drawHeight = panelHeight;
            drawWidth = (int) (panelHeight * imageAspectRatio);
            drawX = (panelWidth - drawWidth) / 2;
        }

        double scaleFactor = setScaleFactor(imageAspectRatio);
        int overlayWidth = (int) (documentImage.getHeight() * scaleFactor);
        int overlayHeight = overlayWidth;
        if (leftClickPosition.x >= 0 && leftClickPosition.y >= 0) {
            int x = (leftClickPosition.x - drawX) * documentImage.getWidth() / drawWidth - (overlayWidth / 2);
            int y = (leftClickPosition.y - drawY) * documentImage.getHeight() / drawHeight - (overlayHeight / 2);
            g2d.drawImage(leftClickImage, x, y, overlayWidth, overlayHeight, null);
        }
        if (rightClickPosition.x >= 0 && rightClickPosition.y >= 0) {
            int x = (rightClickPosition.x - drawX) * documentImage.getWidth() / drawWidth - (overlayWidth / 2);
            int y = (rightClickPosition.y - drawY) * documentImage.getHeight() / drawHeight - (overlayHeight / 2);
            g2d.drawImage(rightClickImage, x, y, overlayWidth, overlayHeight, null);
        }
        g2d.dispose();
    }


    private double setScaleFactor(double imageAspectRatio) {
        return imageAspectRatio <= 1 ? 0.14 : 0.20;
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new Application().setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error initializing application.");
            }
        });
    }
}

// TODO:
// разобраться с растровой и векторной графикой
// стрелочки по бокам для пролистывания многостраничных файлов
// избавиться от перемещения печатей при сжатии окна
// разобраться с масштобированием
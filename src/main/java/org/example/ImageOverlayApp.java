package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageOverlayApp extends JFrame {

    private BufferedImage documentImage;
    private BufferedImage leftClickImage;
    private BufferedImage rightClickImage;
    private Point leftClickPosition;
    private Point rightClickPosition;
    private JPanel imagePanel;
    private String evklidSignPath = "/images/evklid/sign.PNG";
    private String evklidStampPath = "/images/evklid/stamp.PNG";
    private String spelsSignPath = "/images/spels/sign.PNG";
    private String spelsStampPath = "/images/spels/stamp.PNG";

    public ImageOverlayApp() throws IOException {
        leftClickImage = ImageIO.read(getClass().getResource(evklidSignPath));
        rightClickImage = ImageIO.read(getClass().getResource(evklidStampPath));
        leftClickPosition = new Point(-1, -1);
        rightClickPosition = new Point(-1, -1);
        setTitle("Stamper");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (documentImage != null) {
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();

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

                    g.drawImage(documentImage, drawX, drawY, drawWidth, drawHeight, this);

                    double scaleFactor = setScaleFactor(imageAspectRatio);
                    int overlayWidth = (int) (drawHeight * scaleFactor);
                    int overlayHeight = overlayWidth;

                    if (leftClickPosition.x >= 0 && leftClickPosition.y >= 0) {
                        int x = leftClickPosition.x - (overlayWidth / 2);
                        int y = leftClickPosition.y - (overlayHeight / 2);
                        g.drawImage(leftClickImage, x, y, overlayWidth, overlayHeight, this);
                    }

                    if (rightClickPosition.x >= 0 && rightClickPosition.y >= 0) {
                        int x = rightClickPosition.x - (overlayWidth / 2);
                        int y = rightClickPosition.y - (overlayHeight / 2);
                        g.drawImage(rightClickImage, x, y, overlayWidth, overlayHeight, this);
                        g.dispose();
                        try {
                            File file = new File("saved_image.png");
                            ImageIO.write(documentImage, "png", file);
                            System.out.println("Image saved successfully.");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };

        // Обработка кликов мыши
        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Получение позиции клика левой кнопкой мыши
                    leftClickPosition.setLocation(e.getPoint());
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // Получение позиции клика правой кнопкой мыши
                    rightClickPosition.setLocation(e.getPoint());
                }
                // Перерисовка панели
                imagePanel.repaint();
            }
        });

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem openMenuItem = new JMenuItem("Открыть");
        JMenuItem saveMenuItem = new JMenuItem("Сохранить");


        openMenuItem.addActionListener(e -> openImage());
        saveMenuItem.addActionListener(e -> saveImage());

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel buttonPanel = new JPanel();
        JButton evklidButton = new JButton("Evklid");
        JButton spelsButton = new JButton("Spels");

        evklidButton.addActionListener(e -> loadStamp(evklidStampPath));
        spelsButton.addActionListener(e -> loadStamp(spelsStampPath));

        buttonPanel.add(evklidButton);
        buttonPanel.add(spelsButton);

        add(imagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStamp(String stampPath) {
        try {
            rightClickImage = ImageIO.read(getClass().getResource(stampPath));
            imagePanel.repaint();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading stamp image.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                documentImage = ImageIO.read(selectedFile);
                // Центрирование окна на экране с учетом размеров изображения
                int imageWidth = documentImage.getWidth();
                int imageHeight = documentImage.getHeight();

                // Установка размеров окна под размеры изображения
                setSize(imageWidth, imageHeight);

                // Получение размера экрана
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                // Вычисление позиции окна для центрирования
                int windowX = (screenSize.width - imageWidth) / 2;

                // Установка позиции окна
                setLocation(windowX, 0);

                imagePanel.repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveImage() {
        if (documentImage == null) {
            JOptionPane.showMessageDialog(this, "No image to save.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BufferedImage resultImage = new BufferedImage(documentImage.getWidth(), documentImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resultImage.createGraphics();
            g2d.drawImage(documentImage, 0, 0, null);

            // Пересчет размеров печатей и подписяй для сохранения
            double imageAspectRatio = (double) documentImage.getWidth() / documentImage.getHeight();
            double scaleFactor = setScaleFactor(imageAspectRatio);
            int overlayWidth = (int) (documentImage.getHeight() * scaleFactor);
            int overlayHeight = overlayWidth;

            // Наложение печати и подписи на временное изображение
            if (leftClickPosition.x >= 0 && leftClickPosition.y >= 0) {
                int x = leftClickPosition.x - (overlayWidth / 2);
                int y = leftClickPosition.y - (overlayHeight / 2);
                g2d.drawImage(leftClickImage, x, y, overlayWidth, overlayHeight, null);
            }

            if (rightClickPosition.x >= 0 && rightClickPosition.y >= 0) {
                int x = rightClickPosition.x - (overlayWidth / 2);
                int y = rightClickPosition.y - (overlayHeight / 2);
                g2d.drawImage(rightClickImage, x, y, overlayWidth, overlayHeight, null);
            }

            g2d.dispose();

            // Открытие диалога для сохранения файла
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                // Сохранение изображения в выбранный файл
                ImageIO.write(resultImage, "png", file);
                JOptionPane.showMessageDialog(this, "Image saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving image.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double setScaleFactor(double imageAspectRatio) {
        return imageAspectRatio <= 1 ? 0.14 : 0.20;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ImageOverlayApp().setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error initializing application.");
            }
        });
    }
}

//TODO:
// решить проблему перемещения печати и подписи;
// добавить расширение jpg при сохранении;
// конвертировать word и pdf в jpg;
// конвертировать jpg в pdf;
// изучить вопрос сохранения качества картинки.
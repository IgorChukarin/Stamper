package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage documentImage;
    private BufferedImage leftClickImage;
    private BufferedImage rightClickImage;
    private Point leftClickPosition;
    private Point rightClickPosition;

    public ImagePanel() {
        this.leftClickPosition = new Point(-1, -1);
        this.rightClickPosition = new Point(-1, -1);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Получаем текущие размеры панели и документа
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                BufferedImage documentImage = getDocumentImage();

                if (documentImage == null) return;

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

                int mouseX = e.getX();
                int mouseY = e.getY();

                // Проверяем, находится ли клик внутри области документа
                if (mouseX >= drawX && mouseX <= drawX + drawWidth &&
                        mouseY >= drawY && mouseY <= drawY + drawHeight) {
                    // Если кликнутый пункт находится в области документа, обновляем координаты
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        leftClickPosition.setLocation(mouseX, mouseY);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        rightClickPosition.setLocation(mouseX, mouseY);
                    }
                    repaint();
                }
            }
        });
    }

    public BufferedImage getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(BufferedImage documentImage) {
        this.documentImage = documentImage;
        repaint();
    }

    public void setLeftClickImage(BufferedImage leftClickImage) {
        this.leftClickImage = leftClickImage;
        repaint();
    }

    public void setRightClickImage(BufferedImage rightClickImage) {
        this.rightClickImage = rightClickImage;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (documentImage != null) {
            drawDocument(g);
            drawStampAndSign(g);
        }
    }


    private void drawDocument(Graphics g) {
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
    }


    private void drawStampAndSign(Graphics g) {
        double imageAspectRatio = (double) documentImage.getWidth() / documentImage.getHeight();
        double scaleFactor = setScaleFactor(imageAspectRatio);
        int overlayWidth = (int) (getHeight() * scaleFactor); //diff
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
        }
    }


    private double setScaleFactor(double imageAspectRatio) {
        return imageAspectRatio <= 1 ? 0.14 : 0.20;
    }


    private void handleMouseClick(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftClickPosition.setLocation(e.getPoint());
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightClickPosition.setLocation(e.getPoint());
        }
        repaint();
    }
}

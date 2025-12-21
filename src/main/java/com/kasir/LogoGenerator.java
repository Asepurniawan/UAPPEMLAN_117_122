package com.kasir;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Class untuk membuat logo Supermarket UMM secara programatis
 */
public class LogoGenerator {
    
    public static void generateLogo() {
        // Ukuran logo: 200x200 pixel
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set antialias untuk hasil yang lebih baik
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background - Gradien Biru
        Paint gradient = new GradientPaint(0, 0, new Color(41, 50, 140), 200, 200, new Color(25, 30, 80));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 200, 200);
        
        // Border putih
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(3, 3, 194, 194);
        
        // Gambar keranjang belanja (shopping cart icon)
        // Badan keranjang
        g2d.setColor(new Color(242, 101, 34)); // Warna Oranye
        int[] xPoints = {50, 150, 140, 60};
        int[] yPoints = {80, 80, 140, 140};
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        // Handle keranjang
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(242, 101, 34));
        g2d.drawArc(45, 40, 110, 60, 0, 180);
        
        // Roda kiri
        g2d.fillOval(60, 140, 15, 15);
        
        // Roda kanan
        g2d.fillOval(125, 140, 15, 15);
        
        // Text "UMM" di bawah
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "UMM";
        int x = (200 - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, 170);
        
        g2d.dispose();
        
        // Simpan ke file
        try {
            File outputFile = new File("assets/logo.png");
            ImageIO.write(image, "png", outputFile);
            System.out.println("Logo berhasil dibuat: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error membuat logo: " + e.getMessage());
        }
    }
}

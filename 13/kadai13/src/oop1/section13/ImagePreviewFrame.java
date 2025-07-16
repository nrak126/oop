package oop1.section13;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class ImagePreviewFrame extends JFrame {
    public ImagePreviewFrame(File file) {
        setTitle(file.getName() + " - 画像プレビュー");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) throw new IOException();
            JLabel label = new JLabel(new ImageIcon(img));
            JScrollPane scrollPane = new JScrollPane(label);
            add(scrollPane, BorderLayout.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ファイルが破損しているか、サポートされていない形式です。", "エラー", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
}

package oop1.section13;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TextPreviewFrame extends JFrame {
    public TextPreviewFrame(File file) {
        setTitle(file.getName() + " - テキストプレビュー");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            JTextArea textArea = new JTextArea(content);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            add(scrollPane, BorderLayout.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ファイルが破損しているか、サポートされていない形式です。", "エラー", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
}

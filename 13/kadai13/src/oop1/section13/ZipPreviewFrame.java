package oop1.section13;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.*;
import java.util.zip.*;

public class ZipPreviewFrame extends JFrame {
    public ZipPreviewFrame(File file) {
        setTitle(file.getName() + " - ZIPプレビュー");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        try {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(file.getName());
            buildZipTree(file, root);
            JTree tree = new JTree(root);
            JScrollPane scrollPane = new JScrollPane(tree);
            add(scrollPane, BorderLayout.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ファイルが破損しているか、サポートされていない形式です。", "エラー", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void buildZipTree(File file, DefaultMutableTreeNode root) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                addEntryToTree(root, entry.getName());
            }
        }
    }

    private void addEntryToTree(DefaultMutableTreeNode root, String path) {
        String[] parts = path.split("/");
        DefaultMutableTreeNode node = root;
        for (String part : parts) {
            if (part.isEmpty()) continue;
            DefaultMutableTreeNode child = null;
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) node.getChildAt(i);
                if (n.getUserObject().equals(part)) {
                    child = n;
                    break;
                }
            }
            if (child == null) {
                child = new DefaultMutableTreeNode(part);
                node.add(child);
            }
            node = child;
        }
    }
}

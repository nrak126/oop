package oop1.section13;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.FileSystemView;

public class MainFrame extends JFrame {
    private JButton openDirButton;
    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private JScrollPane listScrollPane;
    private File currentDirectory;
    private JFrame previewFrame;

    public MainFrame() {
        setTitle("ファイルプレビューアプリケーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        openDirButton = new JButton("ディレクトリを開く");
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setCellRenderer(new FileListCellRenderer());
        listScrollPane = new JScrollPane(fileList);

        openDirButton.addActionListener(e -> openDirectory());
        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                File selected = fileList.getSelectedValue();
                if (selected != null) {
                    openPreview(selected);
                }
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(openDirButton, BorderLayout.NORTH);
        topPanel.add(listScrollPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.CENTER);
    }

    private void openDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentDirectory = chooser.getSelectedFile();
            updateFileList();
        }
    }

    private void updateFileList() {
        listModel.clear();
        if (currentDirectory != null && currentDirectory.isDirectory()) {
            File[] files = currentDirectory.listFiles(File::isFile);
            if (files != null) {
                for (File f : files) {
                    listModel.addElement(f);
                }
            }
        }
    }

    private void openPreview(File file) {
        // 既存プレビューウィンドウがあれば閉じる
        if (previewFrame != null) {
            previewFrame.dispose();
            previewFrame = null;
        }
        String name = file.getName().toLowerCase();
        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif") || name.endsWith(".bmp")) {
            previewFrame = new ImagePreviewFrame(file);
        } else if (name.endsWith(".txt") || name.endsWith(".csv") || name.endsWith(".xml") || name.endsWith(".java") || name.endsWith(".md")) {
            previewFrame = new TextPreviewFrame(file);
        } else if (name.endsWith(".zip")) {
            previewFrame = new ZipPreviewFrame(file);
        } else {
            // 対応外
            return;
        }
        previewFrame.setVisible(true);
    }

    // ファイル名＋アイコン表示用
    private static class FileListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof File) {
                File file = (File) value;
                label.setText(file.getName());
                label.setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
            }
            return label;
        }
    }
}

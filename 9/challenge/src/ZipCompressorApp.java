import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ドラッグ&ドロップでファイルを指定してZIP圧縮するGUIアプリケーション
 */
public class ZipCompressorApp extends JFrame {
    private JPanel dropPanel;
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JButton compressButton;
    private List<File> droppedFiles;
    
    public ZipCompressorApp() {
        initializeGUI();
        setupDragAndDrop();
        droppedFiles = new ArrayList<>();
    }
    
    /**
     * GUIの初期化
     */
    private void initializeGUI() {
        setTitle("ZIP圧縮ツール - ドラッグ&ドロップでファイルを圧縮");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // メインパネル
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // ドロップエリア
        dropPanel = new JPanel();
        dropPanel.setBackground(new Color(240, 248, 255));
        dropPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("ドロップエリア"),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        dropPanel.setLayout(new BorderLayout());
        
        JLabel dropLabel = new JLabel("ここにファイルやフォルダをドロップしてください", JLabel.CENTER);
        dropLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        dropLabel.setForeground(new Color(70, 130, 180));
        dropPanel.add(dropLabel, BorderLayout.CENTER);
        
        // ファイル一覧表示エリア
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBorder(new TitledBorder("ドロップされたファイル"));
        scrollPane.setPreferredSize(new Dimension(580, 200));
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        compressButton = new JButton("ZIP圧縮実行");
        compressButton.setFont(new Font("Dialog", Font.BOLD, 14));
        compressButton.setEnabled(false);
        compressButton.addActionListener(e -> compressFiles());
        
        JButton clearButton = new JButton("リストクリア");
        clearButton.addActionListener(e -> clearFileList());
        
        buttonPanel.add(compressButton);
        buttonPanel.add(clearButton);
        
        // パネルの配置
        mainPanel.add(dropPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * ドラッグ&ドロップ機能の設定
     */
    private void setupDragAndDrop() {
        new DropTarget(dropPanel, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                dropPanel.setBackground(new Color(230, 240, 250));
            }
            
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                // ドラッグ中の処理
            }
            
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
                // ドロップアクション変更時の処理
            }
            
            @Override
            public void dragExit(DropTargetEvent dte) {
                dropPanel.setBackground(new Color(240, 248, 255));
            }
            
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = dtde.getTransferable();
                    
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        addFiles(files);
                        dtde.dropComplete(true);
                    } else {
                        dtde.dropComplete(false);
                    }
                } catch (Exception e) {
                    showErrorDialog("ファイルの読み込みに失敗しました", e);
                    dtde.dropComplete(false);
                }
                dropPanel.setBackground(new Color(240, 248, 255));
            }
        });
    }
    
    /**
     * ファイルをリストに追加
     */
    private void addFiles(List<File> files) {
        for (File file : files) {
            if (!droppedFiles.contains(file)) {
                droppedFiles.add(file);
                String displayName = file.isDirectory() ? 
                    "[フォルダ] " + file.getName() : 
                    "[ファイル] " + file.getName();
                listModel.addElement(displayName + " (" + file.getAbsolutePath() + ")");
            }
        }
        compressButton.setEnabled(!droppedFiles.isEmpty());
    }
    
    /**
     * ファイルリストをクリア
     */
    private void clearFileList() {
        droppedFiles.clear();
        listModel.clear();
        compressButton.setEnabled(false);
    }
    
    /**
     * ZIP圧縮実行
     */
    private void compressFiles() {
        if (droppedFiles.isEmpty()) {
            showInfoDialog("圧縮するファイルがありません。");
            return;
        }
        
        try {
            // 最初のファイルの親ディレクトリを取得
            File firstFile = droppedFiles.get(0);
            File parentDir = firstFile.getParentFile();
            
            // 重複しないZIPファイル名を生成
            File zipFile = generateUniqueZipFileName(parentDir);
            
            // 進行状況ダイアログ表示
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setString("圧縮中...");
            progressBar.setStringPainted(true);
            
            JDialog progressDialog = new JDialog(this, "圧縮中", true);
            progressDialog.add(new JLabel("ファイルを圧縮しています..."), BorderLayout.NORTH);
            progressDialog.add(progressBar, BorderLayout.CENTER);
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(this);
            
            // バックグラウンドで圧縮処理を実行
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    createZipFile(zipFile, droppedFiles);
                    return null;
                }
                
                @Override
                protected void done() {
                    progressDialog.dispose();
                    try {
                        get(); // 例外がある場合はここで発生
                        showSuccessDialog(zipFile);
                        clearFileList();
                    } catch (Exception e) {
                        showErrorDialog("圧縮処理中にエラーが発生しました", e.getCause());
                    }
                }
            };
            
            worker.execute();
            progressDialog.setVisible(true);
            
        } catch (Exception e) {
            showErrorDialog("圧縮処理の開始に失敗しました", e);
        }
    }
    
    /**
     * 重複しないZIPファイル名を生成
     */
    private File generateUniqueZipFileName(File parentDir) {
        String baseName = "archive";
        String extension = ".zip";
        File zipFile = new File(parentDir, baseName + extension);
        
        int counter = 1;
        while (zipFile.exists()) {
            zipFile = new File(parentDir, baseName + "_" + counter + extension);
            counter++;
        }
        
        return zipFile;
    }
    
    /**
     * ZIPファイル作成
     */
    private void createZipFile(File zipFile, List<File> files) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirectoryToZip(zos, file, file.getName());
                } else {
                    addFileToZip(zos, file, file.getName());
                }
            }
        }
    }
    
    /**
     * ディレクトリをZIPに追加
     */
    private void addDirectoryToZip(ZipOutputStream zos, File dir, String basePath) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        // 空のディレクトリの場合
        if (files.length == 0) {
            ZipEntry entry = new ZipEntry(basePath + "/");
            zos.putNextEntry(entry);
            zos.closeEntry();
            return;
        }
        
        for (File file : files) {
            String entryName = basePath + "/" + file.getName();
            if (file.isDirectory()) {
                addDirectoryToZip(zos, file, entryName);
            } else {
                addFileToZip(zos, file, entryName);
            }
        }
    }
    
    /**
     * ファイルをZIPに追加
     */
    private void addFileToZip(ZipOutputStream zos, File file, String entryName) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            
            zos.closeEntry();
        }
    }
    
    /**
     * 成功ダイアログ表示
     */
    private void showSuccessDialog(File zipFile) {
        String message = String.format(
            "圧縮が完了しました！\n\n" +
            "保存先: %s\n" +
            "ファイルサイズ: %.2f MB",
            zipFile.getAbsolutePath(),
            zipFile.length() / (1024.0 * 1024.0)
        );
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "圧縮完了",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * エラーダイアログ表示
     */
    private void showErrorDialog(String message, Throwable throwable) {
        String detailMessage = message;
        if (throwable != null) {
            detailMessage += "\n\n詳細: " + throwable.getMessage();
        }
        
        JOptionPane.showMessageDialog(
            this,
            detailMessage,
            "エラー",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * 情報ダイアログ表示
     */
    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "情報",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ZipCompressorApp().setVisible(true);
        });
    }
}

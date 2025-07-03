package oop1.section11;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 大規模データ集計アプリケーション
 * SwingWorkerを使用してバックグラウンドで大量のCSVデータを処理する
 */
public class DataAggregatorApp extends JFrame implements ActionListener {
    
    // GUI コンポーネント
    private JTextField filePathField;
    private JButton selectFileButton;
    private JButton startButton;
    private JButton cancelButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JTextArea resultArea;
    
    // 処理制御
    private DataProcessor processor;
    
    /**
     * 集計データを保持するクラス
     */
    static class SummaryData {
        private long totalSales = 0;     // 総売上高
        private int transactionCount = 0; // 取引回数
        
        public void addTransaction(int price, int quantity) {
            totalSales += (long) price * quantity;
            transactionCount++;
        }
        
        public long getTotalSales() {
            return totalSales;
        }
        
        public int getTransactionCount() {
            return transactionCount;
        }
        
        public double getAveragePrice() {
            return transactionCount > 0 ? (double) totalSales / transactionCount : 0.0;
        }
    }
    
    /**
     * バックグラウンド処理を行うSwingWorkerクラス
     */
    class DataProcessor extends SwingWorker<Map<String, SummaryData>, Integer> {
        private final String filePath;
        
        public DataProcessor(String filePath) {
            this.filePath = filePath;
        }
        
        @Override
        protected Map<String, SummaryData> doInBackground() throws Exception {
            Map<String, SummaryData> summaryMap = new HashMap<>();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                int lineCount = 0;
                int totalLines = getTotalLines(); // 総行数を取得
                
                // ヘッダー行をスキップ
                reader.readLine();
                lineCount++;
                
                while ((line = reader.readLine()) != null && !isCancelled()) {
                    lineCount++;
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String category = parts[2];
                        int price = Integer.parseInt(parts[3]);
                        int quantity = Integer.parseInt(parts[4]);
                        
                        summaryMap.computeIfAbsent(category, k -> new SummaryData())
                                 .addTransaction(price, quantity);
                    }
                    
                    // 1000行ごとに進捗を更新
                    if (lineCount % 1000 == 0) {
                        int progress = (int) ((double) lineCount / totalLines * 100);
                        publish(progress);
                    }
                }
                
                // 最終進捗を100%に設定
                if (!isCancelled()) {
                    publish(100);
                }
            }
            
            return summaryMap;
        }
        
        @Override
        protected void process(java.util.List<Integer> chunks) {
            if (!chunks.isEmpty()) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
                statusLabel.setText("集計中... " + progress + "%");
            }
        }
        
        @Override
        protected void done() {
            try {
                if (isCancelled()) {
                    statusLabel.setText("キャンセルされました");
                    progressBar.setValue(0);
                } else {
                    Map<String, SummaryData> result = get();
                    displayResults(result);
                    statusLabel.setText("完了");
                    progressBar.setValue(100);
                }
            } catch (InterruptedException | ExecutionException e) {
                statusLabel.setText("エラーが発生しました");
                JOptionPane.showMessageDialog(DataAggregatorApp.this,
                    "処理中にエラーが発生しました: " + e.getMessage(),
                    "エラー",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                resetButtons();
            }
        }
        
        /**
         * ファイルの総行数を取得
         */
        private int getTotalLines() {
            int count = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                while (reader.readLine() != null) {
                    count++;
                }
            } catch (IOException e) {
                return 1000000; // デフォルト値
            }
            return count;
        }
    }
    
    /**
     * コンストラクタ
     */
    public DataAggregatorApp() {
        setupGUI();
    }
    
    /**
     * GUIの初期化
     */
    private void setupGUI() {
        setTitle("大規模データ集計アプリケーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // メインパネル
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // ファイル選択パネル
        JPanel filePanel = createFileSelectionPanel();
        
        // 制御パネル
        JPanel controlPanel = createControlPanel();
        
        // 進捗パネル
        JPanel progressPanel = createProgressPanel();
        
        // 結果表示パネル
        JPanel resultPanel = createResultPanel();
        
        // 上部パネル（ファイル選択、制御、進捗）
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filePanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        topPanel.add(progressPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // 初期状態の設定
        resetButtons();
        
        // ウィンドウのサイズと位置を設定
        setSize(700, 600);
        setLocationRelativeTo(null);
    }
    
    /**
     * ファイル選択パネルを作成
     */
    private JPanel createFileSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("ファイル選択"));
        
        filePathField = new JTextField();
        filePathField.setEditable(false);
        
        selectFileButton = new JButton("ファイルを選択");
        selectFileButton.addActionListener(this);
        
        panel.add(new JLabel("選択されたファイル: "), BorderLayout.WEST);
        panel.add(filePathField, BorderLayout.CENTER);
        panel.add(selectFileButton, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * 制御パネルを作成
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("操作"));
        
        startButton = new JButton("集計開始");
        startButton.addActionListener(this);
        
        cancelButton = new JButton("キャンセル");
        cancelButton.addActionListener(this);
        
        panel.add(startButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    /**
     * 進捗パネルを作成
     */
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("進捗状況"));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        
        statusLabel = new JLabel("待機中");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 結果表示パネルを作成
     */
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("集計結果"));
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * ボタンクリック時の処理
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectFileButton) {
            selectFile();
        } else if (e.getSource() == startButton) {
            startProcessing();
        } else if (e.getSource() == cancelButton) {
            cancelProcessing();
        }
    }
    
    /**
     * ファイル選択処理
     */
    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
            startButton.setEnabled(true);
        }
    }
    
    /**
     * 処理開始
     */
    private void startProcessing() {
        String filePath = filePathField.getText();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "ファイルを選択してください",
                "エラー",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this,
                "選択されたファイルが存在しません",
                "エラー",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // ボタン状態を更新
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        selectFileButton.setEnabled(false);
        
        // 結果エリアをクリア
        resultArea.setText("");
        
        // 進捗をリセット
        progressBar.setValue(0);
        statusLabel.setText("ファイルを読み込み中...");
        
        // バックグラウンド処理を開始
        processor = new DataProcessor(filePath);
        processor.execute();
    }
    
    /**
     * 処理キャンセル
     */
    private void cancelProcessing() {
        if (processor != null && !processor.isDone()) {
            processor.cancel(true);
        }
    }
    
    /**
     * ボタン状態をリセット
     */
    private void resetButtons() {
        startButton.setEnabled(!filePathField.getText().isEmpty());
        cancelButton.setEnabled(false);
        selectFileButton.setEnabled(true);
    }
    
    /**
     * 結果を表示
     */
    private void displayResults(Map<String, SummaryData> summaryMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 商品カテゴリごとの集計結果 ===\n\n");
        
        long grandTotal = 0;
        int totalTransactions = 0;
        
        for (Map.Entry<String, SummaryData> entry : summaryMap.entrySet()) {
            String category = entry.getKey();
            SummaryData data = entry.getValue();
            
            grandTotal += data.getTotalSales();
            totalTransactions += data.getTransactionCount();
            
            sb.append(String.format("【%s】\n", category));
            sb.append(String.format("  総売上高: %,d円\n", data.getTotalSales()));
            sb.append(String.format("  取引回数: %,d回\n", data.getTransactionCount()));
            sb.append(String.format("  平均取引単価: %.2f円\n\n", data.getAveragePrice()));
        }
        
        sb.append("=== 全体集計 ===\n");
        sb.append(String.format("総売上高: %,d円\n", grandTotal));
        sb.append(String.format("総取引回数: %,d回\n", totalTransactions));
        sb.append(String.format("全体平均取引単価: %.2f円\n", 
                totalTransactions > 0 ? (double) grandTotal / totalTransactions : 0.0));
        
        resultArea.setText(sb.toString());
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataAggregatorApp().setVisible(true);
        });
    }
}

package oop1.section11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * アンケート集計アプリケーション
 * ユーザーからのアンケート回答を収集し、CSVファイルに保存する
 */
public class SurveyApp extends JFrame implements ActionListener {
    
    // CSVファイル名
    private static final String CSV_FILE = "survey_results.csv";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // GUI コンポーネント
    private JTextField nameField;
    private ButtonGroup ageGroup;
    private JRadioButton age20s, age30s, age40s;
    private JCheckBox programmingCheck, designCheck, travelCheck;
    private JButton submitButton;
    private JTextArea resultArea;
    
    /**
     * コンストラクタ
     */
    public SurveyApp() {
        setupGUI();
        loadExistingData();
    }
    
    /**
     * GUIの初期化
     */
    private void setupGUI() {
        setTitle("アンケート集計アプリ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // メインパネル
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 入力フォームパネル
        JPanel formPanel = createFormPanel();
        
        // 結果表示パネル
        JPanel resultPanel = createResultPanel();
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // ウィンドウのサイズと位置を設定
        setSize(600, 500);
        setLocationRelativeTo(null);
    }
    
    /**
     * 入力フォームパネルを作成
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("アンケート入力"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 氏名入力
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("氏名:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // 年代選択
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("年代:"), gbc);
        gbc.gridx = 1;
        JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ageGroup = new ButtonGroup();
        age20s = new JRadioButton("20代");
        age30s = new JRadioButton("30代");
        age40s = new JRadioButton("40代");
        ageGroup.add(age20s);
        ageGroup.add(age30s);
        ageGroup.add(age40s);
        agePanel.add(age20s);
        agePanel.add(age30s);
        agePanel.add(age40s);
        formPanel.add(agePanel, gbc);
        
        // 興味のある分野選択
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("興味のある分野:"), gbc);
        gbc.gridx = 1;
        JPanel interestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        programmingCheck = new JCheckBox("プログラミング");
        designCheck = new JCheckBox("デザイン");
        travelCheck = new JCheckBox("旅行");
        interestPanel.add(programmingCheck);
        interestPanel.add(designCheck);
        interestPanel.add(travelCheck);
        formPanel.add(interestPanel, gbc);
        
        // 送信ボタン
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        submitButton = new JButton("回答を送信");
        submitButton.addActionListener(this);
        formPanel.add(submitButton, gbc);
        
        return formPanel;
    }
    
    /**
     * 結果表示パネルを作成
     */
    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("過去の回答一覧"));
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(580, 200));
        
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        return resultPanel;
    }
    
    /**
     * ボタンクリック時の処理
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            submitSurvey();
        }
    }
    
    /**
     * アンケート回答を送信する処理
     */
    private void submitSurvey() {
        try {
            // 入力値を取得
            String name = nameField.getText().trim();
            
            // 入力チェック
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "氏名を入力してください", 
                    "入力エラー", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 年代を取得
            String age = "";
            if (age20s.isSelected()) age = "20代";
            else if (age30s.isSelected()) age = "30代";
            else if (age40s.isSelected()) age = "40代";
            
            // 興味のある分野を取得
            List<String> interests = new ArrayList<>();
            if (programmingCheck.isSelected()) interests.add("プログラミング");
            if (designCheck.isSelected()) interests.add("デザイン");
            if (travelCheck.isSelected()) interests.add("旅行");
            String interestStr = String.join(";", interests);
            
            // 現在の日時を取得
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            
            // CSV形式の文字列を作成
            String csvLine = String.format("%s,%s,%s,%s", timestamp, name, age, interestStr);
            
            // ファイルに保存
            saveToFile(csvLine);
            
            // 結果表示エリアに追加
            String displayLine = String.format("[%s] %s (%s) - %s%n", 
                timestamp, name, age, interestStr.isEmpty() ? "なし" : interestStr);
            resultArea.append(displayLine);
            
            // フォームをクリア
            clearForm();
            
            JOptionPane.showMessageDialog(this, 
                "回答を送信しました", 
                "送信完了", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "エラーが発生しました: " + ex.getMessage(), 
                "エラー", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * CSVファイルに回答を保存
     */
    private void saveToFile(String csvLine) throws IOException {
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            writer.write(csvLine + System.lineSeparator());
        }
    }
    
    /**
     * 既存のデータを読み込む
     */
    private void loadExistingData() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length >= 4) {
                    String timestamp = parts[0];
                    String name = parts[1];
                    String age = parts[2];
                    String interests = parts[3];
                    
                    String displayLine = String.format("[%s] %s (%s) - %s%n", 
                        timestamp, name, age, interests.isEmpty() ? "なし" : interests);
                    resultArea.append(displayLine);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "ファイルの読み込み中にエラーが発生しました: " + ex.getMessage(), 
                "読み込みエラー", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * フォームをクリア
     */
    private void clearForm() {
        nameField.setText("");
        ageGroup.clearSelection();
        programmingCheck.setSelected(false);
        designCheck.setSelected(false);
        travelCheck.setSelected(false);
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SurveyApp().setVisible(true);
        });
    }
}

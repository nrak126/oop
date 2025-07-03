package oop1.section11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * BMI計算アプリケーション
 * 身長と体重を入力してBMIを計算し、肥満度判定を行う
 */
public class BmiCalculatorApp extends JFrame implements ActionListener {
    
    // GUI コンポーネント
    private JTextField heightField;
    private JTextField weightField;
    private JButton calculateButton;
    private JLabel resultLabel;
    
    /**
     * コンストラクタ
     */
    public BmiCalculatorApp() {
        setupGUI();
    }
    
    /**
     * GUIの初期化
     */
    private void setupGUI() {
        setTitle("BMI計算機");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // メインパネルを作成
        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 身長入力部分
        JLabel heightLabel = new JLabel("身長（cm）:");
        heightField = new JTextField();
        mainPanel.add(heightLabel);
        mainPanel.add(heightField);
        
        // 体重入力部分
        JLabel weightLabel = new JLabel("体重（kg）:");
        weightField = new JTextField();
        mainPanel.add(weightLabel);
        mainPanel.add(weightField);
        
        // 計算ボタン
        calculateButton = new JButton("計算実行");
        calculateButton.addActionListener(this);
        mainPanel.add(new JLabel()); // 空のスペース
        mainPanel.add(calculateButton);
        
        // 結果表示部分
        JLabel resultTitleLabel = new JLabel("結果:");
        resultLabel = new JLabel("身長と体重を入力してください");
        resultLabel.setBorder(BorderFactory.createEtchedBorder());
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(resultTitleLabel);
        mainPanel.add(resultLabel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // ウィンドウのサイズと位置を設定
        setSize(500, 300);
        setLocationRelativeTo(null);
    }
    
    /**
     * ボタンクリック時の処理
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == calculateButton) {
            calculateBMI();
        }
    }
    
    /**
     * BMI計算処理
     */
    private void calculateBMI() {
        try {
            // 入力値を取得
            String heightText = heightField.getText().trim();
            String weightText = weightField.getText().trim();
            
            // 入力チェック
            if (heightText.isEmpty() || weightText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "身長と体重の両方を入力してください", 
                    "入力エラー", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 数値に変換
            double height = Double.parseDouble(heightText);
            double weight = Double.parseDouble(weightText);
            
            // 値の妥当性チェック
            if (height <= 0 || weight <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "身長と体重は正の数値を入力してください", 
                    "入力エラー", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // BMI計算（身長をcmからmに変換）
            double heightInMeters = height / 100.0;
            double bmi = weight / (heightInMeters * heightInMeters);
            
            // 判定結果を取得
            String category = getBMICategory(bmi);
            
            // 結果表示（小数点以下2桁）
            String result = String.format("BMI: %.2f (%s)", bmi, category);
            resultLabel.setText(result);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "数値を入力してください", 
                "入力エラー", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * BMI値から肥満度判定を取得
     * @param bmi BMI値
     * @return 判定結果の文字列
     */
    private String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "低体重（痩せ型）";
        } else if (bmi < 25) {
            return "普通体重";
        } else if (bmi < 30) {
            return "肥満（1度）";
        } else {
            return "肥満（2度以上）";
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BmiCalculatorApp().setVisible(true);
        });
    }
}

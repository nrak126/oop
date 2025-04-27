import javax.swing.*;
import java.awt.*;

public class NumArrayStatsApp extends JFrame {

    private JTextField inputField; // 文字を入力するフィールド
    private JButton processButton; // 処理を実行するボタン
    private JTextArea outputArea;  // 処理結果を表示するエリア

    public NumArrayStatsApp() {
        // --- ウィンドウの基本設定 ---
        setTitle("K24142 統計情報算出");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);


        // --- レイアウトにBorderLayoutを採用 ---
        // 部品間の隙間を縦横5ピクセルに設定
        setLayout(new BorderLayout(5, 5));

        // --- 上部に配置する部品 (入力欄、ボタンなど) ---
        // これらの部品をまとめるためのパネルを作成 (FlowLayoutを使用)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel inputLabel = new JLabel("データの入力：");
        inputField = new JTextField(15);
        processButton = new JButton("処理実行");

        // パネルに部品を追加
        topPanel.add(inputLabel);
        topPanel.add(inputField);
        topPanel.add(processButton);

        // --- 中央に配置する部品 (結果表示エリア) ---
        outputArea = new JTextArea(); // 初期サイズはBorderLayoutが調整
        outputArea.setEditable(false); // 編集不可に設定
        // テキストエリアをスクロール可能にする (JScrollPaneでラップ)
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // --- 部品をウィンドウに追加 ---
        // 上部パネルをウィンドウの北 (上) に配置
        add(topPanel, BorderLayout.NORTH);
        // スクロール可能なテキストエリアをウィンドウの中央に配置（中央領域は利用可能な残りのスペースをすべて使う）
        add(scrollPane, BorderLayout.CENTER);

        // --- ボタンのアクション設定 ---
        processButton.addActionListener(e -> {
            String inputText = inputField.getText();
            // テキストエリアをクリア
            outputArea.setText("");
            // 入力されたテキストを整数に変換
            String[] inputArray = inputText.split(",");
            int[] numbers = new int[inputArray.length];
            for (int i = 0; i < inputArray.length; i++) {
                numbers[i] = Integer.parseInt(inputArray[i].trim());
            }
            
            // 配列の合計、平均値、最小値、最大値、中央値、最頻値を計算
            int sum = 0;
            double avg = 0;
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            double median = 0;
            int mode = 0;

            // 合計、最小値、最大値の計算
            for (int number : numbers) {
                sum += number;
                if (number < min) {
                    min = number;
                }
                if (number > max) {
                    max = number;
                }
            }
            // 平均値の計算
            avg = (double) sum / numbers.length;
            // 中央値の計算
            int[] sortedNumbers = numbers.clone();
            java.util.Arrays.sort(sortedNumbers);
            if (sortedNumbers.length % 2 == 0) {
                median = (double)(sortedNumbers[sortedNumbers.length / 2 - 1] + sortedNumbers[sortedNumbers.length / 2]) / 2;
            } else {
                median = sortedNumbers[sortedNumbers.length / 2];
            }
            // 最頻値の計算
            int[] frequency = new int[max + 1];
            for (int number : numbers) {
                frequency[number]++;
            }
            int maxFrequency = 0;
            for (int i = 0; i < frequency.length; i++) {
                if (frequency[i] > maxFrequency) {
                    maxFrequency = frequency[i];
                    mode = i;
                }
            }

            outputArea.append("合計：" + sum + System.lineSeparator());
            outputArea.append("平均：" + avg + System.lineSeparator());
            outputArea.append("最小値：" + min + System.lineSeparator());
            outputArea.append("最大値：" + max + System.lineSeparator());
            outputArea.append("中央値：" + median + System.lineSeparator());
            outputArea.append("最頻値：" + '[' + mode + ']' + System.lineSeparator());
        });
        // --- ウィンドウを表示 ---
        setVisible(true);
    }

    public static void main(String[] args) {
        // イベントディスパッチスレッドでGUIを作成・実行
        SwingUtilities.invokeLater(() -> new NumArrayStatsApp());
    }
}
import javax.swing.*;
import java.awt.*;

public class StoreApp extends JFrame {

    JTextField janCodeField; // janコードを入力するフィールド
    private JTextArea outputArea;  // 処理結果を表示するエリア
    private JButton calcButton; // 処理を実行するボタン

    // レシートオブジェクトの生成
    Receipt receipt = new Receipt();
    // 販売商品を登録するための配列
    ProductItem[] salesProducts = new ProductItem[10];

    public StoreApp() {
        // 商品の登録済みデータを内部に保持
        salesProducts[0] = new ProductItem("オレンジ", 150, 1, "000");
        salesProducts[1] = new ProductItem("バナナ", 200, 1, "111");
        salesProducts[2] = new ProductItem("歯ブラシ", 120, 1, "222");
        salesProducts[3] = new ProductItem("納豆", 110, 1, "333");
        salesProducts[4] = new ProductItem("野菜ジュース", 90, 1, "444");
        salesProducts[5] = new ProductItem("白菜", 170, 1, "555");
        salesProducts[6] = new ProductItem("唐揚げ", 220, 1, "666");
        salesProducts[7] = new ProductItem("フルグラ", 700, 1, "777");
        salesProducts[8] = new ProductItem("お好み焼きソース", 250, 1, "888");
        salesProducts[9] = new ProductItem("ガラムマサラ", 300, 1, "999");


        // --- ウィンドウの基本設定 ---
        setTitle("レジ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        // --- レイアウトにBorderLayoutを採用 ---
        // 部品間の隙間を縦横5ピクセルに設定
        setLayout(new BorderLayout(5, 5));

        // GridBagLayoutを使用して柔軟な配置を行う
        JPanel topPanel = new JPanel(new GridBagLayout());
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // GridBagConstraintsのデフォルト設定
        gbc.insets = new Insets(5, 5, 5, 5); // 部品間の余白
        gbc.anchor = GridBagConstraints.WEST; // 左寄せを基本とする

        // --- 1行目: 商品名ラベルとフィールド ---
        // 商品名ラベル (gridx=0, gridy=0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0; // ラベル列は伸縮させない
        gbc.fill = GridBagConstraints.NONE; // サイズ変更しない
        gbc.anchor = GridBagConstraints.EAST; // ラベルを右寄せにする
        JLabel productNameLabel = new JLabel("janコード:");
        topPanel.add(productNameLabel, gbc);

        // 商品名フィールド (gridx=1, gridy=0)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // フィールド列は横方向に伸縮させる
        gbc.fill = GridBagConstraints.HORIZONTAL; // 横方向にいっぱいに広げる
        janCodeField = new JTextField();
        topPanel.add(janCodeField, gbc);

        // 合計ボタンをボトムパネルに追加
        calcButton = new JButton("合計計算");
        bottomPanel.add(calcButton);

        // --- 中央に配置する部品 (結果表示エリア) ---
        outputArea = new JTextArea();
        // outputArea.setEditable(false); // 必要に応じて編集不可に設定
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // --- 部品をウィンドウに追加 ---
        // 上部パネルをウィンドウの北 (上) に配置
        add(topPanel, BorderLayout.NORTH);
        // スクロール可能なテキストエリアをウィンドウの中央に配置（中央領域は利用可能な残りのスペースをすべて使う）
        add(scrollPane, BorderLayout.CENTER);
        // 下部パネルをウィンドウの南（下）に配置
        add(bottomPanel, BorderLayout.SOUTH);

        // 合計計算ボタンを押した時の処理
        calcButton.addActionListener(e -> {
            // 合計点数と合計金額を出力
            outputArea.append("\n--- 合計点数: " + receipt.getTotalQuantity() + " 点 ---");
            outputArea.append("\n--- 合計金額: " + receipt.getTotalPrice() + " 円 ---\n\n");
        });

        // janコードフィールドでエンターキーが押された時の処理
        janCodeField.addActionListener(e -> {
            // janコードフィールドに入力されたテキストを取得
            String janCode = janCodeField.getText();

            for (int i = 0; i < salesProducts.length; i++) {
                // もし、販売商品に入力したjanコードに一致する商品があれば
                // ついでにnullチェック
                if (salesProducts[i].janCode.equals(janCode) && salesProducts[i] != null) {
                    // レシートに商品を追加する
                    receipt.addProduct(salesProducts[i]);
                    // 結果をテキストエリアに追加
                    String outputLine = salesProducts[i].toString();
                    // appendメソッドで追記
                    outputArea.append(outputLine + System.lineSeparator());
                    // 商品が見つかったらループを抜ける
                    break;
                }
            }
            // janコードフィールドをクリア
            janCodeField.setText("");
            // janコードフィールドにカーソルを移動
            janCodeField.requestFocus();
        });

        // --- ウィンドウを表示 ---
        setVisible(true);
    }
    
    public static void main(String[] args) {
        // イベントディスパッチスレッドでGUIを作成・実行
        SwingUtilities.invokeLater(() -> new StoreApp());
    }
}
import javax.swing.*;
import java.awt.*;
import java.util.Collections;

/**
 * {@link Executable} インターフェイスを実装したタスクを選択し、実行するためのシンプルなGUIアプリケーション。
 * ラジオボタンで処理を選択し、実行ボタンで結果を表示します。
 */
public class SelectableGuiApp extends JFrame {
    private JLabel resultLabel;
    private ButtonGroup taskButtonGroup;

    /**
     * {@code SelectableGuiApp} のコンストラクタ。
     * GUIの初期化とコンポーネントの配置を行います。
     */
    public SelectableGuiApp() {
        setTitle("処理選択デモ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 200);
        setLocationRelativeTo(null);

        // メインパネルの作成とレイアウト設定
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // タスク選択用のラジオボタンを作成
        taskButtonGroup = new ButtonGroup();
        ExecutableRadioButton radioTaskA = new ExecutableRadioButton("タスクAを実行", new TaskA());
        ExecutableRadioButton radioTaskB = new ExecutableRadioButton("タスクBを実行", new TaskB());
        
        radioTaskA.setSelected(true);
        taskButtonGroup.add(radioTaskA);
        taskButtonGroup.add(radioTaskB);

        // 実行ボタンの作成
        JButton executeButton = new JButton("実行");

        // 結果表示ラベルの作成
        resultLabel = new JLabel("ここに結果が表示されます", SwingConstants.CENTER);
        resultLabel.setPreferredSize(new Dimension(300, 30));

        // 実行ボタンにActionListenerを追加
        executeButton.addActionListener(e -> {
            // 選択されているラジオボタンから直接タスクを取得して実行
            Collections.list(taskButtonGroup.getElements()).stream()
                .filter(AbstractButton::isSelected)
                .filter(b -> b instanceof ExecutableRadioButton)
                .map(b -> ((ExecutableRadioButton) b).getTask())
                .findFirst()
                .ifPresentOrElse(
                    task -> resultLabel.setText("結果: " + task.execute()),
                    () -> resultLabel.setText("エラー: 処理が選択されていません。")
                );
        });

        // パネルにコンポーネントを追加
        panel.add(new JLabel("実行するタスクを選択:"));
        panel.add(radioTaskA);
        panel.add(radioTaskB);
        panel.add(executeButton);
        panel.add(resultLabel);

        // フレームにパネルを追加
        add(panel);
    }

    /**
     * アプリケーションのメインエントリポイント。
     * GUIをイベントディスパッチスレッドで安全に起動します。
     *
     * @param args コマンドライン引数 (未使用)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SelectableGuiApp().setVisible(true));
    }
}

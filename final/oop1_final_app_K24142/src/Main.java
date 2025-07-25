import javax.swing.*;

/**
 * Simple Todo App - メインクラス
 * アプリケーションのエントリーポイント
 */
public class Main {
    public static void main(String[] args) {
        // Event Dispatch Thread上でGUIを起動
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TodoApp().setVisible(true);
            }
        });
    }
}
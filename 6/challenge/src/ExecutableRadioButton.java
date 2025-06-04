import javax.swing.JRadioButton;

/**
 * 実行可能なタスクと関連付けられたラジオボタンコンポーネント。
 * タスクの選択と実行を一元管理します。
 */
public class ExecutableRadioButton extends JRadioButton {
    private final Executable task;

    /**
     * 指定されたラベルとタスクでラジオボタンを構築します。
     *
     * @param label ラジオボタンに表示するラベル
     * @param task このボタンに関連付けられる実行可能なタスク
     */
    public ExecutableRadioButton(String label, Executable task) {
        super(label);
        this.task = task;
    }

    /**
     * このラジオボタンに関連付けられたタスクを取得します。
     *
     * @return 関連付けられたタスク
     */
    public Executable getTask() {
        return task;
    }
}

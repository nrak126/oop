/**
 * {@link Executable} インターフェイスを実装するクラス。
 * タスクAの具体的な処理を定義します。
 */
public class TaskA implements Executable {
    /**
     * タスクAの処理を実行し、その結果を説明する文字列を返します。
     *
     * @return タスクAの実行結果メッセージ
     */
    @Override
    public String execute() {
        // コンソールにも実行中であることを示すメッセージを出力（デバッグや確認用）
        System.out.println("コンソール: TaskAを実行中...");
        return "タスクAを実行しました！";
    }
}

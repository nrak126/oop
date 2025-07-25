import java.io.Serializable;

/**
 * タスク情報を保持するクラス
 * シリアライズ機能を使用してファイル保存を行うため、Serializableインタフェースを実装
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String taskName;    // タスクの内容
    private boolean isCompleted; // タスクの完了状態
    
    /**
     * コンストラクタ
     * @param taskName タスクの内容
     */
    public Task(String taskName) {
        this.taskName = taskName;
        this.isCompleted = false; // 初期状態は未完了
    }
    
    /**
     * タスク名を取得
     * @return タスク名
     */
    public String getTaskName() {
        return taskName;
    }
    
    /**
     * タスク名を設定
     * @param taskName タスク名
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    /**
     * 完了状態を取得
     * @return true: 完了, false: 未完了
     */
    public boolean isCompleted() {
        return isCompleted;
    }
    
    /**
     * 完了状態を設定
     * @param completed 完了状態
     */
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }
    
    /**
     * 完了状態を切り替える
     */
    public void toggleCompleted() {
        this.isCompleted = !this.isCompleted;
    }
    
    @Override
    public String toString() {
        return taskName;
    }
}

package oop1.todoapp;

import java.time.LocalDate;

/**
 * タスクを表現するクラス。
 * Taskableインターフェースを実装し、タスクの内容、期限日、完了状態を管理します。
 */
public class Task implements Taskable {
    private String description;
    private LocalDate dueDate;
    private boolean completed;

    /**
     * タスクを生成します。
     *
     * @param description タスクの説明
     * @param dueDate 期限日（null可）
     */
    public Task(String description, LocalDate dueDate) {
        setDescription(description);
        setDueDate(dueDate);
        setCompleted(false);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("タスクの説明にnullや空文字列は指定できません。");
        }
        this.description = description.trim();
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(completed ? "完了" : "未完了");
        sb.append("] ");
        sb.append(description);
        if (dueDate != null) {
            sb.append(" (期限: ").append(dueDate.format(DATE_FORMATTER)).append(")");
        } else {
            sb.append(" (期限: 未設定)");
        }
        return sb.toString();
    }
}

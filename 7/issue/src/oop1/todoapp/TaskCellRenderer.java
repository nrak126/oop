package oop1.todoapp;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

/**
 * タスクの表示をカスタマイズするためのレンダラー。
 * 完了状態に応じて、タスクの表示スタイルを変更します。
 */
class TaskCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        
        // 親クラスの実装を呼び出し、基本的なJLabelコンポーネントを取得
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        // タスクオブジェクトの場合のみ処理
        if (value instanceof Task) {
            Task task = (Task) value;

            // タスクの文字列表現を設定
            label.setText(task.toString());

            // 完了状態に応じてスタイルを変更
            if (task.isCompleted()) {
                // 完了したタスクは灰色で取り消し線付き
                label.setForeground(Color.GRAY);
                
                // フォント属性を操作して取り消し線を追加
                Map<TextAttribute, Object> attributes = new HashMap<>(label.getFont().getAttributes());
                attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                label.setFont(label.getFont().deriveFont(attributes));
            } else {
                // 未完了のタスクはデフォルトスタイル
                label.setForeground(list.getForeground());
                
                // 取り消し線を解除
                Map<TextAttribute, Object> attributes = new HashMap<>(label.getFont().getAttributes());
                attributes.put(TextAttribute.STRIKETHROUGH, false);
                label.setFont(label.getFont().deriveFont(attributes));
            }
        }

        return label;
    }
}

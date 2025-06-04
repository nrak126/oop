package oop1.todoapp;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * TODOリストアプリケーションのメインクラスです。
 * GUIの構築とイベント処理を担当します。
 */
public class TodoListApp {
    private JFrame frame;                     // メインウィンドウ
    private DefaultListModel<Task> listModel; // JListのモデル
    private JList<Task> taskList;            // タスク表示用リスト
    private JTextField taskInput;             // タスク内容入力用テキストフィールド
    private JTextField dueDateInput;          // 期限日入力用テキストフィールド
    private List<Task> tasks;                 // タスクを格納するArrayList

    /**
     * アプリケーションを初期化し、GUIを表示します。
     */
    public TodoListApp() {
        // データ構造の初期化
        tasks = new ArrayList<>();
        listModel = new DefaultListModel<>();

        // メインフレームの設定
        frame = new JFrame("TODO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 450);
        frame.setLayout(new BorderLayout(5, 5));

        // 入力パネルの作成
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        // タスク内容入力フィールド
        inputPanel.add(new JLabel("タスク内容:"));
        taskInput = new JTextField(20);
        inputPanel.add(taskInput);

        // 期限日入力フィールド
        inputPanel.add(new JLabel("期限日 (YYYY-MM-DD):"));
        dueDateInput = new JTextField(10);
        inputPanel.add(dueDateInput);

        // 追加ボタン
        JButton addButton = new JButton("追加");
        addButton.addActionListener(e -> addTask());
        inputPanel.add(addButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        // タスク表示リストの作成
        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TaskCellRenderer());

        JScrollPane scrollPane = new JScrollPane(taskList);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 操作ボタンパネルの作成
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // 完了/未完了切り替えボタン
        JButton toggleCompleteButton = new JButton("完了/未完了");
        toggleCompleteButton.addActionListener(e -> toggleTaskComplete());
        buttonPanel.add(toggleCompleteButton);

        // 削除ボタン
        JButton deleteButton = new JButton("削除");
        deleteButton.addActionListener(e -> deleteTask());
        buttonPanel.add(deleteButton);

        // 期限日ソートボタン
        JButton sortByDueDateButton = new JButton("期限日でソート");
        sortByDueDateButton.addActionListener(e -> sortByDueDate());
        buttonPanel.add(sortByDueDateButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // フレームを画面中央に表示し、可視化
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 新しいタスクを追加します。
     */
    private void addTask() {
        String description = taskInput.getText().trim();
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "タスク内容を入力してください。",
                    "エラー",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate dueDate = Taskable.parseDueDate(dueDateInput.getText());
        Task newTask = new Task(description, dueDate);
        tasks.add(newTask);
        updateListModel();

        // 入力フィールドをクリア
        taskInput.setText("");
        dueDateInput.setText("");
    }

    /**
     * 選択されたタスクの完了状態を切り替えます。
     */
    private void toggleTaskComplete() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = listModel.getElementAt(selectedIndex);
            task.setCompleted(!task.isCompleted());
            taskList.repaint();
        }
    }

    /**
     * 選択されたタスクを削除します。
     */
    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = listModel.getElementAt(selectedIndex);
            tasks.remove(task);
            updateListModel();
        }
    }

    /**
     * タスクを期限日でソートします。
     */
    private void sortByDueDate() {
        tasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                LocalDate date1 = t1.getDueDate();
                LocalDate date2 = t2.getDueDate();
                
                // nullの場合は後ろに配置
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;
                if (date2 == null) return -1;
                
                return date1.compareTo(date2);
            }
        });
        updateListModel();
    }

    /**
     * リストモデルを更新します。
     */
    private void updateListModel() {
        listModel.clear();
        for (Task task : tasks) {
            listModel.addElement(task);
        }
    }

    /**
     * アプリケーションのエントリーポイントです。
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TodoListApp());
    }
}

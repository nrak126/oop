import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Simple Todo App - メインのGUIアプリケーションクラス
 */
public class TodoApp extends JFrame {
    private static final String DATA_FILE = "tasks.dat";
    
    private ArrayList<Task> tasks;
    private ArrayList<Task> filteredTasks;
    private DefaultListModel<Task> listModel;
    private JTextField taskInputField;
    private JTextField searchField;
    private JList<Task> taskList;
    private JButton addButton;
    private JButton deleteButton;
    private JButton deleteCompletedButton;
    private JComboBox<String> filterComboBox;
    private String currentFilter = "すべて";
    private String currentSearchText = "";
    
    /**
     * コンストラクタ - GUIの初期化とデータの読み込み
     */
    public TodoApp() {
        tasks = new ArrayList<>();
        filteredTasks = new ArrayList<>();
        listModel = new DefaultListModel<>();
        
        // 保存されたデータを読み込み
        loadTasks();
        
        // GUIの初期化
        initializeGUI();
        
        // ウィンドウ閉じるイベントで自動保存
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveTasks();
                System.exit(0);
            }
        });
    }
    
    /**
     * GUIコンポーネントの初期化
     */
    private void initializeGUI() {
        setTitle("Simple Todo App");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // メインパネル
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // ① タスク入力エリア (上部)
        JPanel inputPanel = new JPanel(new BorderLayout());
        taskInputField = new JTextField();
        addButton = new JButton("追加");
        
        inputPanel.add(new JLabel("新しいタスク: "), BorderLayout.WEST);
        inputPanel.add(taskInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        // 検索・フィルタエリア
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 25));
        
        // フィルタコンボボックス
        String[] filterOptions = {"すべて", "未完了", "完了済み"};
        filterComboBox = new JComboBox<>(filterOptions);
        
        JPanel searchControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlPanel.add(new JLabel("検索:"));
        searchControlPanel.add(searchField);
        searchControlPanel.add(new JLabel("表示:"));
        searchControlPanel.add(filterComboBox);
        
        searchPanel.add(searchControlPanel, BorderLayout.WEST);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        // 上部パネルの組み合わせ
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        // ② タスク表示エリア (中央)
        taskList = new JList<>(listModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("タスク一覧"));
        
        // ③ 操作ボタンエリア (下部)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        deleteButton = new JButton("選択項目を削除");
        deleteCompletedButton = new JButton("完了済みタスクを一括削除");
        
        buttonPanel.add(deleteButton);
        buttonPanel.add(deleteCompletedButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // パネルを配置
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // イベントリスナーの設定
        setupEventListeners();
        
        // 既存のタスクをリストに表示
        updateTaskList();
    }
    
    /**
     * イベントリスナーの設定
     */
    private void setupEventListeners() {
        // 追加ボタンのクリックイベント
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });
        
        // Enterキーでタスク追加
        taskInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });
        
        // 削除ボタンのクリックイベント
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTasks();
            }
        });
        
        // 完了済みタスク一括削除ボタンのクリックイベント
        deleteCompletedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCompletedTasks();
            }
        });
        
        // フィルタコンボボックスの変更イベント
        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFilter = (String) filterComboBox.getSelectedItem();
                updateTaskList();
            }
        });
        
        // 検索フィールドの入力イベント
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSearch();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSearch();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSearch();
            }
            
            private void updateSearch() {
                currentSearchText = searchField.getText().toLowerCase();
                updateTaskList();
            }
        });
        
        // リスト項目のクリックでチェックボックスを処理
        taskList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index >= 0 && index < filteredTasks.size()) {
                    Rectangle cellBounds = taskList.getCellBounds(index, index);
                    if (cellBounds.contains(e.getPoint())) {
                        // チェックボックスの領域内かチェック（おおよそ左端25px）
                        if (e.getX() <= cellBounds.x + 25) {
                            Task task = filteredTasks.get(index);
                            task.toggleCompleted();
                            updateTaskList();
                        }
                    }
                }
            }
        });
    }
    
    /**
     * タスクを追加
     */
    private void addTask() {
        String taskName = taskInputField.getText().trim();
        if (!taskName.isEmpty()) {
            Task newTask = new Task(taskName);
            tasks.add(newTask);
            updateTaskList();
            taskInputField.setText("");
            taskInputField.requestFocus();
        }
    }
    
    /**
     * 選択されたタスクを削除
     */
    private void deleteSelectedTasks() {
        int[] selectedIndices = taskList.getSelectedIndices();
        if (selectedIndices.length > 0) {
            // 選択されたタスクの情報を取得
            ArrayList<Task> selectedTasks = new ArrayList<>();
            for (int index : selectedIndices) {
                selectedTasks.add(filteredTasks.get(index));
            }
            
            // 削除確認ダイアログ
            String message;
            if (selectedTasks.size() == 1) {
                message = "以下のタスクを削除しますか？\n\n「" + selectedTasks.get(0).getTaskName() + "」";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("以下の ").append(selectedTasks.size()).append(" 件のタスクを削除しますか？\n\n");
                for (int i = 0; i < Math.min(selectedTasks.size(), 5); i++) {
                    sb.append("・").append(selectedTasks.get(i).getTaskName()).append("\n");
                }
                if (selectedTasks.size() > 5) {
                    sb.append("...他 ").append(selectedTasks.size() - 5).append(" 件");
                }
                message = sb.toString();
            }
            
            int result = JOptionPane.showConfirmDialog(
                this,
                message,
                "タスク削除確認",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // 元のタスクリストから削除
                tasks.removeAll(selectedTasks);
                updateTaskList();
                
                JOptionPane.showMessageDialog(
                    this,
                    selectedTasks.size() + " 件のタスクを削除しました。",
                    "削除完了",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
    
    /**
     * タスクリストの表示を更新
     */
    private void updateTaskList() {
        // フィルタリングと検索を適用
        filteredTasks.clear();
        
        for (Task task : tasks) {
            // 検索フィルタ
            boolean matchesSearch = currentSearchText.isEmpty() || 
                task.getTaskName().toLowerCase().contains(currentSearchText);
            
            // ステータスフィルタ
            boolean matchesFilter = false;
            switch (currentFilter) {
                case "すべて":
                    matchesFilter = true;
                    break;
                case "未完了":
                    matchesFilter = !task.isCompleted();
                    break;
                case "完了済み":
                    matchesFilter = task.isCompleted();
                    break;
            }
            
            if (matchesSearch && matchesFilter) {
                filteredTasks.add(task);
            }
        }
        
        // 完了済みタスクを一番下に移動
        Collections.sort(filteredTasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                // 完了状態で比較（false < true なので、未完了が先に来る）
                int completedComparison = Boolean.compare(t1.isCompleted(), t2.isCompleted());
                if (completedComparison != 0) {
                    return completedComparison;
                }
                // 同じ完了状態の場合は、タスク名でソート
                return t1.getTaskName().compareToIgnoreCase(t2.getTaskName());
            }
        });
        
        // リストモデルを更新
        listModel.clear();
        for (Task task : filteredTasks) {
            listModel.addElement(task);
        }
    }
    
    /**
     * タスクデータをファイルから読み込み
     */
    private void loadTasks() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                ArrayList<Task> loadedTasks = (ArrayList<Task>) ois.readObject();
                tasks = loadedTasks;
                System.out.println("タスクデータを読み込みました: " + tasks.size() + "件");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("タスクデータの読み込みに失敗しました: " + e.getMessage());
                tasks = new ArrayList<>();
            }
        } else {
            System.out.println("保存ファイルが見つかりません。新しくタスクリストを開始します。");
        }
    }
    
    /**
     * タスクデータをファイルに保存
     */
    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks);
            System.out.println("タスクデータを保存しました: " + tasks.size() + "件");
        } catch (IOException e) {
            System.err.println("タスクデータの保存に失敗しました: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this,
                "タスクデータの保存に失敗しました。",
                "保存エラー",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * タスクリストのカスタムセルレンダラー
     * チェックボックス付きで完了したタスクに取り消し線を表示
     */
    private class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {
        private JCheckBox checkBox;
        private JLabel label;
        
        public TaskCellRenderer() {
            setLayout(new BorderLayout());
            checkBox = new JCheckBox();
            label = new JLabel();
            
            add(checkBox, BorderLayout.WEST);
            add(label, BorderLayout.CENTER);
            
            // チェックボックスのクリックイベント
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = taskList.getSelectedIndex();
                    if (index >= 0 && index < filteredTasks.size()) {
                        Task task = filteredTasks.get(index);
                        task.setCompleted(checkBox.isSelected());
                        updateTaskList();
                        
                        // 選択状態を保持
                        taskList.setSelectedIndex(index);
                    }
                }
            });
        }
        
        @Override
        public Component getListCellRendererComponent(
                JList<? extends Task> list, Task task, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            if (task != null) {
                checkBox.setSelected(task.isCompleted());
                
                String displayText = task.getTaskName();
                if (task.isCompleted()) {
                    // 完了したタスクには取り消し線を表示
                    label.setText("<html><strike>" + displayText + "</strike></html>");
                    label.setForeground(isSelected ? Color.WHITE : Color.GRAY);
                } else {
                    // 未完了のタスク
                    label.setText(displayText);
                    label.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                }
                
                // 背景色の設定
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    checkBox.setBackground(list.getSelectionBackground());
                    label.setBackground(list.getSelectionBackground());
                } else {
                    setBackground(list.getBackground());
                    checkBox.setBackground(list.getBackground());
                    label.setBackground(list.getBackground());
                }
                
                checkBox.setOpaque(true);
                label.setOpaque(true);
                setOpaque(true);
            }
            
            return this;
        }
    }
    
    /**
     * 完了済みタスクを一括削除
     */
    private void deleteCompletedTasks() {
        ArrayList<Task> completedTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }
        
        if (completedTasks.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "完了済みのタスクがありません。",
                "情報",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "完了済みのタスク " + completedTasks.size() + " 件を削除しますか？\n\n削除されるタスク:\n" +
            getCompletedTasksList(completedTasks),
            "完了済みタスク削除確認",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            tasks.removeAll(completedTasks);
            updateTaskList();
            JOptionPane.showMessageDialog(
                this,
                completedTasks.size() + " 件のタスクを削除しました。",
                "削除完了",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * 完了済みタスクのリスト文字列を作成
     */
    private String getCompletedTasksList(ArrayList<Task> completedTasks) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Task task : completedTasks) {
            if (count >= 5) {
                sb.append("...他 ").append(completedTasks.size() - 5).append(" 件");
                break;
            }
            sb.append("・").append(task.getTaskName()).append("\n");
            count++;
        }
        return sb.toString();
    }
}

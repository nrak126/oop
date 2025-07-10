package oop1.section12;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 生徒名簿アプリケーションのメインフレーム
 */
public class MainFrame extends JFrame {
    private JTree tree;
    private JTable table;
    private DefaultTableModel tableModel;
    private Document document;
    private JLabel statusLabel;
    
    public MainFrame() {
        initializeComponents();
        loadXMLData();
    }
    
    /**
     * GUI コンポーネントの初期化
     */
    private void initializeComponents() {
        setTitle("生徒名簿アプリ - StudentViewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // レイアウトの設定
        setLayout(new BorderLayout());
        
        // 左側のJTree
        tree = new JTree();
        tree.setRootVisible(true);
        tree.addMouseListener(new TreeMouseListener());
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(300, 0));
        
        // 右側のJTable
        String[] columnNames = {"学生ID", "氏名", "学年"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(table);
        
        // スプリットペインで左右を分割
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                            treeScrollPane, tableScrollPane);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.3);
        
        add(splitPane, BorderLayout.CENTER);
        
        // ステータスバー
        statusLabel = new JLabel("XMLファイルを読み込み中...");
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
     * XMLファイルの読み込みと解析
     */
    private void loadXMLData() {
        try {
            File xmlFile = new File("university_data.xml");
            if (!xmlFile.exists()) {
                statusLabel.setText("エラー: XMLファイルが見つかりません");
                JOptionPane.showMessageDialog(this, 
                    "university_data.xmlファイルが見つかりません。", 
                    "エラー", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();
            
            buildTree();
            statusLabel.setText("XMLファイルを正常に読み込みました");
            
        } catch (Exception e) {
            statusLabel.setText("エラー: XMLファイルの読み込みに失敗しました");
            JOptionPane.showMessageDialog(this, 
                "XMLファイルの読み込みでエラーが発生しました: " + e.getMessage(), 
                "エラー", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * XMLデータからJTreeを構築
     */
    private void buildTree() {
        Element root = document.getDocumentElement();
        String universityName = root.getAttribute("name");
        
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(universityName);
        
        NodeList faculties = root.getElementsByTagName("faculty");
        for (int i = 0; i < faculties.getLength(); i++) {
            Element faculty = (Element) faculties.item(i);
            String facultyName = faculty.getAttribute("name");
            DefaultMutableTreeNode facultyNode = new DefaultMutableTreeNode(facultyName);
            rootNode.add(facultyNode);
            
            NodeList departments = faculty.getElementsByTagName("department");
            for (int j = 0; j < departments.getLength(); j++) {
                Element department = (Element) departments.item(j);
                String departmentName = department.getAttribute("name");
                DefaultMutableTreeNode departmentNode = new DefaultMutableTreeNode(departmentName);
                facultyNode.add(departmentNode);
            }
        }
        
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        tree.setModel(treeModel);
        
        // ツリーを展開
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
    
    /**
     * 選択された学科/専攻の学生情報をテーブルに表示
     */
    private void displayStudents(String departmentName) {
        // テーブルをクリア
        tableModel.setRowCount(0);
        
        if (document == null) return;
        
        List<Student> students = getStudentsInDepartment(departmentName);
        
        for (Student student : students) {
            Object[] row = {student.getId(), student.getName(), student.getGrade()};
            tableModel.addRow(row);
        }
        
        statusLabel.setText("学科/専攻: " + departmentName + " - " + students.size() + "名の学生を表示中");
    }
    
    /**
     * 指定された学科/専攻の学生リストを取得
     */
    private List<Student> getStudentsInDepartment(String departmentName) {
        List<Student> students = new ArrayList<>();
        
        NodeList departments = document.getElementsByTagName("department");
        for (int i = 0; i < departments.getLength(); i++) {
            Element department = (Element) departments.item(i);
            String deptName = department.getAttribute("name");
            
            if (deptName.equals(departmentName)) {
                NodeList studentNodes = department.getElementsByTagName("student");
                for (int j = 0; j < studentNodes.getLength(); j++) {
                    Element studentElement = (Element) studentNodes.item(j);
                    String id = studentElement.getAttribute("id");
                    String name = studentElement.getElementsByTagName("name").item(0).getTextContent();
                    String grade = studentElement.getElementsByTagName("grade").item(0).getTextContent();
                    
                    students.add(new Student(id, name, grade));
                }
                break;
            }
        }
        
        return students;
    }
    
    /**
     * ツリーのマウスクリックリスナー
     */
    private class TreeMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                DefaultMutableTreeNode node = 
                    (DefaultMutableTreeNode) path.getLastPathComponent();
                
                // 学科または専攻レベルの場合のみテーブルを更新
                if (path.getPathCount() == 3) { // 大学 -> 学部 -> 学科/専攻
                    String departmentName = node.toString();
                    displayStudents(departmentName);
                }
            }
        }
    }
    
    /**
     * 学生情報を格納するクラス
     */
    private static class Student {
        private String id;
        private String name;
        private String grade;
        
        public Student(String id, String name, String grade) {
            this.id = id;
            this.name = name;
            this.grade = grade;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getGrade() { return grade; }
    }
    
    /**
     * アプリケーションのエントリーポイント
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}

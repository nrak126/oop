import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;

// メインウィンドウ
public class MainFrame extends JFrame {
    private DrawingPanel drawingPanel;

    public MainFrame() {
        setTitle("図形描画");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        drawingPanel = new DrawingPanel();

        // --- 図形選択ラジオボタン ---
        JRadioButton circleRadioButton = new JRadioButton("円");
        circleRadioButton.setActionCommand("Circle"); // アクションコマンドを設定
        circleRadioButton.setSelected(true); // 最初は円を選択状態にする
        drawingPanel.setCurrentShapeType("Circle"); // DrawingPanelの初期状態も合わせる

        JRadioButton rectangleRadioButton = new JRadioButton("四角形");
        rectangleRadioButton.setActionCommand("Rectangle");
        JRadioButton triangleRadioButton = new JRadioButton("三角形");
        triangleRadioButton.setActionCommand("Triangle");

        // ButtonGroupを作成し、ラジオボタンをグループ化する
        // これにより、一度に1つのラジオボタンのみが選択されるようになる
        ButtonGroup shapeGroup = new ButtonGroup();
        shapeGroup.add(circleRadioButton);
        shapeGroup.add(rectangleRadioButton);
        shapeGroup.add(triangleRadioButton);

        // ラジオボタン用のアクションリスナー
        ActionListener shapeSelectionListener = e -> {
            // 選択されたラジオボタンのアクションコマンドをDrawingPanelに伝える
            drawingPanel.setCurrentShapeType(e.getActionCommand());
            // if (e.getSource() == circleRadioButton) でイベントの発生元コンポーネントで条件分岐可能
        };

        circleRadioButton.addActionListener(shapeSelectionListener);
        rectangleRadioButton.addActionListener(shapeSelectionListener);
        triangleRadioButton.addActionListener(shapeSelectionListener);
        // --- ここまで図形選択ラジオボタン ---

        // --- 色選択ラジオボタン ---
        JRadioButton redRadioButton = new JRadioButton("赤");
        redRadioButton.setForeground(Color.RED);
        JRadioButton blueRadioButton = new JRadioButton("青");
        blueRadioButton.setForeground(Color.BLUE);
        JRadioButton greenRadioButton = new JRadioButton("緑");
        greenRadioButton.setForeground(Color.GREEN);

        // ButtonGroupで色選択ラジオボタンをグループ化
        ButtonGroup colorGroup = new ButtonGroup();
        colorGroup.add(redRadioButton);
        colorGroup.add(blueRadioButton);
        colorGroup.add(greenRadioButton);

        // 初期選択色を設定 (例: 青)
        blueRadioButton.setSelected(true);
        drawingPanel.setCurrentColor(Color.BLUE);

        // 色選択ラジオボタン用のアクションリスナー
        ActionListener colorSelectionListener = e -> {
            if (e.getSource() == redRadioButton) {
                drawingPanel.setCurrentColor(Color.RED);
            }
            //TODO: 他のラジオボタンの処理を実装する
            if (e.getSource() == blueRadioButton) {
                drawingPanel.setCurrentColor(Color.blue);
            }
            if (e.getSource() == greenRadioButton) {
                drawingPanel.setCurrentColor(Color.green);
            }
        };

        redRadioButton.addActionListener(colorSelectionListener);
        blueRadioButton.addActionListener(colorSelectionListener);
        greenRadioButton.addActionListener(colorSelectionListener);
        // --- ここまで色選択ラジオボタン ---
        
        // --- 描画モードラジオボタン ---
        JRadioButton drawRadioButton = new JRadioButton("描く");
        drawRadioButton.setActionCommand("Draw");
        drawRadioButton.setSelected(true);
        drawingPanel.setCurrentDrawMode("Draw");
        JRadioButton moveRadioButton = new JRadioButton("移動");
        moveRadioButton.setActionCommand("Move");
        JRadioButton deleteRadioButton = new JRadioButton("削除");
        deleteRadioButton.setActionCommand("Delete");
        
        // ButtonGroupを作成し、ラジオボタンをグループ化する
        // これにより、一度に1つのラジオボタンのみが選択されるようになる
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(drawRadioButton);
        modeGroup.add(moveRadioButton);
        modeGroup.add(deleteRadioButton);
        
        // ラジオボタン用のアクションリスナー
        ActionListener modeSelectionListener = e -> {
            // 選択されたラジオボタンのアクションコマンドをDrawingPanelに伝える
            drawingPanel.setCurrentDrawMode(e.getActionCommand());
            // if (e.getSource() == circleRadioButton) でイベントの発生元コンポーネントで条件分岐可能
        };
        
        drawRadioButton.addActionListener(modeSelectionListener);
        moveRadioButton.addActionListener(modeSelectionListener);
        deleteRadioButton.addActionListener(modeSelectionListener);
        // --- ここまで描画モードラジオボタン ---
        
        // --- クリアボタン ---
        JButton clearButton = new JButton("クリア");
        clearButton.addActionListener(e -> {
            //TODO: クリアボタンの処理を実装する
            drawingPanel.clearShapes();
        });
        // --- ここまでクリアボタン ---

        // --- 元に戻すボタン ---
        JButton undoButton = new JButton("元に戻す");
        undoButton.addActionListener(e -> {
            drawingPanel.undo();
        });
        // --- ここまで元に戻すボタン ---

        // --- やり直しボタン ---
        JButton redoButton = new JButton("やり直し");
        redoButton.addActionListener(e -> {
            drawingPanel.redo();
        });
        // --- ここまでやり直しボタン ---

        // ツールバーにコンポーネントを配置
        JToolBar toolBar = new JToolBar();
        toolBar.add(new JLabel("図形: "));
        toolBar.add(circleRadioButton);
        toolBar.add(rectangleRadioButton);
        toolBar.add(triangleRadioButton);
        toolBar.addSeparator();
        toolBar.add(new JLabel("色: "));
        toolBar.add(redRadioButton);
        toolBar.add(blueRadioButton);
        toolBar.add(greenRadioButton);
        toolBar.addSeparator();
        toolBar.add(new JLabel("描画モード: "));
        toolBar.add(drawRadioButton);
        toolBar.add(moveRadioButton);
        toolBar.add(deleteRadioButton);
        toolBar.add(undoButton);
        toolBar.add(redoButton);
        toolBar.add(clearButton);

        add(toolBar, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
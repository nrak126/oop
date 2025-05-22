import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// 図形を描画するパネル
public class DrawingPanel extends JPanel {
    private Shape[] shapes; // 描画する図形の配列 (ポリモーフィズムを活用)
    private String currentShapeType = "Circle"; // 現在選択されている描画モード（デフォルトは円）
    private Color currentColor = Color.BLUE;    // 現在選択されている色（デフォルトは青）

    public DrawingPanel() {
        shapes = new Shape[0];
        setBackground(Color.WHITE);

        // マウスリスナーを追加してクリックされた位置に図形を追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Shape newShape = null;
                // currentShapeTypeに応じて適切な図形オブジェクトを生成
                // たとえば、円が選択されている場合の制御
                // TODO: ここに追加の実装する
                if ("Circle".equals(currentShapeType)) {
                    newShape = new Circle(e.getX(), e.getY(), 30, currentColor);
                }
                if ("Rectangle".equals(currentShapeType)) {
                    newShape = new Rectangle(e.getX(), e.getY(), 60, 40, currentColor);
                }
                if (newShape != null) {
                    addShape(newShape);
                    repaint(); // パネルを再描画
                }
            }
        });
    }

    public void addShape(Shape shape) {
        Shape[] newShapes = new Shape[this.shapes.length + 1];
        for (int i = 0; i < this.shapes.length; i++) {
            newShapes[i] = this.shapes[i];
        }
        newShapes[newShapes.length - 1] = shape;
        this.shapes = newShapes;
    }

    public void clearShapes() {
        this.shapes = new Shape[0];
        repaint(); // パネルを再描画
    }


    // 描画する図形の種類を設定するメソッド
    public void setCurrentShapeType(String shapeType) {
        this.currentShapeType = shapeType;
    }

    // 描画する図形の色を設定するメソッド (オプション)
    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    // JPanelのpaintComponentメソッドをオーバーライドして描画処理を実装
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // 親クラスの描画処理（背景のクリアなど）

        // shapes配列内のすべての図形を描画
        // shape変数にはCircleオブジェクトやRectangleオブジェクトが実際には入る
        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }
}

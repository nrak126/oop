import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

// 図形を描画するパネル
public class DrawingPanel extends JPanel {
    private Shape[] shapes; // 描画する図形の配列 (ポリモーフィズムを活用)
    private String currentShapeType = "Circle"; // 現在選択されている描画モード（デフォルトは円）
    private Color currentColor = Color.BLUE;    // 現在選択されている色（デフォルトは青）
    private int startX, startY, endX, endY;
    private boolean isDragging = false;

    public DrawingPanel() {
        shapes = new Shape[0];
        setBackground(Color.WHITE);

        // マウスリスナーを追加してクリックされた位置に図形を追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                isDragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                isDragging = false;
                // ここで図形をリストに追加
                addShapeFromDrag();
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                repaint();
            }
        });
    }

    private void addShapeFromDrag() {
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);
        // currentShapeTypeに応じて図形を生成
        if (currentShapeType.equals("Rectangle")) {
            addShape(new Rectangle(x, y, width, height, currentColor));
        } else if (currentShapeType.equals("Circle")) {
            int radius = (int) Math.hypot(width, height) / 2;
            int centerX = (startX + endX) / 2;
            int centerY = (startY + endY) / 2;
            addShape(new Circle(centerX, centerY, radius, currentColor));
        } else if (currentShapeType.equals("Triangle")) {
            // 三角形の座標計算は工夫が必要
            if(endX < startX) {
                height = -height; // 下向きにする
            }
            addShape(new Triangle((startX + endX) / 2, startY, width, height, currentColor));
        }
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

        // ドラッグ中はプレビュー描画
        if (isDragging) {
            // currentShapeTypeに応じて仮図形を描画
            int x = Math.min(startX, endX);
            int y = Math.min(startY, endY);
            int width = Math.abs(endX - startX);
            int height = Math.abs(endY - startY);
            g.setColor(currentColor);
            if (currentShapeType.equals("Rectangle")) {
                g.drawRect(x, y, width, height);
            } else if (currentShapeType.equals("Circle")) {
                int radius = (int) Math.hypot(width, height) / 2;
                int centerX = (startX + endX) / 2;
                int centerY = (startY + endY) / 2;
                g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            } else if (currentShapeType.equals("Triangle")) {
                int[] xPoints = {(startX + endX) / 2, startX, endX};
                int[] yPoints = {startY, endY, endY};
                g.drawPolygon(xPoints, yPoints, 3);
            }
        }
    }
}

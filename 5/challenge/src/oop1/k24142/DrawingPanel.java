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
    private Color currentColor = Color.BLUE; // 現在選択されている色（デフォルトは青）
    private int startX, startY, endX, endY;
    private boolean isDragging = false;

    private String currentDrawMode = "Draw"; // 現在選択されている描画モード（デフォルトは描画）
    private Shape selectedShape = null;
    private int offsetX, offsetY;

    private Shape[] redoStack = new Shape[0]; // Redo用スタック

    public DrawingPanel() {
        shapes = new Shape[0];
        setBackground(Color.WHITE);

        // マウスリスナーを追加してクリックされた位置に図形を追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(currentDrawMode.equals("Draw")) {
                    startX = e.getX();
                    startY = e.getY();
                    isDragging = true;
                }
                if(currentDrawMode.equals("Move")) {
                    for (Shape shape : shapes) {
                        if (shape.contains(e.getX(), e.getY())) {
                            selectedShape = shape;
                            offsetX = e.getX() - shape.getX();
                            offsetY = e.getY() - shape.getY();
                            isDragging = true;
                            break;
                        }
                    }
                }
                if(currentDrawMode.equals("Delete")) {
                    for (int i = 0; i < shapes.length; i++) {
                        if (shapes[i].contains(e.getX(), e.getY())) {
                            Shape[] newShapes = new Shape[shapes.length - 1];
                            System.arraycopy(shapes, 0, newShapes, 0, i);
                            System.arraycopy(shapes, i + 1, newShapes, i, shapes.length - i - 1);
                            shapes = newShapes;
                            break;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(currentDrawMode.equals("Draw")) {
                    endX = e.getX();
                    endY = e.getY();
                    isDragging = false;
                    // ここで図形をリストに追加
                    addShapeFromDrag();
                    repaint();
                }
                if(currentDrawMode.equals("Move")) {
                    selectedShape = null; // 選択解除
                }
                if(currentDrawMode.equals("Delete")) {
                    repaint(); // 削除後に再描画
                }
                // redoStackをクリア
                redoStack = new Shape[0];
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
            if (endX < startX) {
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

    public void setCurrentDrawMode(String mode) {
        this.currentDrawMode = mode;
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
            if(currentDrawMode.equals("Draw")) {
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
            if(currentDrawMode.equals("Move") && selectedShape != null) {
                // 選択された図形を移動
                selectedShape.setX(endX - offsetX);
                selectedShape.setY(endY - offsetY);
            }
        }
    }

    public void undo() {
        if (shapes.length > 0) {
            // 取り除く図形をredoStackに追加
            Shape removed = shapes[shapes.length - 1];
            Shape[] newRedoStack = new Shape[redoStack.length + 1];
            System.arraycopy(redoStack, 0, newRedoStack, 0, redoStack.length);
            newRedoStack[newRedoStack.length - 1] = removed;
            redoStack = newRedoStack;

            // shapesから最後の図形を削除
            Shape[] newShapes = new Shape[shapes.length - 1];
            System.arraycopy(shapes, 0, newShapes, 0, shapes.length - 1);
            shapes = newShapes;
            repaint();
        }
    }
    public void redo() {
        if (redoStack.length > 0) {
            Shape[] newShapes = new Shape[shapes.length + 1];
            System.arraycopy(shapes, 0, newShapes, 0, shapes.length);
            newShapes[newShapes.length - 1] = redoStack[redoStack.length - 1];
            shapes = newShapes;

            // redoStackから最後の図形を削除
            Shape[] newRedoStack = new Shape[redoStack.length - 1];
            System.arraycopy(redoStack, 0, newRedoStack, 0, redoStack.length - 1);
            redoStack = newRedoStack;

            repaint();
        }
    }
}


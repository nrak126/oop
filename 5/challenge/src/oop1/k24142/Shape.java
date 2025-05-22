import java.awt.Color;
import java.awt.Graphics;

public class Shape {
    protected int x;
    protected int y;
    protected Color color;

    public Shape(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void draw(Graphics g) {
        System.err.println("具体的な処理内容はサブクラスで実装してね！");
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean contains(int px, int py) {
    // サブクラスでオーバーライドして実装
    return false;
}
}


class Circle extends Shape {
    private int radius;

    public Circle(int x, int y, int radius, Color color) {
        super(x, y, color);
        this.radius = radius;
    }


    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    @Override
    public boolean contains(int px, int py) {
        int dx = px - x;
        int dy = py - y;
        return dx * dx + dy * dy <= radius * radius;
    }
}


class Rectangle extends Shape {
    private int width;
    private int height;

    public Rectangle(int x, int y, int width, int height, Color color) {
        super(x, y, color);
        this.width = width;
        this.height = height;
    }


    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    @Override
    public boolean contains(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}

class Triangle extends Shape {
    private int base;
    private int height;

    public Triangle(int x, int y, int base, int height, Color color) {
        super(x, y, color);
        this.base = base;
        this.height = height;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        int[] xs = { x, x - base / 2, x + base / 2 };
        int[] ys = { y, y + height, y + height };
        g.fillPolygon(xs, ys, 3);
    }

    @Override
    public boolean contains(int px, int py) {
        // 三角形の頂点座標を計算
        int[] xs = { x, x - base / 2, x + base / 2 };
        int[] ys = { y, y + height, y + height };
        java.awt.Polygon poly = new java.awt.Polygon(xs, ys, 3);
        return poly.contains(px, py);
    }
}


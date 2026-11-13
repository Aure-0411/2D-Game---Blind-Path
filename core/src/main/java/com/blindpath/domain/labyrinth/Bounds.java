package com.blindpath.domain.labyrinth;

public class Bounds {

    private float x;
    private float y;
    private float width;
    private float height;

    public Bounds(float x, float y, float width, float height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("La hit box est negative error ou la size est null");
        }
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getCenterX() { return x + width / 2f; }
    public float getCenterY() { return y + height / 2f; }

    public boolean intersects(Bounds other) {
        return this.x < other.x + other.width
            && this.x + this.width > other.x
            && this.y < other.y + other.height
            && this.y + this.height > other.y;
    }
}
package com.blindpath.domain.labyrinth;

public abstract class Obstacle implements Collidable {

    private float x;
    private float y;
    private float height;
    private float width;
    private boolean collide;

    protected Obstacle(float x, float y, float width, float height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("La taille de l'obstacle est negative error ou la size est null");
        }

        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.collide = true;

    }

    public void setCollide(boolean state) {
        this.collide = state;
    }

    @Override
    public Bounds getBounds() {
        Bounds bounds = new Bounds(this.x, this.y, this.width, this.height);
        return bounds;
    }

    @Override
    public boolean collide(Collidable other) {
        if (this.canCollide()
                && other.canCollide()
                && this.getBounds().intersects(other.getBounds())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canCollide() {
        return this.collide;
    }

}

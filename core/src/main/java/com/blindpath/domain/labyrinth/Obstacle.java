package com.blindpath.domain.labyrinth;

/** Obstacle rectangulaire du labyrinthe. Il bloque les collisions, sauf s'il est désactivé. */
public abstract class Obstacle implements Collidable {

    private float x;
    private float y;
    private float height;
    private float width;
    private boolean collide;

    /**
     * @param x position horizontale du coin bas gauche
     * @param y position verticale du coin bas gauche
     * @param width largeur, strictement positive
     * @param height hauteur, strictement positive
     * @throws IllegalArgumentException si la largeur ou la hauteur n'est pas positive
     */
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

    /**
     * Active ou désactive le blocage des collisions.
     *
     * @param state {@code true} pour bloquer
     */
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
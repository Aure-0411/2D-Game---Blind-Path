package com.blindpath.domain.labyrinth;

/** Rectangle aligné sur les axes (hitbox), avec l'origine en bas à gauche, en cases. */
public class Bounds {

    private float x;
    private float y;
    private float width;
    private float height;

    /**
     * @param x position horizontale du coin bas gauche
     * @param y position verticale du coin bas gauche
     * @param width largeur, strictement positive
     * @param height hauteur, strictement positive
     * @throws IllegalArgumentException si la largeur ou la hauteur n'est pas positive
     */
    public Bounds(float x, float y, float width, float height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("La hit box est negative error ou la size est null");
        }
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }
    
    /** @return la position horizontale du coin bas gauche */
    public float getX() { return x; }
    /** @return la position verticale du coin bas gauche */
    public float getY() { return y; }
    /** @return la largeur */
    public float getWidth() { return width; }
    /** @return la hauteur */
    public float getHeight() { return height; }
    /** @return la position horizontale du centre */
    public float getCenterX() { return x + width / 2f; }
    /** @return la position verticale du centre */
    public float getCenterY() { return y + height / 2f; }

    /**
     * Teste si deux rectangles se chevauchent. Deux rectangles qui se touchent
     * seulement par un bord ne se chevauchent pas.
     *
     * @param other l'autre rectangle
     * @return {@code true} s'ils se chevauchent
     */
    public boolean intersects(Bounds other) {
        return this.x < other.x + other.width
            && this.x + this.width > other.x
            && this.y < other.y + other.height
            && this.y + this.height > other.y;
    }
}
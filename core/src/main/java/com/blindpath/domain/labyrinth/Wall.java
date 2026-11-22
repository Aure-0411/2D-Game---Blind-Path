package com.blindpath.domain.labyrinth;

/** Mur du labyrinthe : il bloque le héros et le monstre. */
public class Wall extends Obstacle {

    private final boolean blocksMonster;

    /**
     * @param x position horizontale du coin bas gauche
     * @param y position verticale du coin bas gauche
     * @param width largeur
     * @param height hauteur
     */
    public Wall(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.blocksMonster = true;

    }

    /** @return {@code true} si le mur bloque aussi le monstre */
    public boolean blocksMonster() {
        return this.blocksMonster;
    }
}

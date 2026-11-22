package com.blindpath.domain.labyrinth;

import com.blindpath.domain.character.Character;

/** Zone de sortie de deux cases, horizontale ou verticale, placée dans le labyrinthe. */
public class Exit {

    private int height;
    private int width;
    private int x;
    private int y;
    private Bounds bounds;
    private OpeningOrientation orientation;

    /**
     * @param x colonne de la première case
     * @param y ligne de la première case
     * @param orientation sens de la sortie (horizontale = 2 x 1, verticale = 1 x 2)
     * @throws IllegalArgumentException si l'orientation est {@code null}
     */
    public Exit(int x, int y, OpeningOrientation orientation) {
        if (orientation == null ) {
            throw new IllegalArgumentException("L’orientation de la sortie doit être renseignée");
        }

        if (orientation == OpeningOrientation.HORIZONTAL){
            this.height = 1;
            this.width =2;
        }
        else {
            this.height = 2;
            this.width = 1;
        }
        this.x = x;
        this.y = y;
        this.orientation = orientation;

        this.bounds = new Bounds(this.x, this.y, this.width, this.height);

    }
    /** @return the X position of the exit. */
    public int getX (){
        return this.x;
    }
    /** @return the Y position of the exit. */
    public int getY (){
        return this.y;
    }
    /** @return the height of the exit. */
    public int getHeight(){
        return this.height;
    }
    /** @return the width of the exit. */
    public int getWidth(){
        return this.width;
    }
    /** @return the orientation of the exit. */
    public OpeningOrientation getOrientation(){
        return this.orientation;
    }

    /** @return the hitbox of the exit. */
        public Bounds getBounds() {
        return this.bounds;
    }

    /**
     * Teste si le personnage touche la sortie. Un contact bord à bord ne compte pas.
     *
     * @param character le personnage
     * @return {@code true} si sa hitbox chevauche la sortie, {@code false} s'il est {@code null}
     */
    public boolean isReached(Character character) {
        if (character == null) {
            return false;
        }
        return this.bounds.intersects(character.getBounds());
    }
}
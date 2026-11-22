package com.blindpath.domain.labyrinth;

/** Entrée du labyrinthe : une ouverture de deux cases où le héros apparaît au début du niveau. */
public class Entrance {

    private int x;
    private int y;
    private int height;
    private int width;

    private OpeningOrientation orientation;

    /**
     * @param x colonne de la première case
     * @param y ligne de la première case
     * @param orientation sens de l'ouverture (horizontale = 2 x 1, verticale = 1 x 2)
     * @throws IllegalArgumentException si l'orientation est {@code null}
     */
    public Entrance(int x, int y, OpeningOrientation orientation) {
        if (orientation == null ) {
            throw new IllegalArgumentException("L’orientation de l’entrée doit être renseignée");
        }

        if (orientation == OpeningOrientation.HORIZONTAL){
            this.height = 1;
            this.width =2;
        }
        else {
            this.height = 2;
            this.width = 1;
        }
        this.orientation = orientation;
        this.x = x;
        this.y = y;
    }

    /** @return la colonne de la première case */
    public int getX (){
        return this.x;
    }
        /** @return la ligne de la première case */
        public int getY (){
        return this.y;
    }
    /** @return la hauteur, en cases */
    public int getHeight(){
        return this.height;
    }
        /** @return la largeur, en cases */
        public int getWidth(){
        return this.width;
    }

    /** @return le sens de l'ouverture */
    public OpeningOrientation getOrientation(){
        return this.orientation;
    }
}

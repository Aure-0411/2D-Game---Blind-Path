package com.blindpath.domain.labyrinth;

import com.blindpath.domain.character.Gnome;

/** Zone de sortie de deux cases, horizontale ou verticale, placée dans le labyrinthe. */
public class Exit {

    private int height;
    private int width;
    private int x;
    private int y;
    private Bounds bounds;
    private OpeningOrientation orientation;

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

    /**
    * Checks whether a collision is detected between the exit's and the gnome's hitboxes.
    * In case of border to border, there is no contact.
    *
    * @param gnome can collide with other hitboxes, including the exit's.
    * @return answers "Is there a collision?" with true or false.
    */
    public boolean isReached(Gnome gnome){
        if (this.bounds.intersects(gnome.getBounds())){
            return true;
        }
    return false;
    }

}

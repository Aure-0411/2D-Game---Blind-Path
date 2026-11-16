package com.blindpath.domain.labyrinth;

import com.blindpath.domain.character.Gnome;

/**
* In the game, an exit is a zone on an edge of the labyrinth and the gnome's goal.
* Its size depends on its border: it spans two tiles along that edge
* (2 * 1 on TOP or BOTTOM, 1 * 2 on LEFT or RIGHT).
* It has a hitbox and answers the question "Am I touched?". If yes, the Level class sends the gnome to the next level.
*/
public class Exit {

    private int height;
    private int width;
    private int x;
    private int y;
    private Bounds bounds;
    private Border border;

    /**
     * Creates an exit on the labyrinth.
     *
     * @param x defines the horizontal position of the exit.
     * @param y defines the vertical position of the exit.
     * @param border is the edge on which the exit is placed.
     * @throws IllegalArgumentException is thrown if border equals null, meaning an exit can't be created.
     */
    public Exit(int x, int y, Border border) {
        if (border == null ) {
            throw new IllegalArgumentException("Le bord de la sortie doit être renseigné");
        }

        if (border== Border.TOP || border==Border.BOTTOM){
            this.height = 1;
            this.width =2;
        }
        else {
            this.height = 2;
            this.width = 1;
        }
        this.x = x;
        this.y = y;
        this.border = border;

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
    /** @return the border of the exit. */
    public Border getBorder(){
        return this.border;
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

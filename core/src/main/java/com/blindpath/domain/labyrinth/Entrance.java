package com.blindpath.domain.labyrinth;

public class Entrance {

    private int x;
    private int y;
    private int height;
    private int width;

    private OpeningOrientation orientation;

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

    public int getX (){
        return this.x;
    }
        public int getY (){
        return this.y;
    }
    public int getHeight(){
        return this.height;
    }
        public int getWidth(){
        return this.width;
    }

    public OpeningOrientation getOrientation(){
        return this.orientation;
    }
}

package com.blindpath.domain.labyrinth;

public class Entrance {

    private int x;
    private int y;
    private int height;
    private int width;

    private Border border;

    public Entrance(int x, int y, Border border) {
        if (border == null ) {
            throw new IllegalArgumentException("Le bord de l’entrée doit être renseigné");
        }

        if (border== Border.TOP || border==Border.BOTTOM){
            this.height = 1;
            this.width =2;
        }
        else {
            this.height = 2;
            this.width = 1;
        }
        this.border = border;
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

    public Border getBorder(){
        return this.border;
    }
}

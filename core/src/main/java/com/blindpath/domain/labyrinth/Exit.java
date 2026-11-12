package com.blindpath.domain.labyrinth;

public class Exit {

    private int height;
    private int width;
    private int x;
    private int y;
    private Bounds bounds;
    private Border border;


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

    // public boolean isReached(Gnome gnome){
    //     if (this.bounds.intersects(gnome.getBounds())){
    //         return true;
    //     }
    // return false;
    // }
    
}

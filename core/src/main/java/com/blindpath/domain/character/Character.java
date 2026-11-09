package com.blindpath.domain.character;

// import com.blindpath.physics.Movable;
// import com.blindpath.physics.Collidable;
// import com.blindpath.physics.Bounds;

public abstract class Character implements Movable/*, Collidable*/{
    private int life;
    private int maxLife;
    private float speed;
    private float x, y;
    private float width, height;

    public Character (float speed, float x, float y, float width, float height){
        this.maxLife = 6;
        this.life = maxLife;
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getMaxLife() { return maxLife; }
    public int getLife() { return life; }
    public void setLife(int life) {this.life = life; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) {this.speed = speed; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void move(float directionX, float directionY, float deltaTime) {
        x += directionX * speed * deltaTime;
        y += directionY * speed * deltaTime;
    }
    // @Override 
    // public Bounds getBounds() { 
    //     return new Bounds(x, y, width, height); 
    // }
}
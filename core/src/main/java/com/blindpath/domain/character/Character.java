package com.blindpath.domain.character;

import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Collidable;
import com.blindpath.domain.inventory.Inventory;

public abstract class Character implements Movable, Collidable{
    public static final int MONSTER_DAMAGE = 2;
    public static final float IMMUNITY_DURATION = 2f;
    private final Inventory<Character> inventory;
    
    private int life;
    private int maxLife;
    private float speed;
    private float x, y;
    private float width, height;
    private Direction facing = Direction.DOWN;
    private boolean invincible;
    private float immunityTimer = 0f;

    public Character (float speed, float x, float y, float width, float height){
        this.maxLife = 6;
        this.life = maxLife;
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.inventory = new Inventory<>(this);
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
    public Inventory<Character> getInventory() { return inventory; }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Direction getFacing() { return facing; }

    public void setFacing(Direction facing) {
        if (facing != Direction.NONE) {
            this.facing = facing;
        }
    }

    /** Déplacement "brut", sans collision. Pour avancer en respectant les murs : Level.tryMove. */
    @Override
    public void move(float directionX, float directionY, float deltaTime) {
        x += directionX * speed * deltaTime;
        y += directionY * speed * deltaTime;
    }

    // ---- Collidable ----
    @Override
    public boolean canCollide() { return true; }

    @Override
    public boolean collide(Collidable other) {
        return canCollide() && other.canCollide() && getBounds().intersects(other.getBounds());
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(x, y, width, height);
    }

    // ---- Gestion de vie ----
    public void takeMonsterHit() {
        if (invincible || isImmune()) {
            return;
        }
        setLife(Math.max(0, getLife() - MONSTER_DAMAGE));
        immunityTimer = IMMUNITY_DURATION;
    }

    public boolean isImmune() {
        return immunityTimer > 0;
    }

    public void updateImmunity(float deltaTime) {
        immunityTimer = Math.max(0f, immunityTimer - deltaTime);
    }

    public void toggleInvincible() {
        invincible = !invincible;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public boolean isDead() {
        return getLife() == 0;
    }

    public void resetForRetry() {
        life = maxLife;
        immunityTimer = 0f;
        // TODO: réinitialiser ici les effets temporaires actifs (ex: compas) lors de leur implémentation
        // inventory est conservé
    }
}
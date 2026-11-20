package com.blindpath.domain.object;

import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Collidable;
import com.blindpath.domain.monster.Monster;

public abstract class PlacedObject implements Collidable {
    private float x, y;
    private float width, height;

    public PlacedObject(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

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

    public abstract void onTouch(Monster monster);

    public void update(float deltaTime) {
        // Par défaut, un objet posé ne fait rien au fil du temps. Les objets qui ont besoin du temps redéfinissent cette méthode.
    }
}
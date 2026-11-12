package com.blindpath.domain.labyrinth;

public interface Collidable {
    boolean canCollide();

    boolean collide(Collidable other);

    Bounds getBounds();
}

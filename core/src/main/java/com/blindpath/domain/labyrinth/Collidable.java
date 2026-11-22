package com.blindpath.domain.labyrinth;

/** Tout ce qui a une hitbox et peut entrer en collision. */
public interface Collidable {
    /** @return {@code true} si l'objet bloque actuellement les collisions */
    boolean canCollide();

    /**
     * @param other l'autre objet
     * @return {@code true} si les deux objets peuvent entrer en collision et que leurs hitbox se chevauchent
     */
    boolean collide(Collidable other);

    /** @return la hitbox de l'objet */
    Bounds getBounds();
}

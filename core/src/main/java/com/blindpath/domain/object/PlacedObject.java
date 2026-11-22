package com.blindpath.domain.object;

import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Collidable;
import com.blindpath.domain.monster.Monster;

/**
 * Objet posé au sol par le joueur. Le niveau le met à jour à chaque image,
 * prévient l'objet quand le monstre le touche, et le retire quand il a fini.
 */
public abstract class PlacedObject implements Collidable {
    private float x, y;
    private float width, height;

    /**
     * @param x position horizontale
     * @param y position verticale
     * @param width largeur
     * @param height hauteur
     */
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

    /**
     * Appelé quand le monstre touche l'objet.
     *
     * @param monster le monstre
     */
    public abstract void onTouch(Monster monster);

    /** @return {@code true} quand l'objet a fini son effet et peut être retiré */
    public abstract boolean isDone();

    /**
     * Fait évoluer l'objet dans le temps. Par défaut, rien.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    public void update(float deltaTime) {
        // Par défaut, un objet posé ne fait rien au fil du temps. Les objets qui ont besoin du temps redéfinissent cette méthode.
    }
}
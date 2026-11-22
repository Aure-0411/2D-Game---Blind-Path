package com.blindpath.domain.labyrinth;

import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;

/**
 * Levier à usage unique : un clap à une case de son centre actionne son mur.
 */
public final class Lever {

    /** Distance maximale entre le claquement et le centre du levier, en cases. */
    public static final float SNAP_RANGE = 2f;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final RotatingWall wall;

    /**
     * @param x position horizontale du coin bas gauche
     * @param y position verticale du coin bas gauche
     * @param width largeur de la zone du levier
     * @param height hauteur de la zone du levier
     * @param wall le mur que le levier fait pivoter
     * @throws IllegalArgumentException si la zone n'est pas valide ou si le mur est {@code null}
     */
    public Lever(float x, float y, float width, float height, RotatingWall wall) {
        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(width) || !Float.isFinite(height)
                || width <= 0 || height <= 0 || wall == null) {
            throw new IllegalArgumentException("La zone du levier et son mur doivent être renseignés.");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.wall = wall;
    }

    /** @return le mur que le levier fait pivoter */
    public RotatingWall getWall() {
        return wall;
    }

    /** @return {@code true} si le levier a déjà été actionné */
    public boolean isActivated() {
        return wall.isRotated();

    }

    /** @return la zone du levier */
    public Bounds getBounds() {
        return new Bounds(x, y, width, height);
    }

    /**
     * Actionne le levier si le héros claque des doigts assez près de son centre.
     * Le levier ne sert qu'une fois.
     *
     * @param event le claquement
     * @param occupants les hitbox présentes (héros, monstre) : le mur ne redevient solide
     *        qu'une fois qu'elles ont quitté sa nouvelle position
     * @return {@code true} si le mur a pivoté
     */
    public boolean onSnap(SnapEvent event, Bounds... occupants) {
        if (event == null || event.getSource() != SnapSource.HERO || isActivated()) {
            return false;
        }
        float dx = event.getSourceX() - (x + width / 2f);
        float dy = event.getSourceY() - (y + height / 2f);
        if (dx * dx + dy * dy > SNAP_RANGE * SNAP_RANGE) {
            return false;
        }
        return wall.rotate(occupants);
    }
}

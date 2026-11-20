package com.blindpath.domain.labyrinth;

import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;

/**
 * Levier à usage unique : un clap à une case de son centre actionne son mur.
 */
public final class Lever {

    public static final float SNAP_RANGE = 2f;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final RotatingWall wall;

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

    public RotatingWall getWall() {
        return wall;
    }

    public boolean isActivated() {
        return wall.isRotated();

    }

    public Bounds getBounds() {
        return new Bounds(x, y, width, height);
    }

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

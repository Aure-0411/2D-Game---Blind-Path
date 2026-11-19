package com.blindpath.domain.chest;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.Collidable;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapListener;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Bounds;

public class Chest implements SnapListener, Collidable {
    
    public static final float SHORT_SIDE = 1f;
    public static final float LONG_SIDE = 2f;
    private static final float DEFAULT_ACTIVATION_RADIUS = 1.5f;

    private final float x, y;
    private final float width, height;
    private final Border orientation;
    private final float activationRadius;
    private final LootResult loot;
    private boolean opened;

    /** Constructeur principal : impose 2x1 (TOP/BOTTOM) ou 1x2 (LEFT/RIGHT). */
    public Chest(float x, float y, Border orientation, LootResult loot) {
        if (orientation == null) {
            throw new IllegalArgumentException("L'orientation du coffre doit être renseignée.");
        }
        if (loot == null) {
            throw new IllegalArgumentException("Le loot du coffre doit être renseigné.");
        }
        this.x = x;
        this.y = y;
        this.orientation = orientation;

        boolean horizontal = (orientation == Border.TOP || orientation == Border.BOTTOM);
        this.width = horizontal ? LONG_SIDE : SHORT_SIDE;
        this.height = horizontal ? SHORT_SIDE : LONG_SIDE;
        this.activationRadius = DEFAULT_ACTIVATION_RADIUS;
        this.loot = loot;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Border getOrientation() { return orientation; }

    public boolean open(Gnome gnome) {
        if (opened) {
            return false;
        }
        gnome.getInventory().add(loot.item(), loot.quantity());
        opened = true;
        return true;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isInRange(float px, float py) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float dx = px - centerX;
        float dy = py - centerY;
        return dx * dx + dy * dy <= activationRadius * activationRadius;
    }

    @Override
    public void onSnap(SnapEvent event) {
        // inchangé
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
}
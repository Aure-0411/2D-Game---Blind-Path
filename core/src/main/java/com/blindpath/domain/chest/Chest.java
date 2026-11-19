package com.blindpath.domain.chest;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Collidable;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapListener;

public class Chest implements SnapListener, Collidable {

    public static final float SHORT_SIDE = 1f;
    public static final float LONG_SIDE = 2f;
    public static final float DEFAULT_ACTIVATION_MARGIN = 1f;

    private final float x, y;
    private final float width, height;
    private final Border orientation;
    private final float activationRadiusX, activationRadiusY;
    private final LootResult loot;
    private boolean opened;
    private Character opener;

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

        this.activationRadiusX = (this.width / 2f) + DEFAULT_ACTIVATION_MARGIN;
        this.activationRadiusY = (this.height / 2f) + DEFAULT_ACTIVATION_MARGIN;
        this.loot = loot;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Border getOrientation() { return orientation; }
    public float getActivationRadiusX() { return activationRadiusX; }
    public float getActivationRadiusY() { return activationRadiusY; }

    public void setOpener(Character character) {
        this.opener = character;
    }

    public boolean open(Character character) {
        if (opened || character == null) {
            return false;
        }
        character.getInventory().add(loot.item(), loot.quantity());
        opened = true;
        return true;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isInRange(float px, float py) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float normX = (px - centerX) / activationRadiusX;
        float normY = (py - centerY) / activationRadiusY;
        return normX * normX + normY * normY <= 1f;
    }

    @Override
    public void onSnap(SnapEvent event) {
        if (event == null || opened || opener == null) {
            return;
        }
        if (isInRange(event.getSourceX(), event.getSourceY())) {
            open(opener);
        }
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
}
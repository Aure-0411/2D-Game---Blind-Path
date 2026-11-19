package com.blindpath.domain.character;

import com.blindpath.domain.inventory.Inventory;

public class Gnome extends Character {

    public static final float HITBOX_SIZE = 1f;
    public static final int MONSTER_DAMAGE = 2;
    public static final float IMMUNITY_DURATION = 2f;
    private final Inventory<Gnome> inventory;

    private float immunityTimer = 0f;
    private boolean invincible;

    public Gnome() {
        super(2.75f, 0, 0, HITBOX_SIZE, HITBOX_SIZE);
        this.inventory = new Inventory<>(this);
    }

    public void takeMonsterHit() {

        if (invincible) {
            return;
        }

        if (isImmune()) {
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

    public boolean isDead() {
        return getLife() == 0;
    }

    public Inventory<Gnome> getInventory() {
        return this.inventory;
    }

    public void toggleInvincible() {
        invincible = !invincible;
    }
}

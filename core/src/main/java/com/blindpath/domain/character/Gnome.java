package com.blindpath.domain.character;

public class Gnome extends Character {
    public static final float HITBOX_SIZE = 1f;

    private boolean invincible;

    public Gnome() {
        super(2.75f, 0, 0, HITBOX_SIZE, HITBOX_SIZE);
    }

    @Override
    public void takeMonsterHit() {
        if (invincible) {
            return;
        }
        super.takeMonsterHit();
    }

    public void toggleInvincible() {
        invincible = !invincible;
    }
}
package com.blindpath.domain.character;

public class Gnome extends Character{
    public static final float HITBOX_SIZE = 1f;
    public static final int MONSTER_DAMAGE = 2;
    public static final float IMMUNITY_DURATION = 2f;

    private float immunityTimer = 0f;

    public Gnome() {
        super(2.75f, 0, 0, HITBOX_SIZE, HITBOX_SIZE);
    }

    public void takeMonsterHit() {
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
}
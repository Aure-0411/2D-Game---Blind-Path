package com.blindpath.domain.character;

public class Gnome extends Character{
    public static final float HITBOX_SIZE = 1f;
    public static final int MONSTER_DAMAGE = 2;
    public static final float IMMUNITY_DURATION = 2f;

    private float immunityTimer = 0f;
    private boolean invincible;

    public Gnome() {
        super(2.75f, 0, 0, HITBOX_SIZE, HITBOX_SIZE);
    }

    @Override
    public void takeDamage(int amount) {

        if (invincible) {
            return;
        }


        if (isImmune()) {
            return;
        }
        setLife(Math.max(0, getLife() - amount));
        immunityTimer = IMMUNITY_DURATION;
    }

    public void takeMonsterHit() {
        takeDamage(MONSTER_DAMAGE);
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

    public void toggleInvincible() {
        invincible = !invincible;
    }
}
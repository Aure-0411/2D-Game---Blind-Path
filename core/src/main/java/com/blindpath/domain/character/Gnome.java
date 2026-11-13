package com.blindpath.domain.character;

public class Gnome extends Character{
    public static final float HITBOX_SIZE = 1f;
    public static final int MONSTER_DAMAGE = 2;

    public Gnome() {
        super(2.75f, 0, 0, HITBOX_SIZE, HITBOX_SIZE);
    }

    public void takeMonsterHit() {
        setLife(Math.max(0, getLife() - MONSTER_DAMAGE));
    }
}
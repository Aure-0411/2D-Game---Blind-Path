package com.blindpath.domain.object;

import com.blindpath.domain.monster.Monster;

public class PlacedBearTrap extends PlacedObject {
    public static final float TRAP_SIZE = 1f;
    public static final float IMMOBILITY_DURATION = 3f;

    public PlacedBearTrap(float x, float y) {
        super(x, y, TRAP_SIZE, TRAP_SIZE);
    }

    @Override
    public void onTouch(Monster monster) {
        monster.immobilize(IMMOBILITY_DURATION);
    }
}
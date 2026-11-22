package com.blindpath.domain.object;

import com.blindpath.domain.monster.Monster;

/**
 * Piège à ours posé au sol : le premier monstre qui marche dessus est immobilisé
 * {@link #IMMOBILITY_DURATION} secondes, puis le piège disparaît.
 */
public class PlacedBearTrap extends PlacedObject {
    /** Taille du piège, en cases. */
    public static final float TRAP_SIZE = 1f;
    /** Durée de l'immobilisation, en secondes. */
    public static final float IMMOBILITY_DURATION = 3f;

    private boolean hasTrapped = false;

    /**
     * @param x position horizontale
     * @param y position verticale
     */
    public PlacedBearTrap(float x, float y) {
        super(x, y, TRAP_SIZE, TRAP_SIZE);
    }

    /**
     * Immobilise le monstre et marque le piège comme utilisé.
     *
     * @param monster le monstre qui a marché sur le piège
     */
    @Override
    public void onTouch(Monster monster) {
        monster.immobilize(IMMOBILITY_DURATION);
        hasTrapped = true;
    }

    /** @return {@code true} une fois que le piège a servi */
    @Override
    public boolean isDone() {
        return hasTrapped;
    }
}
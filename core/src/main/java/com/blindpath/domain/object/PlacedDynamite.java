package com.blindpath.domain.object;

import com.blindpath.domain.game.Level;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.monster.Monster;

/**
 * Dynamite posée au sol : elle explose après {@link #COUNTDOWN_DURATION} secondes.
 * L'explosion est diffusée comme un claquement, ce qui attire le monstre et déclenche
 * ce qui réagit au bruit.
 */
public class PlacedDynamite extends PlacedObject {
    /** Taille de la dynamite, en cases. */
    public static final float DYNAMITE_SIZE = 1f;
    /** Délai avant l'explosion, en secondes. */
    public static final float COUNTDOWN_DURATION = 3f;
    private final Level level;

    private float countdownTimer = COUNTDOWN_DURATION;
    private boolean hasExploded = false;

    /**
     * @param x position horizontale
     * @param y position verticale
     * @param level le niveau dans lequel l'explosion sera diffusée
     */
    public PlacedDynamite(float x, float y, Level level) {
        super(x, y, DYNAMITE_SIZE, DYNAMITE_SIZE);
        this.level = level;
    }

    /**
     * Le contact d'un monstre n'a aucun effet : seule l'explosion compte.
     *
     * @param monster le monstre
     */
    @Override
    public void onTouch(Monster monster) {
        // Le contact d'un monstre avec la dynamite n'a aucun effet : seule l'explosion compte.
    }

    /**
     * Fait avancer le compte à rebours et déclenche l'explosion à 0.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    @Override
    public void update(float deltaTime) {
        countdownTimer = Math.max(0f, countdownTimer - deltaTime);
        if (!hasExploded && countdownTimer <= 0f) {
            hasExploded = true;
            level.emitSnap(new SnapEvent(this.getBounds().getCenterX(), this.getBounds().getCenterY(), SnapSource.DYNAMITE, 0L));
        }
    }

    /** @return {@code true} une fois que la dynamite a explosé */
    @Override
    public boolean isDone() {
        return hasExploded;
    }
}
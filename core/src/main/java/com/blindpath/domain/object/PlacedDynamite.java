package com.blindpath.domain.object;

import com.blindpath.domain.game.Level;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.monster.Monster;

public class PlacedDynamite extends PlacedObject {
    public static final float DYNAMITE_SIZE = 1f;
    public static final float COUNTDOWN_DURATION = 3f;
    private final Level level;

    private float countdownTimer = COUNTDOWN_DURATION;
    private boolean hasExploded = false;

    public PlacedDynamite(float x, float y, Level level) {
        super(x, y, DYNAMITE_SIZE, DYNAMITE_SIZE);
        this.level = level;
    }

    @Override
    public void onTouch(Monster monster) {
        // Le contact d'un monstre avec la dynamite n'a aucun effet : seule l'explosion compte.
    }

    @Override
    public void update(float deltaTime) {
        countdownTimer = Math.max(0f, countdownTimer - deltaTime);
        if (!hasExploded && countdownTimer <= 0f) {
            hasExploded = true;
            level.emitSnap(new SnapEvent(this.getBounds().getCenterX(), this.getBounds().getCenterY(), SnapSource.DYNAMITE, 0L));
        }
    }
}
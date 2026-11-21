package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

public class Compass extends Item {

    public static final float DEFAULT_DURATION = 5f;

    private final float duration;

    public Compass() {
        this(DEFAULT_DURATION);
    }

    public Compass(float duration) {
        super("Compass");
        if (duration <= 0f) {
            throw new IllegalArgumentException("La durée de la boussole doit être positive.");
        }
        this.duration = duration;
    }

    public float getDuration() {
        return duration;
    }

    @Override
    public void use(Character user, Level<?> level) {
        if (!canUse(user, level)) {
            throw new IllegalArgumentException("Le joueur et le niveau doivent être renseignés.");
        }
        user.activateCompass(duration);
    }
}
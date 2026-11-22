package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

/** Boussole : indique la direction de la sortie pendant quelques secondes. */
public class Compass extends Item {

    /** Durée par défaut de la boussole, en secondes. */
    public static final float DEFAULT_DURATION = 5f;

    private final float duration;

    /** Crée une boussole de durée {@link #DEFAULT_DURATION}. */
    public Compass() {
        this(DEFAULT_DURATION);
    }

    /**
     * Crée une boussole de durée choisie.
     *
     * @param duration durée de l'effet, en secondes
     * @throws IllegalArgumentException si la durée n'est pas positive
     */
    public Compass(float duration) {
        super("Compass");
        if (duration <= 0f) {
            throw new IllegalArgumentException("La durée de la boussole doit être positive.");
        }
        this.duration = duration;
    }

    /** @return la durée de l'effet, en secondes */
    public float getDuration() {
        return duration;
    }

    /**
     * Active la boussole du joueur.
     *
     * @param user le joueur
     * @param level le niveau en cours
     * @throws IllegalArgumentException si le joueur ou le niveau est {@code null}
     */
    @Override
    public void use(Character user, Level<?> level) {
        if (!canUse(user, level)) {
            throw new IllegalArgumentException("Le joueur et le niveau doivent être renseignés.");
        }
        user.activateCompass(duration);
    }
}
package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Exit;

/** Effet temporaire de la boussole : un compte à rebours et le calcul de la direction de la sortie. */
public class CompassEffect {

    private float remainingTime;

    /**
     * Démarre (ou relance) l'effet.
     *
     * @param durationSeconds durée de l'effet, en secondes
     * @throws IllegalArgumentException si la durée n'est pas positive
     */
    public void activate(float durationSeconds) {
        if (durationSeconds <= 0f) {
            throw new IllegalArgumentException("La durée de la boussole doit être positive.");
        }
        this.remainingTime = durationSeconds;
    }

    /**
     * Fait avancer le compte à rebours.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    public void update(float deltaTime) {
        if (deltaTime > 0f && remainingTime > 0f) {
            remainingTime = Math.max(0f, remainingTime - deltaTime);
        }
    }

    /** @return {@code true} tant que l'effet est actif */
    public boolean isActive() {
        return remainingTime > 0f;
    }

    /** @return le temps restant, en secondes */
    public float getRemainingTime() {
        return remainingTime;
    }

    /** Arrête l'effet immédiatement. */
    public void reset() {
        remainingTime = 0f;
    }

    /**
     * Calcule la direction de la sortie vue depuis le personnage.
     *
     * @param user le personnage
     * @param exit la sortie du niveau
     * @return l'angle en degrés entre le centre du personnage et le centre de la sortie
     *         (0° = droite, 90° = haut)
     * @throws IllegalArgumentException si le personnage ou la sortie est {@code null}
     */
    public float computeAngleDegrees(Character user, Exit exit) {
        if (user == null || exit == null) {
            throw new IllegalArgumentException("Le personnage et la sortie doivent être renseignés.");
        }
        float userCenterX = user.getX() + user.getWidth() / 2f;
        float userCenterY = user.getY() + user.getHeight() / 2f;
        float exitCenterX = exit.getX() + exit.getWidth() / 2f;
        float exitCenterY = exit.getY() + exit.getHeight() / 2f;

        return (float) Math.toDegrees(Math.atan2(exitCenterY - userCenterY, exitCenterX - userCenterX));
    }
}
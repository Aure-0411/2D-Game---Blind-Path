package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Exit;

public class CompassEffect {

    private float remainingTime;

    public void activate(float durationSeconds) {
        if (durationSeconds <= 0f) {
            throw new IllegalArgumentException("La durée de la boussole doit être positive.");
        }
        this.remainingTime = durationSeconds;
    }

    public void update(float deltaTime) {
        if (deltaTime > 0f && remainingTime > 0f) {
            remainingTime = Math.max(0f, remainingTime - deltaTime);
        }
    }

    public boolean isActive() {
        return remainingTime > 0f;
    }

    public float getRemainingTime() {
        return remainingTime;
    }

    public void reset() {
        remainingTime = 0f;
    }

    /** Angle en degrés (0° = droite, 90° = haut) entre le centre du personnage et le centre de la sortie. */
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
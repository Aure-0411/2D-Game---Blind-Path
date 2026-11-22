package com.blindpath.domain.time;

/** Minuteur à rebours, compté en microsecondes pour éviter les erreurs d'arrondi des float. */
public class GameTimer {

    private long durationMicros;
    private long remainingMicros;

    /**
     * Démarre (ou relance) le minuteur.
     *
     * @param duration durée, en secondes
     * @throws IllegalArgumentException si la durée est négative
     */
    public void start(float duration) {
        durationMicros = TimeUnits.toMicros(duration);
        remainingMicros = durationMicros;
    }

    /** Arrête le minuteur immédiatement. */
    public void stop() {
        remainingMicros = 0;
    }

    /**
     * Fait avancer le minuteur.
     *
     * @param deltaTime temps écoulé, en secondes
     * @return {@code true} seulement à l'instant où le minuteur arrive à 0
     */
    public boolean update(float deltaTime) {
        long delta = TimeUnits.toMicros(deltaTime);
        if (remainingMicros == 0) {
            return false;
        }
        remainingMicros = Math.max(0, remainingMicros - delta);
        if (remainingMicros == 0) {
            return true;
        }
        return false;
    }

    /** @return {@code true} tant que le minuteur n'est pas arrivé à 0 */
    public boolean isRunning() {
        return remainingMicros > 0;
    }

    /** @return le temps restant, en secondes */
    public float getRemaining() {
        return TimeUnits.toSeconds(remainingMicros);
    }

    /** @return l'avancement, de 0 (début) à 1 (fini ou arrêté) */
    public float getProgress() {
        if (remainingMicros == 0) {
            return 1f;
        }
        return 1f - ((float) remainingMicros / (float) durationMicros);
    }
}
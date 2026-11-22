package com.blindpath.domain.time;

/** Ce qui reçoit les ticks de l'horloge du jeu. */
public interface TickListener {
    /**
     * Appelé à chaque tick.
     *
     * @param tick le numéro du tick, à partir de 1
     */
    void onTick(long tick);
}
package com.blindpath.domain.time;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Horloge du jeu : elle découpe le temps réel en ticks réguliers ({@link #TICKS_PER_SECOND} par seconde)
 * et prévient ses abonnés à chaque tick (patron Observer). Les monstres s'en servent pour se
 * déplacer à un rythme fixe, quel que soit le nombre d'images par seconde.
 */
public class GameClock {

    /** Nombre de ticks par seconde. */
    public static final int TICKS_PER_SECOND = 30;
    /** Durée d'un tick, en secondes. */
    public static final float TICK_DURATION = 1f / TICKS_PER_SECOND;
    /** Temps maximal pris en compte en une image, en secondes : évite une avalanche de ticks après un blocage. */
    public static final float MAX_DELTA_TIME = 0.25f;
    private final List<TickListener> listeners = new CopyOnWriteArrayList<>();
    private long tickCount;
    private long accumulatedMicroTicks;

    /**
     * Fait avancer l'horloge et émet les ticks correspondants. Le reste de temps est gardé pour la suite.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes (plafonné à {@link #MAX_DELTA_TIME})
     * @return le nombre de ticks émis
     * @throws IllegalArgumentException si le temps est négatif
     */
    public int update(float deltaTime) {         

        accumulatedMicroTicks += TimeUnits.toMicros(Math.min(deltaTime, MAX_DELTA_TIME)) * TICKS_PER_SECOND;
        int emitted = 0;

        while (accumulatedMicroTicks >= TimeUnits.MICROS_PER_SECOND) {
            accumulatedMicroTicks -= TimeUnits.MICROS_PER_SECOND;
            tickCount++;
            emitted++;

            for (TickListener listener : listeners) {
                listener.onTick(tickCount);
            }
        }

        return emitted;

    }

    /**
     * Abonne un objet aux ticks. Un même abonné n'est ajouté qu'une fois.
     *
     * @param listener l'abonné
     * @throws IllegalArgumentException si l'abonné est {@code null}
     */
    public void addListener(TickListener listener) {

        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }

    }

    /** @param listener l'abonné à retirer */
    public void removeListener(TickListener listener) {
        listeners.remove(listener);
    }

    /** @return le nombre de ticks émis depuis la création de l'horloge */
    public long getTickCount() {
        return tickCount;
    }

    /**
     * @param seconds une durée, en secondes
     * @return le nombre de ticks entiers correspondant
     */
    public static int secondsToTicks(float seconds) {
        return (int) (TimeUnits.toMicros(seconds) * TICKS_PER_SECOND / TimeUnits.MICROS_PER_SECOND);
    }

}
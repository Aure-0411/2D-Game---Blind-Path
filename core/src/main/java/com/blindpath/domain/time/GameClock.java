package com.blindpath.domain.time;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameClock {

    public static final int TICKS_PER_SECOND = 30;
    public static final float TICK_DURATION = 1f / TICKS_PER_SECOND;
    public static final float MAX_DELTA_TIME = 0.25f;
    private final List<TickListener> listeners = new CopyOnWriteArrayList<>();
    private long tickCount;
    private long accumulatedMicroTicks;

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

    public void addListener(TickListener listener) {

        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }

    }

    public void removeListener(TickListener listener) {
        listeners.remove(listener);
    }

    public long getTickCount() {
        return tickCount;
    }

    public static int secondsToTicks(float seconds) {
        return (int) (TimeUnits.toMicros(seconds) * TICKS_PER_SECOND / TimeUnits.MICROS_PER_SECOND);
    }

}
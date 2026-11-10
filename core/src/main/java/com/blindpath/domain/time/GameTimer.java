package com.blindpath.domain.time;

public class GameTimer {

    private long durationMicros;
    private long remainingMicros;

    public void start(float duration) {
        durationMicros = TimeUnits.toMicros(duration);
        remainingMicros = durationMicros;
    }

    public void stop() {
        remainingMicros = 0;
    }

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

    public boolean isRunning() {
        return remainingMicros > 0;
    }

    public float getRemaining() {
        return TimeUnits.toSeconds(remainingMicros);
    }

    public float getProgress() {
        if (remainingMicros == 0) {
            return 1f;
        }
        return 1f - ((float) remainingMicros / (float) durationMicros);
    }
}
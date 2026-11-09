package com.blindpath.domain.time;

final class TimeUnits {

    static final long MICROS_PER_SECOND = 1000000L;

    private TimeUnits() {
    }

    static long toMicros(float seconds) {

        if(!(seconds >= 0)) {
            throw new IllegalArgumentException("time must be >= 0: " + seconds);
        }
        return Math.round((double) seconds * MICROS_PER_SECOND);

    }

    static float toSeconds(long micros) {
        return (float) micros / MICROS_PER_SECOND;
    }
}
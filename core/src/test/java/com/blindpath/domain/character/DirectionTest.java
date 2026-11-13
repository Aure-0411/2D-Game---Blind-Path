package com.blindpath.domain.character;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DirectionTest {

    @Test
    void fromKeys_singleKey() {
        assertEquals(Direction.UP, Direction.fromKeys(true, false, false, false));
        assertEquals(Direction.RIGHT,  Direction.fromKeys(false, false, false, true));
    }

    @Test
    void fromKeys_diagonal() {
        assertEquals(Direction.UP_RIGHT, Direction.fromKeys(true, false, false, true));
        assertEquals(Direction.DOWN_LEFT, Direction.fromKeys(false, true, true, false));
    }

    @Test
    void fromKeys_oppositeKeysCancel() {
        assertEquals(Direction.NONE, Direction.fromKeys(false, false, true, true));
        assertEquals(Direction.NONE, Direction.fromKeys(true, true, false, false));
        assertEquals(Direction.NONE, Direction.fromKeys(true, true, true, true));
        assertEquals(Direction.RIGHT, Direction.fromKeys(true, true, false, true));
    }

    @Test
    void normalizedVectorHasLengthOne() {
        for (Direction d : Direction.values()) {
            if (d == Direction.NONE) continue;
            double length = Math.hypot(d.getNormalizedX(), d.getNormalizedY());
            assertEquals(1.0, length, 1e-6, d.name());
        }
    }
}
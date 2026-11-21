package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RotatingWallTest {
    @ParameterizedTest
    @CsvSource({"HORIZONTAL, 90, 3, 4, 1, 2", "HORIZONTAL, -90, 4, 2, 1, 2",
                "VERTICAL, 90, 2, 4, 2, 1", "VERTICAL, -90, 4, 3, 2, 1"})
    void quarterTurnUsesChosenPivot(OpeningOrientation orientation, int turn,
                                   float x, float y, float width, float height) {
        RotatingWall wall = new RotatingWall(4, 4, orientation, 4, 4, turn);
        assertTrue(wall.rotate());
        Bounds result = wall.getBounds();
        assertEquals(x, result.getX());
        assertEquals(y, result.getY());
        assertEquals(width, result.getWidth());
        assertEquals(height, result.getHeight());
        assertFalse(wall.rotate());
        assertEquals(x, wall.getBounds().getX());
        assertTrue(wall.canCollide());
    }

    @Test
    void levelFourPivotAtTopRightProducesExpectedHorizontalPosition() {
        RotatingWall wall = new RotatingWall(6, 21, OpeningOrientation.VERTICAL, 7, 23, 90);
        wall.rotate();
        assertEquals(7f, wall.getBounds().getX());
        assertEquals(22f, wall.getBounds().getY());
        assertEquals(2f, wall.getBounds().getWidth());
        assertEquals(1f, wall.getBounds().getHeight());
    }

    @Test
    void waitsForEveryOccupantToLeaveThenPermanentlyBecomesSolid() {
        RotatingWall wall = new RotatingWall(4, 4, OpeningOrientation.HORIZONTAL, 4, 4, 90);
        Bounds first = new Bounds(3, 4, 0.5f, 0.5f);
        Bounds second = new Bounds(3, 5, 0.5f, 0.5f);
        Bounds outside = new Bounds(8, 8, 1, 1);
        wall.rotate(first, second);
        assertFalse(wall.canCollide());
        wall.updateClearance(outside, second);
        assertFalse(wall.canCollide());
        wall.updateClearance(outside, outside);
        assertTrue(wall.canCollide());
        wall.updateClearance(first, second);
        assertTrue(wall.canCollide());
    }

    @Test
    void touchingBorderDoesNotDeferCollision() {
        RotatingWall wall = new RotatingWall(4, 4, OpeningOrientation.HORIZONTAL, 4, 4, 90);
        wall.rotate(new Bounds(4, 4, 1, 1));
        assertTrue(wall.canCollide());
    }

    @Test
    void rejectsUnsupportedRotationAndMissingOrientation() {
        assertThrows(IllegalArgumentException.class,
                () -> new RotatingWall(4, 4, OpeningOrientation.HORIZONTAL, 4, 4, 45));
        assertThrows(IllegalArgumentException.class,
                () -> new RotatingWall(4, 4, null, 4, 4, 90));
        assertThrows(IllegalArgumentException.class,
                () -> new RotatingWall(4, 4, OpeningOrientation.HORIZONTAL, Float.NaN, 4, 90));
    }
}

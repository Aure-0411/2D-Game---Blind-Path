package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class WallTest {

    @Test
    void wallBlocksMonsters() {
        Wall wall = new Wall(0, 0, 10, 10);

        assertTrue(wall.blocksMonster());
    }

    @Test
    void wallCollisionsAreEnabledInitially() {
        Wall wall = new Wall(0, 0, 10, 10);

        assertTrue(wall.canCollide());
    }

    @Test
    void wallUsesProvidedPositionAndDimensions() {
        Wall wall = new Wall(10, 20, 4, 6);
        Bounds bounds = wall.getBounds();

        assertTrue(bounds.intersects(new Bounds(10, 20, 1, 1)));
        assertTrue(bounds.intersects(new Bounds(13, 25, 1, 1)));
        assertFalse(bounds.intersects(new Bounds(9, 20, 1, 1)));
        assertFalse(bounds.intersects(new Bounds(14, 20, 1, 1)));
        assertFalse(bounds.intersects(new Bounds(10, 19, 1, 1)));
        assertFalse(bounds.intersects(new Bounds(10, 26, 1, 1)));
    }

    @ParameterizedTest
    @CsvSource({"0, 10", "-1, 10", "10, 0", "10, -1"})
    void wallRejectsNonPositiveDimensions(float width, float height) {
        assertThrows(IllegalArgumentException.class,
            () -> new Wall(0, 0, width, height));
    }
}

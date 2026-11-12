package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ObstacleTest {

    // Sous-classe concrète utilisée uniquement pour tester Obstacle.
    private static class TestObstacle extends Obstacle {
        TestObstacle(float x, float y, float width, float height) {
            super(x, y, width, height);
        }
    }

    @Test
    void overlappingObstaclesCollide() {
        Obstacle first = new TestObstacle(0, 0, 10, 10);
        Obstacle second = new TestObstacle(9, 9, 2, 2);

        assertTrue(first.collide(second));
    }

    @Test
    void separatedObstaclesDoNotCollide() {
        Obstacle first = new TestObstacle(0, 0, 1, 1);
        Obstacle second = new TestObstacle(9, 9, 2, 2);

        assertFalse(first.collide(second));
    }

    @Test
    void collisionsAreEnabledInitially() {
        Obstacle obstacle = new TestObstacle(0, 0, 10, 10);

        assertTrue(obstacle.canCollide());
    }

    @Test
    void collisionsCanBeDisabledAndEnabledAgain() {
        Obstacle obstacle = new TestObstacle(0, 0, 10, 10);
        Obstacle other = new TestObstacle(5, 5, 2, 2);

        obstacle.setCollide(false);
        assertFalse(obstacle.canCollide());
        assertFalse(obstacle.collide(other));

        obstacle.setCollide(true);
        assertTrue(obstacle.canCollide());
        assertTrue(obstacle.collide(other));
    }

    @ParameterizedTest
    @CsvSource({"false, true", "true, false", "false, false"})
    void eitherDisabledObjectPreventsCollision(boolean firstEnabled, boolean secondEnabled) {
        Obstacle first = new TestObstacle(0, 0, 10, 10);
        Obstacle second = new TestObstacle(5, 5, 10, 10);
        first.setCollide(firstEnabled);
        second.setCollide(secondEnabled);

        assertFalse(first.collide(second));
        assertFalse(second.collide(first));
    }

    @ParameterizedTest
    @CsvSource({
        "9, 9, true",       // Chevauchement partiel.
        "2, 2, true",       // Inclusion.
        "0, 0, true",       // Même origine.
        "11, 0, false",     // Séparation horizontale.
        "-3, 0, false",
        "0, 11, false",     // Séparation verticale.
        "0, -3, false",
        "10, 0, false",     // Bords en contact.
        "-2, 0, false",
        "0, 10, false",
        "0, -2, false",
        "10, 10, false"     // Coins en contact.
    })
    void collisionRespectsGeometryInBothDirections(float x, float y, boolean expected) {
        Obstacle first = new TestObstacle(0, 0, 10, 10);
        Obstacle second = new TestObstacle(x, y, 2, 2);

        assertEquals(expected, first.collide(second));
        assertEquals(expected, second.collide(first));
    }

    @ParameterizedTest
    @CsvSource({"0, 10", "-1, 10", "10, 0", "10, -1", "0, 0"})
    void nonPositiveDimensionsAreRejected(float width, float height) {
        assertThrows(IllegalArgumentException.class,
            () -> new TestObstacle(0, 0, width, height));
    }

    @Test
    void negativePositionsAreAllowed() {
        Obstacle obstacle = new TestObstacle(-10, -20, 4, 6);
        Obstacle other = new TestObstacle(-9, -19, 1, 1);

        assertTrue(obstacle.collide(other));
    }

    @Test
    void getBoundsUsesObstaclePositionAndDimensions() {
        Obstacle obstacle = new TestObstacle(10, 20, 4, 6);
        Bounds bounds = obstacle.getBounds();

        assertTrue(bounds.intersects(new Bounds(10, 20, 1, 1)));
        assertTrue(bounds.intersects(new Bounds(13, 25, 1, 1)));
        assertFalse(bounds.intersects(new Bounds(9, 20, 1, 1)));
        assertFalse(bounds.intersects(new Bounds(14, 20, 1, 1)));
        assertFalse(bounds.intersects(new Bounds(10, 19, 1, 1)));
        assertFalse(bounds.intersects(new Bounds(10, 26, 1, 1)));
    }

    @Test
    void getBoundsReturnsDistinctObjects() {
        Obstacle obstacle = new TestObstacle(0, 0, 10, 10);

        assertNotSame(obstacle.getBounds(), obstacle.getBounds());
    }
}

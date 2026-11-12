package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BoundsTest {

    @ParameterizedTest
    @CsvSource({
        "9, 9, 2, 2, true",       // Chevauchement partiel.
        "-1, -1, 2, 2, true",
        "2, 2, 2, 2, true",       // Rectangle entièrement inclus.
        "-1, -1, 12, 12, true",   // Rectangle englobant.
        "0, 0, 10, 10, true",     // Rectangles identiques.
        "11, 0, 2, 2, false",     // Séparation à droite.
        "-3, 0, 2, 2, false",     // Séparation à gauche.
        "0, 11, 2, 2, false",     // Séparation au-dessus.
        "0, -3, 2, 2, false",     // Séparation en dessous.
        "10, 0, 2, 2, false",     // Contact du bord droit.
        "-2, 0, 2, 2, false",     // Contact du bord gauche.
        "0, 10, 2, 2, false",     // Contact du bord haut.
        "0, -2, 2, 2, false",     // Contact du bord bas.
        "10, 10, 2, 2, false",    // Contact par un coin.
        "9.5, 9.5, 0.75, 0.75, true"
    })
    void intersectionMatchesGeometryInBothDirections(
            float x, float y, float width, float height, boolean expected) {
        Bounds first = new Bounds(0, 0, 10, 10);
        Bounds second = new Bounds(x, y, width, height);

        assertEquals(expected, first.intersects(second));
        assertEquals(expected, second.intersects(first));
    }

    @Test
    void rectangleIntersectsItself() {
        Bounds bounds = new Bounds(3, 7, 2, 4);

        assertTrue(bounds.intersects(bounds));
    }

    @Test
    void negativePositionsAreAllowed() {
        Bounds first = new Bounds(-10, -20, 4, 6);
        Bounds second = new Bounds(-9, -19, 1, 1);

        assertTrue(first.intersects(second));
        assertTrue(second.intersects(first));
    }

    @ParameterizedTest
    @CsvSource({
        "0, 10",
        "-1, 10",
        "10, 0",
        "10, -1",
        "0, 0",
        "-1, -1"
    })
    void nonPositiveDimensionsAreRejected(float width, float height) {
        assertThrows(IllegalArgumentException.class,
            () -> new Bounds(0, 0, width, height));
    }
}

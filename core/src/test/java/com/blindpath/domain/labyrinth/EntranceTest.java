package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class EntranceTest {

    @ParameterizedTest
    @CsvSource({
        "HORIZONTAL, 2, 1",
        "VERTICAL, 1, 2"
    })
    void dimensionsMatchOrientation(OpeningOrientation orientation, int expectedWidth, int expectedHeight) {
        Entrance entrance = new Entrance(3, 4, orientation);

        assertEquals(expectedWidth, entrance.getWidth());
        assertEquals(expectedHeight, entrance.getHeight());
        assertEquals(orientation, entrance.getOrientation());
    }

    @ParameterizedTest
    @CsvSource({"3, 7", "0, 0", "-3, -7"})
    void entranceKeepsProvidedPosition(int x, int y) {
        Entrance entrance = new Entrance(x, y, OpeningOrientation.HORIZONTAL);

        assertEquals(x, entrance.getX());
        assertEquals(y, entrance.getY());
    }

    @Test
    void missingOrientationIsRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> new Entrance(0, 0, null));
    }
}

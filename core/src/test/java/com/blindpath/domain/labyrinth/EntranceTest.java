package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class EntranceTest {

    @ParameterizedTest
    @CsvSource({
        "TOP, 2, 1",
        "BOTTOM, 2, 1",
        "LEFT, 1, 2",
        "RIGHT, 1, 2"
    })
    void dimensionsMatchBorder(Border border, int expectedWidth, int expectedHeight) {
        Entrance entrance = new Entrance(3, 4, border);

        assertEquals(expectedWidth, entrance.getWidth());
        assertEquals(expectedHeight, entrance.getHeight());
        assertEquals(border, entrance.getBorder());
    }

    @ParameterizedTest
    @CsvSource({"3, 7", "0, 0", "-3, -7"})
    void entranceKeepsProvidedPosition(int x, int y) {
        Entrance entrance = new Entrance(x, y, Border.TOP);

        assertEquals(x, entrance.getX());
        assertEquals(y, entrance.getY());
    }

    @Test
    void missingBorderIsRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> new Entrance(0, 0, null));
    }
}

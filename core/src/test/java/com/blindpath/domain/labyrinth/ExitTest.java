package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ExitTest {

    @ParameterizedTest
    @CsvSource({
        "TOP, 2, 1",
        "BOTTOM, 2, 1",
        "LEFT, 1, 2",
        "RIGHT, 1, 2"
    })
    void dimensionsMatchBorder(Border border, int expectedWidth, int expectedHeight) {
        Exit exit = new Exit(3, 4, border);

        assertEquals(expectedWidth, exit.getWidth());
        assertEquals(expectedHeight, exit.getHeight());
        assertEquals(border, exit.getBorder());
    }

    @ParameterizedTest
    @CsvSource({"3, 7", "0, 0", "-3, -7"})
    void exitKeepsProvidedPosition(int x, int y) {
        Exit exit = new Exit(x, y, Border.TOP);

        assertEquals(x, exit.getX());
        assertEquals(y, exit.getY());
    }

    @Test
    void missingBorderIsRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> new Exit(0, 0, null));
    }

    @Test
    void shouldReachExitWhenOverlapsIt() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void shouldNotReachExitWhenDoesNotOverlapIt() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void shouldNotReachExitWhenBordersOnlyTouch() {
        // Arrange

        // Act

        // Assert
    }
}

package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.blindpath.domain.character.Gnome;

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
        Exit exit = new Exit(0, 0, Border.TOP);
        Gnome gnome = new Gnome();
        gnome.setPosition(1f, 0f);

        // Act
        boolean reached = exit.isReached(gnome);

        // Assert
        assertTrue(reached);
    }

    @Test
    void shouldNotReachExitWhenDoesNotOverlapIt() {
        // Arrange
        Exit exit = new Exit(0, 0, Border.TOP);
        Gnome gnome = new Gnome();
        gnome.setPosition(10f, 10f);

        // Act
        boolean reached = exit.isReached(gnome);

        // Assert
        assertFalse(reached);
    }

    @Test
    void shouldNotReachExitWhenBordersOnlyTouch() {
        // Arrange
        Exit exit = new Exit(0, 0, Border.TOP);
        Gnome gnome = new Gnome();
        gnome.setPosition(1f, 1f);

        // Act
        boolean reached = exit.isReached(gnome);

        // Assert
        assertFalse(reached);
    }
}

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
        "HORIZONTAL, 2, 1",
        "VERTICAL, 1, 2"
    })
    void dimensionsMatchOrientation(OpeningOrientation orientation, int expectedWidth, int expectedHeight) {
        Exit exit = new Exit(3, 4, orientation);

        assertEquals(expectedWidth, exit.getWidth());
        assertEquals(expectedHeight, exit.getHeight());
        assertEquals(orientation, exit.getOrientation());
    }

    @ParameterizedTest
    @CsvSource({"3, 7", "0, 0", "-3, -7"})
    void exitKeepsProvidedPosition(int x, int y) {
        Exit exit = new Exit(x, y, OpeningOrientation.HORIZONTAL);

        assertEquals(x, exit.getX());
        assertEquals(y, exit.getY());
    }

    @Test
    void missingOrientationIsRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> new Exit(0, 0, null));
    }

    @Test
    void shouldReachExitWhenOverlapsIt() {
        // Arrange
        Exit exit = new Exit(0, 0, OpeningOrientation.HORIZONTAL);
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
        Exit exit = new Exit(0, 0, OpeningOrientation.HORIZONTAL);
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
        Exit exit = new Exit(0, 0, OpeningOrientation.HORIZONTAL);
        Gnome gnome = new Gnome();
        gnome.setPosition(1f, 1f);

        // Act
        boolean reached = exit.isReached(gnome);

        // Assert
        assertFalse(reached);
    }
}

package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class LabyrinthTest {

    private Labyrinth createLabyrinth() {
        Entrance entrance = new Entrance(0, 2, Border.LEFT);
        Exit exit = new Exit(9, 2, Border.RIGHT);
        return new Labyrinth(10, 10, entrance, exit, List.of());
    }

    @Test
    void constructorKeepsEntranceAndExit() {
        Entrance entrance = new Entrance(0, 2, Border.LEFT);
        Exit exit = new Exit(9, 2, Border.RIGHT);

        Labyrinth labyrinth = new Labyrinth(10, 10, entrance, exit, List.of());

        assertSame(entrance, labyrinth.getEntrance());
        assertSame(exit, labyrinth.getExit());
    }

    @Test
    void labyrinthInitiallyHasNoObstacles() {
        Labyrinth labyrinth = createLabyrinth();

        assertTrue(labyrinth.getObstacles().isEmpty());
    }

    @Test
    void returnedListPreventsAddingObstacles() {
        Labyrinth labyrinth = createLabyrinth();
        List<Obstacle> obstacles = labyrinth.getObstacles();
        Wall wall = new Wall(3, 3, 1, 1);

        assertThrows(UnsupportedOperationException.class,
            () -> obstacles.add(wall));
        assertTrue(labyrinth.getObstacles().isEmpty());
    }

    @Test
    void dimensionsArePreserved() {
        Labyrinth labyrinth = createLabyrinth();

        assertEquals(10, labyrinth.getWidth());
        assertEquals(10, labyrinth.getHeight());
    }

    @ParameterizedTest
    @CsvSource({"0, 10", "-1, 10", "10, 0", "10, -1"})
    void nonPositiveDimensionsAreRejected(int width, int height) {
        assertThrows(IllegalArgumentException.class,
            () -> new Labyrinth(width, height, new Entrance(0, 2, Border.LEFT),
                new Exit(9, 2, Border.RIGHT), List.of()));
    }

    @ParameterizedTest
    @CsvSource({"LEFT, 0, 2", "RIGHT, 9, 2", "TOP, 2, 9", "BOTTOM, 2, 0"})
    void openingsCanUseEachBorder(Border border, int x, int y) {
        Entrance entrance = new Entrance(x, y, border);
        Exit exit = new Exit(x, y, border);

        Labyrinth labyrinth = new Labyrinth(10, 10, entrance, exit, List.of());

        assertSame(entrance, labyrinth.getEntrance());
        assertSame(exit, labyrinth.getExit());
    }

    @ParameterizedTest
    @CsvSource({
        "LEFT, 1, 2", "RIGHT, 8, 2", "TOP, 2, 8", "BOTTOM, 2, 1",
        "LEFT, 0, 9", "TOP, 9, 9", "LEFT, -1, 2", "BOTTOM, 2, -1"
    })
    void misplacedOrOverflowingOpeningsAreRejected(Border border, int x, int y) {
        assertThrows(IllegalArgumentException.class,
            () -> new Labyrinth(10, 10, new Entrance(x, y, border),
                new Exit(9, 2, Border.RIGHT), List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new Labyrinth(10, 10, new Entrance(0, 2, Border.LEFT),
                new Exit(x, y, border), List.of()));
    }

    @Test
    void missingConstructorDataIsRejected() {
        Entrance entrance = new Entrance(0, 2, Border.LEFT);
        Exit exit = new Exit(9, 2, Border.RIGHT);

        assertThrows(IllegalArgumentException.class,
            () -> new Labyrinth(10, 10, null, exit, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new Labyrinth(10, 10, entrance, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new Labyrinth(10, 10, entrance, exit, null));
        assertThrows(IllegalArgumentException.class,
            () -> new Labyrinth(10, 10, entrance, exit, Arrays.asList((Obstacle) null)));
    }

    @Test
    void changingOriginalListDoesNotChangeLabyrinth() {
        Wall wall = new Wall(3, 3, 1, 1);
        List<Obstacle> original = new ArrayList<>();
        original.add(wall);
        Labyrinth labyrinth = new Labyrinth(10, 10,
            new Entrance(0, 2, Border.LEFT), new Exit(9, 2, Border.RIGHT), original);

        original.clear();

        assertEquals(List.of(wall), labyrinth.getObstacles());
    }
}

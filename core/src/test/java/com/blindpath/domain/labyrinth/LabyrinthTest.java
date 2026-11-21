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
        Entrance entrance = new Entrance(0, 2, OpeningOrientation.VERTICAL);
        Exit exit = new Exit(9, 2, OpeningOrientation.VERTICAL);
        return new Labyrinth(10, 10, entrance, exit, List.of());
    }

    @Test
    void constructorKeepsEntranceAndExit() {
        Entrance entrance = new Entrance(0, 2, OpeningOrientation.VERTICAL);
        Exit exit = new Exit(9, 2, OpeningOrientation.VERTICAL);

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
            () -> new Labyrinth(width, height, new Entrance(0, 2, OpeningOrientation.VERTICAL),
                new Exit(9, 2, OpeningOrientation.VERTICAL), List.of()));
    }

    @ParameterizedTest
    @CsvSource({"VERTICAL, 0, 2", "VERTICAL, 9, 2", "HORIZONTAL, 2, 9", "HORIZONTAL, 2, 0", "HORIZONTAL, 4, 4", "VERTICAL, 4, 4"})
    void openingsCanBeOnEdgesOrInside(OpeningOrientation orientation, int x, int y) {
        Entrance entrance = new Entrance(x, y, orientation);
        Exit exit = new Exit(x, y, orientation);

        Labyrinth labyrinth = new Labyrinth(10, 10, entrance, exit, List.of());

        assertSame(entrance, labyrinth.getEntrance());
        assertSame(exit, labyrinth.getExit());
    }

    @ParameterizedTest
    @CsvSource({
        "VERTICAL, 0, 9", "HORIZONTAL, 9, 9", "VERTICAL, -1, 2", "HORIZONTAL, 2, -1",
        "VERTICAL, 10, 2", "HORIZONTAL, 2, 10"
    })
    void outOfBoundsOpeningsAreRejected(OpeningOrientation orientation, int x, int y) {
        assertThrows(IllegalArgumentException.class,
            () -> new Labyrinth(10, 10, new Entrance(x, y, orientation),
                new Exit(9, 2, OpeningOrientation.VERTICAL), List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new Labyrinth(10, 10, new Entrance(0, 2, OpeningOrientation.VERTICAL),
                new Exit(x, y, orientation), List.of()));
    }

    @Test
    void missingConstructorDataIsRejected() {
        Entrance entrance = new Entrance(0, 2, OpeningOrientation.VERTICAL);
        Exit exit = new Exit(9, 2, OpeningOrientation.VERTICAL);

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
            new Entrance(0, 2, OpeningOrientation.VERTICAL), new Exit(9, 2, OpeningOrientation.VERTICAL), original);

        original.clear();

        assertEquals(List.of(wall), labyrinth.getObstacles());
    }
}

package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class LabyrinthTest {

    private Labyrinth createLabyrinth() {
        Entrance entrance = new Entrance(0, 2, Border.LEFT);
        Exit exit = new Exit(9, 2, Border.RIGHT);
        return new Labyrinth(entrance, exit);
    }

    @Test
    void constructorKeepsEntranceAndExit() {
        Entrance entrance = new Entrance(0, 2, Border.LEFT);
        Exit exit = new Exit(9, 2, Border.RIGHT);

        Labyrinth labyrinth = new Labyrinth(entrance, exit);

        assertSame(entrance, labyrinth.getEntrance());
        assertSame(exit, labyrinth.getExit());
    }

    @Test
    void labyrinthInitiallyHasNoObstacles() {
        Labyrinth labyrinth = createLabyrinth();

        assertTrue(labyrinth.geObstacles().isEmpty());
    }

    @Test
    void returnedListPreventsAddingObstacles() {
        Labyrinth labyrinth = createLabyrinth();
        List<Obstacle> obstacles = labyrinth.geObstacles();
        Wall wall = new Wall(3, 3, 1, 1);

        assertThrows(UnsupportedOperationException.class,
            () -> obstacles.add(wall));
        assertTrue(labyrinth.geObstacles().isEmpty());
    }
}

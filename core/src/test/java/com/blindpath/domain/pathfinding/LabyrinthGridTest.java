package com.blindpath.domain.pathfinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.labyrinth.Wall;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link LabyrinthGrid} on a 10 x 10 labyrinth (columns and rows 0 to 9).
 */
class LabyrinthGridTest {

    private static LabyrinthGrid grid(List<Obstacle> walls) {
        return new LabyrinthGrid(
                new Labyrinth(10, 10, new Entrance(0, 2, Border.LEFT), new Exit(9, 2, Border.RIGHT), walls));
    }

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    @Test
    void sizeComesFromTheLabyrinth() {
        LabyrinthGrid empty = grid(List.of());
        assertEquals(10, empty.getWidth(), "width of the labyrinth");
        assertEquals(10, empty.getHeight(), "height of the labyrinth");
    }

    @Test
    void cellsInsideAnEmptyMapAreWalkable() {
        LabyrinthGrid empty = grid(List.of());
        assertTrue(empty.isWalkable(cell(0, 0)), "bottom-left corner is inside the map");
        assertTrue(empty.isWalkable(cell(9, 9)), "top-right corner is inside the map");
    }

    @Test
    void cellsOutsideTheMapAreNotWalkable() {
        LabyrinthGrid empty = grid(List.of());
        assertFalse(empty.isWalkable(cell(-1, 0)), "column -1 is outside");
        assertFalse(empty.isWalkable(cell(0, -1)), "row -1 is outside");
        assertFalse(empty.isWalkable(cell(10, 0)), "column 10 is outside a 10-wide map");
        assertFalse(empty.isWalkable(cell(0, 10)), "row 10 is outside a 10-high map");
    }

    @Test
    void wallCellsAreNotWalkable() {
        LabyrinthGrid withWall = grid(List.of(new Wall(3, 0, 1, 5)));
        assertFalse(withWall.isWalkable(cell(3, 0)), "bottom of the wall is blocked");
        assertFalse(withWall.isWalkable(cell(3, 4)), "top of the wall is blocked");
        assertTrue(withWall.isWalkable(cell(3, 5)), "the cell just above the wall is free");
        assertTrue(withWall.isWalkable(cell(4, 0)), "the cell just right of the wall is free");
        assertTrue(withWall.isInside(cell(3, 0)), "a wall cell is still inside the map");
    }

    @Test
    void wallThatNoLongerCollidesIsWalkable() {
        Wall wall = new Wall(3, 0, 1, 5);
        wall.setCollide(false);
        assertTrue(grid(List.of(wall)).isWalkable(cell(3, 2)), "a disabled wall does not block");
    }

    @Test
    void neighborsInTheMiddleOfAnEmptyMap() {
        List<GridPosition> neighbors = grid(List.of()).getNeighbors(cell(5, 5));
        assertEquals(4, neighbors.size(), "4 directions, no diagonal");
        assertTrue(neighbors.containsAll(List.of(cell(6, 5), cell(4, 5), cell(5, 6), cell(5, 4))),
                "right, left, up and down");
    }

    @Test
    void neighborsStopAtTheBorderAndAtWalls() {
        assertEquals(2, grid(List.of()).getNeighbors(cell(0, 0)).size(), "a corner has only 2 neighbors");
        List<GridPosition> nextToWall = grid(List.of(new Wall(3, 0, 1, 5))).getNeighbors(cell(2, 2));
        assertEquals(3, nextToWall.size(), "the wall on the right removes one neighbor");
        assertFalse(nextToWall.contains(cell(3, 2)), "the wall cell is not a neighbor");
    }

    @Test
    void labyrinthIsRequired() {
        assertThrows(IllegalArgumentException.class, () -> new LabyrinthGrid(null), "labyrinth required");
    }
}

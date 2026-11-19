package com.blindpath.domain.pathfinding;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.labyrinth.Wall;

/**
 * Tests of {@link LabyrinthGrid} for an agent bigger than one cell (the 2x2 monster),
 * on a 10 x 10 labyrinth. A position is the bottom-left cell of the agent's block.
 */
class LabyrinthGridAgentSizeTest {

    private static LabyrinthGrid grid(int agentSize, List<Obstacle> walls) {
        return new LabyrinthGrid(
                new Labyrinth(10, 10, new Entrance(0, 2, Border.LEFT), new Exit(9, 2, Border.RIGHT), walls),
                agentSize);
    }

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    @Test
    void defaultAgentSizeIsOne() {
        LabyrinthGrid grid = new LabyrinthGrid(
                new Labyrinth(10, 10, new Entrance(0, 2, Border.LEFT), new Exit(9, 2, Border.RIGHT), List.of()));
        assertEquals(1, grid.getAgentSize());
    }

    @Test
    void agentSizeBelowOneIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> grid(0, List.of()));
    }

    @Test
    void wholeBlockMustBeInsideTheMap() {
        LabyrinthGrid big = grid(2, List.of());
        assertTrue(big.isWalkable(cell(8, 8)), "block 8-9 x 8-9 fits in a 10x10 map");
        assertFalse(big.isWalkable(cell(9, 0)), "column 10 would be outside");
        assertFalse(big.isWalkable(cell(0, 9)), "row 10 would be outside");
    }

    @Test
    void blockTouchingAWallIsNotWalkable() {
        // Wall on column 3, rows 0 to 4.
        LabyrinthGrid big = grid(2, List.of(new Wall(3, 0, 1, 5)));
        assertFalse(big.isWalkable(cell(2, 0)), "block 2-3 overlaps the wall column");
        assertTrue(big.isWalkable(cell(1, 0)), "block 1-2 stops just before the wall");
        assertTrue(big.isWalkable(cell(4, 0)), "block 4-5 starts just after the wall");
    }

    @Test
    void neighborsOnlyKeepBlocksThatFit() {
        // Corridor 2 cells wide (columns 1-2) between walls on columns 0 and 3.
        LabyrinthGrid big = grid(2, List.of(new Wall(0, 0, 1, 10), new Wall(3, 0, 1, 10)));
        List<GridPosition> neighbors = big.getNeighbors(cell(1, 4));
        assertEquals(2, neighbors.size(), "only up and down: left and right would hit a wall");
        assertTrue(neighbors.contains(cell(1, 3)));
        assertTrue(neighbors.contains(cell(1, 5)));
    }

    @Test
    void anchorCoveringKeepsTheCellWhenTheBlockFits() {
        LabyrinthGrid big = grid(2, List.of());
        assertEquals(cell(4, 4), big.anchorCovering(cell(4, 4)));
    }

    @Test
    void anchorCoveringShiftsTheBlockAwayFromAWall() {
        // Wall on column 3: a block starting on column 2 would overlap it, so we start on column 1.
        LabyrinthGrid big = grid(2, List.of(new Wall(3, 0, 1, 10)));
        GridPosition anchor = big.anchorCovering(cell(2, 4));
        assertEquals(cell(1, 4), anchor);
    }

    @Test
    void anchorCoveringShiftsDownUnderACeiling() {
        // Wall on row 6: a block starting on row 5 would overlap it, so we start on row 4.
        LabyrinthGrid big = grid(2, List.of(new Wall(0, 6, 10, 1)));
        assertEquals(cell(4, 4), big.anchorCovering(cell(4, 5)));
    }

    @Test
    void anchorCoveringWithSizeOneIsTheCellItself() {
        LabyrinthGrid small = grid(1, List.of(new Wall(3, 0, 1, 10)));
        assertEquals(cell(2, 4), small.anchorCovering(cell(2, 4)));
    }
}

package com.blindpath.infrastructure.pathfinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.labyrinth.Wall;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests of the gdx-ai adapter ({@link GdxAiPathfinder}, {@link GridGraph}, {@link GridDistanceHeuristic})
 * on a 10 x 10 labyrinth (columns and rows 0 to 9).
 */
class GdxAiPathfinderTest {

    private final GdxAiPathfinder pathfinder = new GdxAiPathfinder();

    private static LabyrinthGrid grid(List<Obstacle> walls) {
        return new LabyrinthGrid(
                new Labyrinth(10, 10, new Entrance(0, 2, Border.LEFT), new Exit(9, 2, Border.RIGHT), walls));
    }

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    private static void assertNeighbours(List<GridPosition> path, GridPosition start) {
        GridPosition previous = start;
        for (GridPosition step : path) {
            int distance = Math.abs(step.getColumn() - previous.getColumn())
                    + Math.abs(step.getRow() - previous.getRow());
            assertEquals(1, distance, "each step moves exactly one cell, no diagonal: " + previous + " -> " + step);
            previous = step;
        }
    }

    @Test
    void straightLineInAnEmptyMap() {
        List<GridPosition> path = pathfinder.findPath(grid(List.of()), cell(0, 0), cell(4, 0));
        assertEquals(List.of(cell(1, 0), cell(2, 0), cell(3, 0), cell(4, 0)), path,
                "the path goes straight and does not include the start cell");
    }

    @Test
    void pathIsAsShortAsPossible() {
        List<GridPosition> path = pathfinder.findPath(grid(List.of()), cell(1, 1), cell(4, 5));
        assertEquals(7, path.size(), "3 columns + 4 rows = 7 steps without diagonals");
        assertEquals(cell(4, 5), path.get(path.size() - 1), "the path ends on the target");
        assertNeighbours(path, cell(1, 1));
    }

    @Test
    void pathGoesAroundAWall() {
        // wall on column 3, rows 0 to 4: from (1, 0) to (5, 0) the monster has to go up to row 5
        LabyrinthGrid withWall = grid(List.of(new Wall(3, 0, 1, 5)));
        List<GridPosition> path = pathfinder.findPath(withWall, cell(1, 0), cell(5, 0));
        assertEquals(14, path.size(), "up 5 rows, across 4 columns, down 5 rows");
        assertNeighbours(path, cell(1, 0));
        for (GridPosition step : path) {
            assertTrue(withWall.isWalkable(step), "the path never goes through a wall: " + step);
        }
    }

    @Test
    void noPathWhenTheTargetIsEnclosed() {
        // the target (8, 8) is boxed in by four walls
        LabyrinthGrid closed = grid(List.of(
                new Wall(7, 8, 1, 1), new Wall(9, 8, 1, 1), new Wall(8, 7, 1, 1), new Wall(8, 9, 1, 1)));
        assertTrue(pathfinder.findPath(closed, cell(0, 0), cell(8, 8)).isEmpty(), "no path to an enclosed cell");
    }

    @Test
    void noPathToAWallOrOutsideTheMap() {
        LabyrinthGrid withWall = grid(List.of(new Wall(3, 0, 1, 5)));
        assertTrue(pathfinder.findPath(withWall, cell(0, 0), cell(3, 2)).isEmpty(), "no path into a wall");
        assertTrue(pathfinder.findPath(withWall, cell(0, 0), cell(12, 0)).isEmpty(), "no path outside the map");
        assertTrue(pathfinder.findPath(withWall, cell(-1, 0), cell(5, 5)).isEmpty(), "no path from outside the map");
    }

    @Test
    void noStepWhenAlreadyOnTheTarget() {
        assertTrue(pathfinder.findPath(grid(List.of()), cell(2, 2), cell(2, 2)).isEmpty(),
                "start == target: nothing to do");
    }

    @Test
    void graphIndexesEveryCellOnce() {
        GridGraph graph = new GridGraph(grid(List.of()));
        assertEquals(100, graph.getNodeCount(), "10 x 10 cells");
        assertEquals(0, graph.getIndex(cell(0, 0)), "first cell has index 0");
        assertEquals(99, graph.getIndex(cell(9, 9)), "last cell has index 99");
        assertEquals(graph.getIndex(cell(4, 7)), graph.getIndex(new GridPosition(4, 7)),
                "the same cell always gets the same index");
        assertEquals(2, graph.getConnections(cell(0, 0)).size, "a corner has 2 connections");
        assertSame(graph.getNode(cell(4, 7)), graph.getNode(new GridPosition(4, 7)),
                "gdx-ai compares nodes with ==: the graph always returns the same object for a cell");
    }

    @Test
    void heuristicIsTheManhattanDistance() {
        GridDistanceHeuristic heuristic = new GridDistanceHeuristic();
        assertEquals(7f, heuristic.estimate(cell(1, 1), cell(4, 5)), 0.001f, "3 columns + 4 rows");
        assertEquals(7f, heuristic.estimate(cell(4, 5), cell(1, 1)), 0.001f, "same distance the other way");
    }
}

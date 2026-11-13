package com.blindpath.domain.pathfinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GridPositionTest {

    @Test
    void keepsColumnAndRow() {
        GridPosition position = new GridPosition(3, 7);
        assertEquals(3, position.getColumn(), "column of the cell");
        assertEquals(7, position.getRow(), "row of the cell");
    }

    @Test
    void sameCellsAreEqual() {
        GridPosition first = new GridPosition(2, 5);
        GridPosition second = new GridPosition(2, 5);
        assertEquals(first, second, "two positions on the same cell are equal");
        assertEquals(first.hashCode(), second.hashCode(), "equal positions have the same hashCode");
        assertEquals(first, first, "a position is equal to itself");
    }

    @Test
    void differentCellsAreNotEqual() {
        GridPosition position = new GridPosition(2, 5);
        assertNotEquals(position, new GridPosition(5, 2), "column and row are not interchangeable");
        assertNotEquals(position, new GridPosition(2, 6), "a different row is a different cell");
        assertNotEquals(position, null, "a position is never equal to null");
        assertNotEquals(position, "(2, 5)", "a position is not equal to another type");
    }

    @Test
    void worksInASet() {
        Set<GridPosition> visited = new HashSet<>();
        visited.add(new GridPosition(1, 1));
        assertTrue(visited.contains(new GridPosition(1, 1)), "a new object for the same cell is found in a Set");
        visited.add(new GridPosition(1, 1));
        assertEquals(1, visited.size(), "the same cell is not stored twice");
    }

    @Test
    void readableText() {
        assertEquals("(4, 9)", new GridPosition(4, 9).toString(), "readable text for test messages and logs");
    }
}

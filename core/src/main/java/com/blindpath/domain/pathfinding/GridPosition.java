package com.blindpath.domain.pathfinding;

import java.util.Objects;

public class GridPosition {

    private final int column;
    private final int row;

    public GridPosition(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (!(other instanceof GridPosition)) {
            return false;
        }

        GridPosition position = (GridPosition) other;
        return (column == position.column && row == position.row);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }

    @Override
    public String toString() {
        return ("(" + column + ", " + row + ")");
    }

}
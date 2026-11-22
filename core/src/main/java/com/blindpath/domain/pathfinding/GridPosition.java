package com.blindpath.domain.pathfinding;

import java.util.Objects;

/**
 * Case de la grille du labyrinthe, repérée par sa colonne et sa ligne (origine en bas à gauche).
 * Deux positions sont égales si elles désignent la même case.
 */
public class GridPosition {

    private final int column;
    private final int row;

    /**
     * @param column la colonne
     * @param row la ligne
     */
    public GridPosition(int column, int row) {
        this.column = column;
        this.row = row;
    }

    /** @return la colonne */
    public int getColumn() {
        return column;
    }

    /** @return la ligne */
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
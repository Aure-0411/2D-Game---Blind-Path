package com.blindpath.domain.pathfinding;

import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.labyrinth.Wall;
import java.util.ArrayList;
import java.util.List;

public class LabyrinthGrid {

    private static final int[][] DIRECTIONS = {
        {-1, 0}, // Gauche
        {1, 0}, // Droite
        {0, -1}, // Bas
        {0, 1}   // Haut

    };

    private final Labyrinth labyrinth;

    public LabyrinthGrid(Labyrinth labyrinth) {
        if (labyrinth == null) {
            throw new IllegalArgumentException("Labyrinth must not be null");
        }
        this.labyrinth = labyrinth;
    }

    public int getWidth() {
        return labyrinth.getWidth();
    }

    public int getHeight() {
        return labyrinth.getHeight();
    }

    public boolean isInside(GridPosition position) {
        return position.getColumn() >= 0 && position.getRow() >=0 && position.getColumn() < getWidth() && position.getRow() < getHeight();
    }

    public boolean isWalkable(GridPosition position) {
        if (!isInside(position)) {
            return false;
        }

        Bounds cell = newBoundsCell(position);

        return true;

    }

}


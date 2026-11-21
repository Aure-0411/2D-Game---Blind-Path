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

    // Marge de tolérance (en cases) contre les imprécisions des float, voir isWalkable.
    private static final float WALL_MARGIN = 0.05f;

    private final Labyrinth labyrinth;
    // Taille (en cases) de l'agent qui se déplace sur la grille : 1 = une case, 2 = un bloc 2x2.
    // Une position désigne alors la case en bas à gauche du bloc.
    private final int agentSize;

    public LabyrinthGrid(Labyrinth labyrinth) {
        this(labyrinth, 1);
    }

    public LabyrinthGrid(Labyrinth labyrinth, int agentSize) {
        if (labyrinth == null) {
            throw new IllegalArgumentException("Labyrinth must not be null");
        }
        if (agentSize < 1) {
            throw new IllegalArgumentException("agentSize must be >= 1");
        }
        this.labyrinth = labyrinth;
        this.agentSize = agentSize;
    }

    public GridPosition getExitCell() {
        return new GridPosition(labyrinth.getExit().getX(), labyrinth.getExit().getY());
    }

    public int getAgentSize() {
        return agentSize;
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
        // Tout le bloc agentSize x agentSize doit tenir dans la carte...
        if (!isInside(position)
                || position.getColumn() + agentSize > getWidth()
                || position.getRow() + agentSize > getHeight()) {
            return false;
        }

        // ... et ne toucher aucun mur. On rétrécit le bloc d'une petite marge de chaque côté :
        // les murs chargés depuis Tiled passent par une division en float (pixels / 250)
        // et peuvent valoir 7.0000005 au lieu de 7. Sans marge, un bloc qui touche juste
        // le bord d'un mur serait vu comme "dans" le mur, et le monstre resterait coincé.
        Bounds cell = new Bounds(
                position.getColumn() + WALL_MARGIN,
                position.getRow() + WALL_MARGIN,
                agentSize - 2 * WALL_MARGIN,
                agentSize - 2 * WALL_MARGIN);

        for (Obstacle obstacle : labyrinth.getObstacles()) {
            if (obstacle instanceof Wall wall && wall.blocksMonster() && wall.canCollide() && wall.getBounds().intersects(cell)) {
                return false;
            }
        }

        return true;

    }

    public List<GridPosition> getNeighbors(GridPosition position) {
        
        List<GridPosition> neighbors = new ArrayList<>();

        for (int[] direction : DIRECTIONS) {
            GridPosition next = new GridPosition(position.getColumn() + direction[0], position.getRow() + direction[1]);
            if (isWalkable(next)) {
                neighbors.add(next);
            }
        }

        return neighbors;

    }

    /**
     * Renvoie la position (coin bas-gauche) d'un bloc praticable qui recouvre la case donnée.
     * Sert à viser le personnage avec un agent de plusieurs cases : on teste d'abord le bloc
     * qui commence sur la case, puis ceux décalés d'une case à gauche et/ou en bas.
     * Si aucun bloc ne convient, renvoie la case telle quelle.
     */
    public GridPosition anchorCovering(GridPosition cell) {
        for (int dRow = 0; dRow < agentSize; dRow++) {
            for (int dColumn = 0; dColumn < agentSize; dColumn++) {
                GridPosition anchor = new GridPosition(cell.getColumn() - dColumn, cell.getRow() - dRow);
                if (isWalkable(anchor)) {
                    return anchor;
                }
            }
        }
        return cell;
    }

    
}
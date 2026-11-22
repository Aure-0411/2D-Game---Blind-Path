package com.blindpath.domain.pathfinding;

import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.labyrinth.Wall;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue en grille du labyrinthe, utilisée pour le calcul de chemin des monstres.
 * <p>
 * Elle gère des agents de plusieurs cases : pour un monstre de 2 x 2, une position désigne
 * la case en bas à gauche du bloc, et tout le bloc doit être libre. Les déplacements se font
 * en 4 directions (pas de diagonale).
 */
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

    /**
     * Crée une grille pour un agent d'une case.
     *
     * @param labyrinth le labyrinthe
     */
    public LabyrinthGrid(Labyrinth labyrinth) {
        this(labyrinth, 1);
    }

    /**
     * @param labyrinth le labyrinthe
     * @param agentSize la taille de l'agent, en cases (2 pour un bloc 2 x 2)
     * @throws IllegalArgumentException si le labyrinthe est {@code null} ou la taille inférieure à 1
     */
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

    /** @return la première case de la sortie */
    public GridPosition getExitCell() {
        return new GridPosition(labyrinth.getExit().getX(), labyrinth.getExit().getY());
    }

    /** @return la taille de l'agent, en cases */
    public int getAgentSize() {
        return agentSize;
    }

    /** @return la largeur de la grille, en cases */
    public int getWidth() {
        return labyrinth.getWidth();
    }

    /** @return la hauteur de la grille, en cases */
    public int getHeight() {
        return labyrinth.getHeight();
    }

    /**
     * @param position une case
     * @return {@code true} si la case est dans la carte
     */
    public boolean isInside(GridPosition position) {
        return position.getColumn() >= 0 && position.getRow() >=0 && position.getColumn() < getWidth() && position.getRow() < getHeight();
    }

    /**
     * Teste si l'agent peut se tenir à cette position : tout son bloc doit tenir dans la carte
     * et ne toucher aucun mur qui bloque les monstres. Une petite marge évite qu'un bloc collé
     * à un mur soit vu « dans » le mur à cause des arrondis des float.
     *
     * @param position la case du coin bas gauche du bloc
     * @return {@code true} si la position est praticable
     */
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

    /**
     * @param position la case actuelle
     * @return les cases voisines praticables (gauche, droite, bas, haut)
     */
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
     *
     * @param cell la case à recouvrir
     * @return la position du bloc, ou la case elle-même si aucun bloc ne convient
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
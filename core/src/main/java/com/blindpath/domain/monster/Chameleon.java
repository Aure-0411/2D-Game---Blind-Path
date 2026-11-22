package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

/**
 * Chameleon (palier extrême) : il poursuit le héros mais reste caché sous le brouillard.
 * Il ne devient visible que lorsqu'il est à {@link #REVEAL_RANGE} cases ou moins du héros.
 */
public class Chameleon extends Monster {

    /** Nombre de cases parcourues à chaque déplacement. */
    public static final int CASE_PER_MOVE = 1;
    /** Distance (en cases, dans les 8 directions) à partir de laquelle le Chameleon devient visible. */
    public static final int REVEAL_RANGE = 3;

    /**
     * Crée un Chameleon qui poursuit le héros.
     *
     * @param grid la grille du labyrinthe
     * @param start la case de départ
     * @param pathfinder le calcul de chemin
     */
    public Chameleon(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, new ChaseStrategy(pathfinder), start);
    }

    /**
     * Crée un Chameleon avec une stratégie de déplacement choisie (utile pour les tests).
     *
     * @param grid la grille du labyrinthe
     * @param strategy la stratégie de déplacement
     * @param start la case de départ
     */
    public Chameleon(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        super(grid, strategy, start);
        setCasePerMove(CASE_PER_MOVE);
    }

    /** @return {@code true} si le héros est à {@link #REVEAL_RANGE} cases ou moins */
    public boolean isRevealed() {
        GridPosition target = getTarget();
        if (target == null) {
            return false;
        }
        int dc = Math.abs(target.getColumn() - getPosition().getColumn());
        int dr = Math.abs(target.getRow() - getPosition().getRow());
        return Math.max(dc, dr) <= REVEAL_RANGE;
    }
}
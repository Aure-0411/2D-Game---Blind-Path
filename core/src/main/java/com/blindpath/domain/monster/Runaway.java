package com.blindpath.domain.monster;

import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

/**
 * Runaway (palier moyen) : il poursuit le héros lentement, mais à chaque claquement
 * il fuit de {@link #EXIT_SNAP_DISTANCE} cases vers la sortie au lieu d'approcher.
 */
public class Runaway extends Monster {

    /** Nombre de ticks entre deux déplacements (2 secondes). */
    public static final int TICKS_PER_MOVE = 2 * DEFAULT_TICKS_PER_MOVE;
    /** Nombre de cases parcourues à chaque déplacement. */
    public static final int CASE_PER_MOVE = 2;
    /** Nombre de cases parcourues vers la sortie à chaque claquement. */
    public static final int EXIT_SNAP_DISTANCE = 3;

    private final GridPosition exit;

    /**
     * Crée un Runaway qui poursuit le héros et fuit vers la sortie.
     *
     * @param grid la grille du labyrinthe
     * @param start la case de départ
     * @param pathfinder le calcul de chemin
     */
    public Runaway(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, new ChaseStrategy(pathfinder), start);
    }

    private Runaway(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {

        super(grid, strategy, start);

        this.exit = grid.anchorCovering(grid.getExitCell());
        setTicksPerMove(TICKS_PER_MOVE);
        setCasePerMove(CASE_PER_MOVE);
        setSnapDistance(EXIT_SNAP_DISTANCE);
    }

    /** @return la case de la sortie vers laquelle le Runaway fuit */
    public GridPosition getExit() {
        return exit;
    }

    /**
     * Fait fuir le Runaway vers la sortie, sauf s'il est immobilisé.
     *
     * @param event le claquement
     */
    @Override
    public void onSnap(SnapEvent event) {
        if (!canMove()) {
            return;
        }
        moveTowards(exit, getSnapDistance());
    }
}
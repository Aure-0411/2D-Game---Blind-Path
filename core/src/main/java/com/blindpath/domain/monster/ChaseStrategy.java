package com.blindpath.domain.monster;

import java.util.List;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.pathfinding.GridPosition;

/**
 * Stratégie de poursuite (patron Strategy) : le monstre avance d'une case sur le plus court
 * chemin vers sa cible.
 */
public class ChaseStrategy implements MovementStrategy {
    private final Pathfinder pathfinder;

    /**
     * @param pathfinder le calcul de chemin
     * @throws IllegalArgumentException si le pathfinder est {@code null}
     */
    public ChaseStrategy(Pathfinder pathfinder) {
        if (pathfinder == null) throw new IllegalArgumentException("pathfinder");
        this.pathfinder = pathfinder;
    }

    /**
     * @param grid la grille du labyrinthe
     * @param from la case actuelle du monstre
     * @param target la case visée
     * @return la première case du chemin, ou {@code from} si aucun chemin n'existe
     */
    @Override
    public GridPosition nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target) {
        List<GridPosition> path = pathfinder.findPath(grid, from, target);
        return path.isEmpty() ? from : path.get(0);
    }
}
package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;

/** Façon dont un monstre choisit sa prochaine case (patron Strategy). */
public interface MovementStrategy {
    /**
     * @param grid la grille du labyrinthe
     * @param from la case actuelle du monstre
     * @param target la case visée
     * @return la prochaine case du monstre
     */
    GridPosition nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target);
}
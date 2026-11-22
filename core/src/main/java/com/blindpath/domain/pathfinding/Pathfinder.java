package com.blindpath.domain.pathfinding;

import java.util.List;

/**
 * Calcul du plus court chemin entre deux cases de la grille.
 * L'implémentation (A* de gdx-ai) est dans l'infrastructure.
 */
public interface Pathfinder {

    /**
     * @param grid la grille du labyrinthe
     * @param start la case de départ
     * @param target la case d'arrivée
     * @return les cases à parcourir, sans la case de départ ; vide si aucun chemin n'existe
     */
    List<GridPosition> findPath(LabyrinthGrid grid, GridPosition start, GridPosition target);

}
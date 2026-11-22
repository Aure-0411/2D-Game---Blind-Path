package com.blindpath.infrastructure.pathfinding;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import java.util.ArrayList;
import java.util.List;

/** Calcul de chemin avec l'algorithme A* de la bibliothèque gdx-ai. */
public class GdxAiPathfinder implements Pathfinder {


    private final GridDistanceHeuristic heuristic = new GridDistanceHeuristic();

    /**
     * @param grid la grille du labyrinthe
     * @param start la case de départ
     * @param target la case d'arrivée
     * @return les cases à parcourir, sans la case de départ ; vide si le départ est l'arrivée,
     *         si l'arrivée n'est pas praticable ou si aucun chemin n'existe
     */
    @Override
    public List<GridPosition> findPath(LabyrinthGrid grid, GridPosition start, GridPosition target) {

        if (start.equals(target) || !grid.isInside(start) || !grid.isWalkable(target)) {
            return List.of();
        }

        GridGraph graph = new GridGraph(grid);
        IndexedAStarPathFinder<GridPosition> finder = new IndexedAStarPathFinder<>(graph);
        DefaultGraphPath<GridPosition> graphPath = new DefaultGraphPath<>();

        if (!finder.searchNodePath(graph.getNode(start), graph.getNode(target), heuristic, graphPath)) {
            return List.of();
        }

        List<GridPosition> path = new ArrayList<>();

        for (int i = 1; i < graphPath.getCount(); i++) {
            path.add(graphPath.get(i));
        }

        return path;
    }
}

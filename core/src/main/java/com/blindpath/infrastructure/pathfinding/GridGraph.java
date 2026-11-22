package com.blindpath.infrastructure.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;

/**
 * Adapte la grille du labyrinthe au format de graphe attendu par gdx-ai :
 * chaque case est un nœud, relié à ses voisines praticables.
 */
public class GridGraph implements IndexedGraph<GridPosition> {

    private final LabyrinthGrid grid;
    private final GridPosition[] nodes;

    /** @param grid la grille du labyrinthe */
    public GridGraph(LabyrinthGrid grid) {

        this.grid = grid;
        this.nodes = new GridPosition[grid.getWidth() * grid.getHeight()];

        for (int row = 0; row < grid.getHeight(); row++) {
            for (int column = 0; column < grid.getWidth(); column++) {
                nodes[row * grid.getWidth() + column] = new GridPosition(column, row);
            }
        }
    }

    /**
     * @param node une case
     * @return son indice dans le graphe
     */
    @Override
    public int getIndex(GridPosition node) {
        return (node.getRow() * grid.getWidth() + node.getColumn());
    }

    /** @return le nombre de nœuds (toutes les cases de la grille) */
    @Override
    public int getNodeCount() {
        return nodes.length;
    }

    /**
     * @param position une case
     * @return le nœud du graphe correspondant à cette case
     */
    public GridPosition getNode(GridPosition position) {
        return nodes[getIndex(position)];
    }

    /**
     * @param fromNode une case
     * @return les liaisons vers ses voisines praticables
     */
    @Override
    public Array<Connection<GridPosition>> getConnections(GridPosition fromNode) {
        Array<Connection<GridPosition>> connections = new Array<>();

        for (GridPosition neighbor : grid.getNeighbors(fromNode)) {
            connections.add(new DefaultConnection<>(fromNode, getNode(neighbor)));
        }
        return connections;
    }
}


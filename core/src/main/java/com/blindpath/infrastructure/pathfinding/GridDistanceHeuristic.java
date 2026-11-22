package com.blindpath.infrastructure.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.blindpath.domain.pathfinding.GridPosition;

/**
 * Heuristique de A* : distance de Manhattan (horizontale + verticale), adaptée aux
 * déplacements en 4 directions.
 */
public class GridDistanceHeuristic implements Heuristic<GridPosition> {
    
    /**
     * @param node la case actuelle
     * @param endNode la case d'arrivée
     * @return la distance de Manhattan entre les deux cases
     */
    @Override
    public float estimate(GridPosition node, GridPosition endNode) {
        return (Math.abs(endNode.getColumn() - node.getColumn()) + Math.abs(endNode.getRow() - node.getRow()));
    }
}
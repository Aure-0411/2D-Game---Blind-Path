package com.blindpath.infrastructure.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.blindpath.domain.pathfinding.GridPosition;

public class GridDistanceHeuristic implements Heuristic<GridPosition> {
    
    @Override
    public float estimate(GridPosition node, GridPosition endNode) {
        return (Math.abs(endNode.getColumn() - node.getColumn()) + Math.abs(endNode.getRow() - node.getRow()));
    }
}
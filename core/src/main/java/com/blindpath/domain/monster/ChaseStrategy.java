package com.blindpath.domain.monster;

import java.util.List;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.pathfinding.GridPosition;

public class ChaseStrategy implements MovementStrategy {
    private final Pathfinder pathfinder;

    public ChaseStrategy(Pathfinder pathfinder) {
        if (pathfinder == null) throw new IllegalArgumentException("pathfinder");
        this.pathfinder = pathfinder;
    }

    @Override
    public GridPosition nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target) {
        List<GridPosition> path = pathfinder.findPath(grid, from, target);
        return path.isEmpty() ? from : path.get(0);
    }
}
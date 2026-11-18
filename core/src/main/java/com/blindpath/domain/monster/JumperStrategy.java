package com.blindpath.domain.monster;

import java.util.List;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

public class JumperStrategy implements MovementStrategy {

    public static final int DEFAULT_JUMP_RANGE = 2;

    private final Pathfinder pathfinder;
    private final int jumpRange;
    private JumperState state = JumperState.CHASING;

    public JumperStrategy(Pathfinder pathfinder, int jumpRange) {

        if (pathfinder == null) {
            throw new IllegalArgumentException("pathfinder must not be null");
        }

        if (jumpRange < 1) {
            throw new IllegalArgumentException("jumpRange must be >= 1");
        }

        this.pathfinder = pathfinder;
        this.jumpRange = jumpRange;
    }

    public JumperStrategy(Pathfinder pathfinder) {
        this(pathfinder, DEFAULT_JUMP_RANGE);
    }

    public JumperState getState() {
        return state;
    }

    public int getJumpRange() {
        return jumpRange;
    }

    private boolean isInRange(GridPosition from, GridPosition target) {

        int dx = Math.abs(from.getColumn() - target.getColumn());
        int dy = Math.abs(from.getRow() - target.getRow());
        int distance = dx + dy;
        
        if (distance > 0 && distance <= jumpRange) {
            return true;
        }

        return false;
        
    }

    private GridPosition chase (LabyrinthGrid grid, GridPosition from, GridPosition target) {

        List<GridPosition> path = pathfinder.findPath(grid, from, target);

        if (path.isEmpty()) {
            return from;
        }

        return path.get(0);
    }

    @Override
    public GridPosition nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target) {

        if (state == JumperState.ALERT) {

            state = JumperState.CHASING;

            if (isInRange(from, target) && grid.isWalkable(target)) {
                return target;
            }

            return chase(grid, from, target);
        }

        if (isInRange(from, target)) {
            state = JumperState.ALERT;

            return from;
        }

        return chase(grid, from, target);
    }
    
}
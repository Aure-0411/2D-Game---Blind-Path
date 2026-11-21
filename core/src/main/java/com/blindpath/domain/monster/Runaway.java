package com.blindpath.domain.monster;

import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

public class Runaway extends Monster {

    public static final int TICKS_PER_MOVE = 2 * DEFAULT_TICKS_PER_MOVE;
    public static final int CASE_PER_MOVE = 2;
    public static final int EXIT_SNAP_DISTANCE = 3;

    private final GridPosition exit;

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

    public GridPosition getExit() {
        return exit;
    }

    @Override
    public void onSnap(SnapEvent event) {
        if (!canMove()) {
            return;
        }
        moveTowards(exit, getSnapDistance());
    }
}
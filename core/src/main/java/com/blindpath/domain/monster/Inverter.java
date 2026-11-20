package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

public class Inverter extends Monster {

    public static final int CASE_PER_MOVE = 1;
    public static final int INVERT_RANGE = 3;

    public Inverter(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, new ChaseStrategy(pathfinder), start);
    }

    public Inverter(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        super(grid, strategy, start);
        setCasePerMove(CASE_PER_MOVE);
    }

    public boolean invertsControls() {
        GridPosition target = getTarget();
        if (target == null) {
            return false;
        }
        int dc = Math.abs(target.getColumn() - getPosition().getColumn());
        int dr = Math.abs(target.getRow() - getPosition().getRow());
        return Math.max(dc, dr) <= INVERT_RANGE;
    }
}
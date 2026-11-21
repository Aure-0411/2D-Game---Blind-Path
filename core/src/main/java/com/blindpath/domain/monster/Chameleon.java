package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

public class Chameleon extends Monster {

    public static final int CASE_PER_MOVE = 1;
    public static final int REVEAL_RANGE = 2;

    public Chameleon(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, new ChaseStrategy(pathfinder), start);
    }

    public Chameleon(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        super(grid, strategy, start);
        setCasePerMove(CASE_PER_MOVE);
    }

    public boolean isRevealed() {
        GridPosition target = getTarget();
        if (target == null) {
            return false;
        }
        int dc = Math.abs(target.getColumn() - getPosition().getColumn());
        int dr = Math.abs(target.getRow() - getPosition().getRow());
        return Math.max(dc, dr) <= REVEAL_RANGE;
    }
}
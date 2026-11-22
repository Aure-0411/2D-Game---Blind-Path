package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.time.GameTimer;

public class Inverter extends Monster {

    public static final int CASE_PER_MOVE = 1;
    public static final int INVERT_RANGE = 3;
    public static final float INVERT_AFTER_EXIT_DURATION = 3f;

    private final GameTimer afterExitTimer = new GameTimer();
    private boolean wasInRange;

    public Inverter(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, new ChaseStrategy(pathfinder), start);
    }

    public Inverter(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        super(grid, strategy, start);
        setCasePerMove(CASE_PER_MOVE);
    }

    public boolean isInRange() {
        GridPosition target = getTarget();
        if (target == null) {
            return false;
        }
        int dc = Math.abs(target.getColumn() - getPosition().getColumn());
        int dr = Math.abs(target.getRow() - getPosition().getRow());
        return Math.max(dc, dr) <= INVERT_RANGE;
    }

    public boolean invertsControls() {
        return isInRange() || afterExitTimer.isRunning();
    }

    public boolean isAfterExitActive() {
        return !isInRange() && afterExitTimer.isRunning();
    }

    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);
        boolean inRange = isInRange();

        if (inRange) {
            afterExitTimer.stop();
        } 
        
        else if (wasInRange) {
            afterExitTimer.start(INVERT_AFTER_EXIT_DURATION);
        } 
        
        else {
            afterExitTimer.update(deltaTime);
        }
        wasInRange = inRange;
    }
    
}
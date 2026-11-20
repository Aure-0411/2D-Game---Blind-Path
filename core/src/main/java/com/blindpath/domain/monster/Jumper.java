package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

public class Jumper extends Monster {

    private final JumperStrategy jumperStrategy;

    public Jumper(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, start, new JumperStrategy(pathfinder));
    }

    public Jumper(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        super(grid, strategy, start);
        this.jumperStrategy = (strategy instanceof JumperStrategy js) ? js : null;
    }

    private Jumper(LabyrinthGrid grid, GridPosition start, JumperStrategy strategy) {
        super(grid, strategy, start);
        this.jumperStrategy = strategy;
    }

    public JumperState getState() {
        return jumperStrategy != null ? jumperStrategy.getState() : null;
    }
}
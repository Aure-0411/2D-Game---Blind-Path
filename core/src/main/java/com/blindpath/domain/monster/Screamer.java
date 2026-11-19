package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

public class Screamer extends Monster {

    public Screamer(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, new ChaseStrategy(pathfinder), start);
    }

    public Screamer(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        super(grid, strategy, start);
    }
}
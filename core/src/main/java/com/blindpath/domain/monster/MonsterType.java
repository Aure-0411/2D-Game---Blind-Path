package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

public enum MonsterType {
    JUMPER(Jumper::new),
    RUNAWAY(Runaway::new),
    SCREAMER(Screamer::new),
    CHAMELEON(Chameleon::new),
    INVERTER(Inverter::new);

    @FunctionalInterface
    private interface Constructor {
        Monster create(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder);
    }

    private final Constructor constructor;

    MonsterType(Constructor constructor) {
        this.constructor = constructor;
    }

    public Monster create(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        return constructor.create(grid, start, pathfinder);
    }
}
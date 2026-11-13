package com.blindpath.domain.monster;

import java.util.List;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapListener;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.time.TickListener;

public class Monster implements SnapListener, TickListener {
    public static final int DEFAULT_TICKS_PER_MOVE = 20;
    public static final int DEFAULT_SNAP_DISTANCE = 3;

    private final LabyrinthGrid grid;
    private final MovementStrategy strategy;
    private GridPosition position;
    private GridPosition target;                       // le gnome, peut être null
    private int ticksPerMove = DEFAULT_TICKS_PER_MOVE;
    private int snapDistance = DEFAULT_SNAP_DISTANCE;
    
    public Monster(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        if (grid == null || strategy == null || start == null)
            throw new IllegalArgumentException("null argument");
        this.grid = grid; 
        this.strategy = strategy; 
        this.position = start;
    }
    @Override
    public void onTick(long tick) {
        if (target != null && tick % ticksPerMove == 0) moveTowards(target, 1);
    }

    @Override
    public void onSnap(SnapEvent e) {
        GridPosition origin = new GridPosition(
            (int) Math.floor(e.getSourceX()), (int) Math.floor(e.getSourceY()));
        moveTowards(origin, snapDistance);
    }

    private void moveTowards(GridPosition goal, int steps) {
        for (int i = 0; i < steps; i++)
            position = strategy.nextStep(grid, position, goal);
    }

    public GridPosition getPosition() { return position; }
    public void setTarget(GridPosition t) { this.target = t; }
    public int getTicksPerMove() { return ticksPerMove; }
    public void setTicksPerMove(int v) {
        if (v <= 0) throw new IllegalArgumentException("ticksPerMove <= 0");
        ticksPerMove = v;
    }

    public int getSnapDistance() { return snapDistance; }
    public void setSnapDistance(int v) {
        if (v < 0) throw new IllegalArgumentException("snapDistance < 0");
        snapDistance = v;
    }
}
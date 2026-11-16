package com.blindpath.domain.monster;

import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapListener;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.time.TickListener;

/**
 * Classe de base des monstres. Possèdent tous : grid position, target,
 * tick rhythm et snap reaction. Children (Jumper, Screamer, ...)
 * override les méthodes par ceux dans leur comportement.
 */
public abstract class Monster implements SnapListener, TickListener {
    public static final int DEFAULT_TICKS_PER_MOVE = 30;
    public static final int DEFAULT_CASE_PER_MOVE = 1;
    public static final int DEFAULT_SNAP_DISTANCE = 1;

    private final LabyrinthGrid grid;
    private final MovementStrategy strategy;
    private GridPosition position;
    private GridPosition target; // le gnome, peut être null
    private int ticksPerMove = DEFAULT_TICKS_PER_MOVE;
    private int casePerMove = DEFAULT_CASE_PER_MOVE;
    private int snapDistance = DEFAULT_SNAP_DISTANCE;
    private boolean visible = false;
    
    protected Monster(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        if (grid == null || strategy == null || start == null)
            throw new IllegalArgumentException("null argument");
        this.grid = grid; 
        this.strategy = strategy; 
        this.position = start;
    }

    protected LabyrinthGrid getGrid() { return grid; }
    protected GridPosition getTarget() { return target; }

    public GridPosition getPosition() { return position; }
    public void setTarget(GridPosition t) { this.target = t; }

    public int getTicksPerMove() { return ticksPerMove; }
    public void setTicksPerMove(int v) {
        if (v <= 0) throw new IllegalArgumentException("ticksPerMove <= 0");
        ticksPerMove = v;
    }

    public int getCasePerMove() { return casePerMove; }
    public void setCasePerMove(int v) {
        if (v <= 0) throw new IllegalArgumentException("casePerMove <= 0");
        casePerMove = v;
    }

    public int getSnapDistance() { return snapDistance; }
    public void setSnapDistance(int v) {
        if (v < 0) throw new IllegalArgumentException("snapDistance < 0");
        snapDistance = v;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected void moveTowards(GridPosition goal, int steps) {
        for (int i = 0; i < steps; i++)
            position = strategy.nextStep(grid, position, goal);
    }

    @Override
    public void onTick(long tick) {
        if (target != null && tick % ticksPerMove == 0) moveTowards(target, casePerMove);
    }

    @Override
    public void onSnap(SnapEvent event) {
        GridPosition origin = new GridPosition(
            (int) Math.floor(event.getSourceX()), (int) Math.floor(event.getSourceY()));
        moveTowards(origin, snapDistance);
    }
}
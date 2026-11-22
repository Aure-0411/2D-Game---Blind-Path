package com.blindpath.domain.monster;

import com.blindpath.domain.labyrinth.OpeningOrientation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

class RunawayTest {

    /** Fake pathfinder: one cell toward the goal, columns first, then rows. */
    private static final Pathfinder STRAIGHT = (grid, start, target) -> {
        int dc = Integer.compare(target.getColumn(), start.getColumn());
        int dr = Integer.compare(target.getRow(), start.getRow());
        if (dc != 0) return List.of(new GridPosition(start.getColumn() + dc, start.getRow()));
        if (dr != 0) return List.of(new GridPosition(start.getColumn(), start.getRow() + dr));
        return List.of();
    };

    private static final int START_COL = 10;
    private static final int START_ROW = 10;
    private static final int SNAP_STEPS = Runaway.EXIT_SNAP_DISTANCE;
    private static final int CHASE_STEPS = 2;

    /** Exit block of the default map: the exit (19, 2) is on the right edge, so the 2x2 block starts at (18, 2). */
    private static final GridPosition EXIT = cell(18, 2);

    private LabyrinthGrid grid;
    private Runaway runaway;

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    private static SnapEvent snapAt(float x, float y, SnapSource source) {
        return new SnapEvent(x, y, source, 1L);
    }

    private static void tick(Monster m, int from, int to) {
        for (int t = from; t <= to; t++) {
            m.onTick(t);
        }
    }

    private static LabyrinthGrid gridWithExit(int exitColumn, int exitRow) {
        return new LabyrinthGrid(new Labyrinth(20, 20,
                new Entrance(0, 10, OpeningOrientation.VERTICAL), new Exit(exitColumn, exitRow, OpeningOrientation.VERTICAL), List.of()),
                Monster.SIZE);
    }

    @BeforeEach
    void setUp() {
        grid = gridWithExit(19, 2);
        runaway = new Runaway(grid, cell(START_COL, START_ROW), STRAIGHT);
    }

    // ---------- Configuration ----------

    @Test
    void runawayMovesTwoCellsEveryTwoMoves() {
        assertEquals(2 * Monster.DEFAULT_TICKS_PER_MOVE, runaway.getTicksPerMove(), "2 moves = 60 ticks");
        assertEquals(CHASE_STEPS, runaway.getCasePerMove(), "cells per move");
        assertEquals(SNAP_STEPS, runaway.getSnapDistance(), "cells toward the exit per snap");
    }

    @Test
    void exitIsReadFromTheLabyrinthAndAnchoredSoTheWholeBlockFitsInTheMap() {
        assertEquals(EXIT, runaway.getExit(), "the exit block must start one cell to the left");
    }

    @Test
    void exitFollowsTheLabyrinth() {
        Runaway r = new Runaway(gridWithExit(19, 5), cell(START_COL, START_ROW), STRAIGHT);
        assertEquals(cell(18, 5), r.getExit());
    }

    @Test
    void monsterTypeCreatesARunawayThatKnowsTheExit() {
        Monster m = MonsterType.RUNAWAY.create(grid, cell(START_COL, START_ROW), STRAIGHT);
        assertEquals(Runaway.class, m.getClass());
        assertEquals(EXIT, ((Runaway) m).getExit());
    }

    @Test
    void nullPathfinderIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Runaway(grid, cell(START_COL, START_ROW), null));
    }

    // ---------- Chase (ticks) ----------

    @Test
    void doesNotMoveBeforeSixtyTicks() {
        runaway.setTarget(cell(2, START_ROW));
        tick(runaway, 1, 59);
        assertEquals(cell(START_COL, START_ROW), runaway.getPosition());
    }

    @Test
    void chasesTheHeroTwoCellsAtTickSixty() {
        runaway.setTarget(cell(2, START_ROW));
        tick(runaway, 1, 60);
        assertEquals(cell(START_COL - CHASE_STEPS, START_ROW), runaway.getPosition(), "chases toward the hero");
    }

    // ---------- Snap -> exit ----------

    @Test
    void heroSnapMovesTowardTheExitNotTowardTheSnap() {
        runaway.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO));
        assertEquals(cell(START_COL + SNAP_STEPS, START_ROW), runaway.getPosition(), "moves toward the exit");
    }

    @Test
    void dynamiteSnapAlsoMovesTowardTheExit() {
        runaway.onSnap(snapAt(3.5f, 10.5f, SnapSource.DYNAMITE));
        assertEquals(cell(START_COL + SNAP_STEPS, START_ROW), runaway.getPosition());
    }

    @Test
    void snapDoesNotChangeTheChaseTarget() {
        runaway.setTarget(cell(2, START_ROW));
        runaway.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO));
        tick(runaway, 1, 60);
        assertEquals(cell(START_COL + SNAP_STEPS - CHASE_STEPS, START_ROW), runaway.getPosition(),
                "back toward the hero after the snap");
    }

    @Test
    void stopsOnTheExitInsteadOfGoingPastIt() {
        Runaway r = new Runaway(grid, cell(17, 2), STRAIGHT);
        r.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO));
        assertEquals(EXIT, r.getPosition(), "only 1 cell was left: it stops on the exit");
        r.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO));
        assertEquals(EXIT, r.getPosition(), "already on the exit: it stays there");
    }

    @Test
    void resumesTheChaseAfterReachingTheExit() {
        Runaway r = new Runaway(grid, cell(17, 2), STRAIGHT);
        r.setTarget(cell(10, 2));
        r.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO));
        tick(r, 1, 60);
        assertEquals(cell(EXIT.getColumn() - CHASE_STEPS, 2), r.getPosition(), "leaves the exit to chase the hero");
    }

    // ---------- Bear trap ----------

    @Test
    void immobilizedRunawayIgnoresSnap() {
        runaway.immobilize(3f);
        runaway.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO));
        assertEquals(cell(START_COL, START_ROW), runaway.getPosition());
    }

    @Test
    void immobilizedRunawayDoesNotChase() {
        runaway.setTarget(cell(2, START_ROW));
        runaway.immobilize(3f);
        tick(runaway, 1, 60);
        assertEquals(cell(START_COL, START_ROW), runaway.getPosition());
    }
}
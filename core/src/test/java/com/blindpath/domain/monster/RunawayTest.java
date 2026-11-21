package com.blindpath.domain.monster;

import com.blindpath.domain.labyrinth.OpeningOrientation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the Runaway (POC_MEDIUM-2, #64).
 *
 * <p>The exit is read from the labyrinth (LabyrinthGrid.getExitCell), so the Runaway keeps the
 * same constructor as the other monsters and works with MonsterType.</p>
 *
 * <p>Rules (Aurélien, 19/11): it chases the gnome by default, 2 cells every 2 moves
 * (60 ticks); on each snap (hero or dynamite) it moves 2 cells toward the EXIT,
 * not toward the snap. At the exit it simply stops, then resumes the chase on ticks.</p>
 *
 * <p>A fake pathfinder walks straight to the goal (columns first, then rows),
 * so the tests do not depend on the real pathfinding.</p>
 */
class RunawayTest {

    /** Fake pathfinder: one cell toward the goal, columns first, then rows. */
    private static final Pathfinder STRAIGHT = (grid, start, target) -> {
        int dc = Integer.compare(target.getColumn(), start.getColumn());
        int dr = Integer.compare(target.getRow(), start.getRow());
        if (dc != 0) return List.of(new GridPosition(start.getColumn() + dc, start.getRow()));
        if (dr != 0) return List.of(new GridPosition(start.getColumn(), start.getRow() + dr));
        return List.of();
    };

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

    /** Sends ticks {@code from..to} to the runaway. */
    private static void tick(Monster m, int from, int to) {
        for (int t = from; t <= to; t++) {
            m.onTick(t);
        }
    }

    /** Open 20x20 map whose exit is at (exitColumn, exitRow) on the right edge; 2x2 monster like in the game. */
    private static LabyrinthGrid gridWithExit(int exitColumn, int exitRow) {
        return new LabyrinthGrid(new Labyrinth(20, 20,
                new Entrance(0, 10, OpeningOrientation.VERTICAL), new Exit(exitColumn, exitRow, OpeningOrientation.VERTICAL), List.of()),
                Monster.SIZE);
    }

    @BeforeEach
    void setUp() {
        grid = gridWithExit(19, 2);
        runaway = new Runaway(grid, cell(10, 10), STRAIGHT);
    }

    // ---------- Configuration ----------

    @Test
    void runawayMovesTwoCellsEveryTwoMoves() {
        assertEquals(2 * Monster.DEFAULT_TICKS_PER_MOVE, runaway.getTicksPerMove(), "2 moves = 60 ticks");
        assertEquals(2, runaway.getCasePerMove(), "2 cells per move");
        assertEquals(2, runaway.getSnapDistance(), "2 cells toward the exit per snap");
    }

    @Test
    void exitIsReadFromTheLabyrinthAndAnchoredSoTheWholeBlockFitsInTheMap() {
        // The exit (19, 2) is on the right edge: a 2x2 block starting there would leave the map.
        assertEquals(EXIT, runaway.getExit(), "the exit block must start one cell to the left");
    }

    @Test
    void exitFollowsTheLabyrinth() {
        Runaway r = new Runaway(gridWithExit(19, 5), cell(10, 10), STRAIGHT);
        assertEquals(cell(18, 5), r.getExit());
    }

    @Test
    void monsterTypeCreatesARunawayThatKnowsTheExit() {
        Monster m = MonsterType.RUNAWAY.create(grid, cell(10, 10), STRAIGHT);
        assertEquals(Runaway.class, m.getClass());
        assertEquals(EXIT, ((Runaway) m).getExit());
    }

    @Test
    void nullPathfinderIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Runaway(grid, cell(10, 10), null));
    }

    // ---------- Chase (ticks) ----------

    @Test
    void doesNotMoveBeforeSixtyTicks() {
        runaway.setTarget(cell(2, 10));
        tick(runaway, 1, 59);
        assertEquals(cell(10, 10), runaway.getPosition());
    }

    @Test
    void chasesTheGnomeTwoCellsAtTickSixty() {
        runaway.setTarget(cell(2, 10));
        tick(runaway, 1, 60);
        assertEquals(cell(8, 10), runaway.getPosition(), "2 cells toward the gnome");
    }

    // ---------- Snap -> exit ----------

    @Test
    void heroSnapMovesTwoCellsTowardTheExitNotTowardTheSnap() {
        // The snap comes from the LEFT, the exit is on the RIGHT.
        runaway.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO));
        assertEquals(cell(12, 10), runaway.getPosition(), "2 cells toward the exit");
    }

    @Test
    void dynamiteSnapAlsoMovesTowardTheExit() {
        runaway.onSnap(snapAt(3.5f, 10.5f, SnapSource.DYNAMITE));
        assertEquals(cell(12, 10), runaway.getPosition());
    }

    @Test
    void snapDoesNotChangeTheChaseTarget() {
        runaway.setTarget(cell(2, 10));
        runaway.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO)); // -> (12, 10)
        tick(runaway, 1, 60);
        assertEquals(cell(10, 10), runaway.getPosition(), "back toward the gnome after the snap");
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
        r.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO)); // -> exit (18, 2)
        tick(r, 1, 60);
        assertEquals(cell(16, 2), r.getPosition(), "leaves the exit to chase the gnome");
    }

    // ---------- Bear trap ----------

    @Test
    void immobilizedRunawayIgnoresSnap() {
        runaway.immobilize(3f);
        runaway.onSnap(snapAt(3.5f, 10.5f, SnapSource.HERO));
        assertEquals(cell(10, 10), runaway.getPosition());
    }

    @Test
    void immobilizedRunawayDoesNotChase() {
        runaway.setTarget(cell(2, 10));
        runaway.immobilize(3f);
        tick(runaway, 1, 60);
        assertEquals(cell(10, 10), runaway.getPosition());
    }
}

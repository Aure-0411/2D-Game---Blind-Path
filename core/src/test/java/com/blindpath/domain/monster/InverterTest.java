package com.blindpath.domain.monster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

/**
 * Tests of the Inverter (POC_EXTREME-3, #82).
 *
 * <p>Rules: it chases the hero, CASE_PER_MOVE cells per move, and inverts the hero's
 * movement controls while the hero is within INVERT_RANGE cells. The tests read the
 * constants, so the values can be tuned without touching them. Distance = square around the
 * monster: the largest of the column gap and the row gap.</p>
 */
class InverterTest {

    /** Fake pathfinder: one cell toward the goal, columns first, then rows. */
    private static final Pathfinder STRAIGHT = (grid, start, target) -> {
        int dc = Integer.compare(target.getColumn(), start.getColumn());
        int dr = Integer.compare(target.getRow(), start.getRow());
        if (dc != 0) return List.of(new GridPosition(start.getColumn() + dc, start.getRow()));
        if (dr != 0) return List.of(new GridPosition(start.getColumn(), start.getRow() + dr));
        return List.of();
    };

    private LabyrinthGrid grid;
    private Inverter inverter;

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    @BeforeEach
    void setUp() {
        grid = new LabyrinthGrid(new Labyrinth(20, 20,
                new Entrance(0, 10, OpeningOrientation.VERTICAL), new Exit(19, 2, OpeningOrientation.VERTICAL), List.of()),
                Monster.SIZE);
        inverter = new Inverter(grid, cell(10, 10), STRAIGHT);
    }

    // ---------- Configuration ----------

    private static final int R = Inverter.INVERT_RANGE;

    @Test
    void movesCasePerMoveCellsPerMove() {
        assertEquals(Inverter.CASE_PER_MOVE, inverter.getCasePerMove());
        assertEquals(Monster.DEFAULT_TICKS_PER_MOVE, inverter.getTicksPerMove(), "default rhythm");
    }

    @Test
    void chasesCasePerMoveCellsOnAMoveTick() {
        inverter.setTarget(cell(18, 10));
        inverter.onTick(Monster.DEFAULT_TICKS_PER_MOVE);
        assertEquals(cell(10 + Inverter.CASE_PER_MOVE, 10), inverter.getPosition());
    }

    // ---------- Inversion range ----------

    @Test
    void noTargetNoInversion() {
        assertFalse(inverter.invertsControls(), "the hero is not known yet");
    }

    @Test
    void heroOnTheMonsterInverts() {
        inverter.setTarget(cell(10, 10));
        assertTrue(inverter.invertsControls());
    }

    @Test
    void exactlyAtTheRangeInverts() {
        inverter.setTarget(cell(10 + R, 10));
        assertTrue(inverter.invertsControls(), "INVERT_RANGE cells to the right: inside");
        inverter.setTarget(cell(10, 10 - R));
        assertTrue(inverter.invertsControls(), "INVERT_RANGE cells down: inside");
    }

    @Test
    void oneCellBeyondTheRangeDoesNotInvert() {
        inverter.setTarget(cell(10 + R + 1, 10));
        assertFalse(inverter.invertsControls(), "one cell too far to the right: outside");
        inverter.setTarget(cell(10 - R - 1, 10));
        assertFalse(inverter.invertsControls(), "one cell too far to the left: outside");
    }

    @Test
    void diagonalCornerOfTheSquareInverts() {
        inverter.setTarget(cell(10 + R, 10 + R));
        assertTrue(inverter.invertsControls(), "corner of the square: inside");
    }

    @Test
    void oneGapTooBigIsEnoughToBeOutside() {
        inverter.setTarget(cell(10 + R - 1, 10 + R + 1));
        assertFalse(inverter.invertsControls(), "close in columns but too far in rows: outside");
    }

    @Test
    void followsTheGnomeWhenTheTargetChanges() {
        inverter.setTarget(cell(10 + R, 10));
        assertTrue(inverter.invertsControls());
        inverter.setTarget(cell(18, 10));
        assertFalse(inverter.invertsControls(), "the hero ran away");
    }

    // ---------- Factory ----------

    @Test
    void monsterTypeCreatesAnInverter() {
        Monster m = MonsterType.INVERTER.create(grid, cell(10, 10), STRAIGHT);
        assertInstanceOf(Inverter.class, m);
        assertEquals(Inverter.CASE_PER_MOVE, m.getCasePerMove());
    }
}

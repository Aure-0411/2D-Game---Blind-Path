package com.blindpath.domain.monster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the Inverter (POC_EXTREME-3, #82).
 *
 * <p>Rules (GDD 4.2): it chases the gnome, 2 cells per move, and inverts the gnome's
 * movement controls while the gnome is within 4 cells. Distance = square around the
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
                new Entrance(0, 10, Border.LEFT), new Exit(19, 2, Border.RIGHT), List.of()),
                Monster.SIZE);
        inverter = new Inverter(grid, cell(10, 10), STRAIGHT);
    }

    // ---------- Configuration ----------

    @Test
    void movesTwoCellsPerMove() {
        assertEquals(2, inverter.getCasePerMove());
        assertEquals(Monster.DEFAULT_TICKS_PER_MOVE, inverter.getTicksPerMove(), "default rhythm");
    }

    @Test
    void chasesTwoCellsOnAMoveTick() {
        inverter.setTarget(cell(15, 10));
        inverter.onTick(Monster.DEFAULT_TICKS_PER_MOVE);
        assertEquals(cell(12, 10), inverter.getPosition());
    }

    // ---------- Inversion range ----------

    @Test
    void noTargetNoInversion() {
        assertFalse(inverter.invertsControls(), "the gnome is not known yet");
    }

    @Test
    void gnomeOnTheMonsterInverts() {
        inverter.setTarget(cell(10, 10));
        assertTrue(inverter.invertsControls());
    }

    @Test
    void exactlyFourCellsAwayInverts() {
        inverter.setTarget(cell(14, 10));
        assertTrue(inverter.invertsControls(), "4 cells to the right: inside");
        inverter.setTarget(cell(10, 6));
        assertTrue(inverter.invertsControls(), "4 cells down: inside");
    }

    @Test
    void fiveCellsAwayDoesNotInvert() {
        inverter.setTarget(cell(15, 10));
        assertFalse(inverter.invertsControls(), "5 cells to the right: outside");
        inverter.setTarget(cell(5, 10));
        assertFalse(inverter.invertsControls(), "5 cells to the left: outside");
    }

    @Test
    void diagonalCornerOfTheSquareInverts() {
        inverter.setTarget(cell(14, 14));
        assertTrue(inverter.invertsControls(), "4 right and 4 up: corner of the square, inside");
    }

    @Test
    void oneGapTooBigIsEnoughToBeOutside() {
        inverter.setTarget(cell(13, 15));
        assertFalse(inverter.invertsControls(), "3 right but 5 up: outside");
    }

    @Test
    void followsTheGnomeWhenTheTargetChanges() {
        inverter.setTarget(cell(12, 10));
        assertTrue(inverter.invertsControls());
        inverter.setTarget(cell(18, 10));
        assertFalse(inverter.invertsControls(), "the gnome ran away");
    }

    // ---------- Factory ----------

    @Test
    void monsterTypeCreatesAnInverter() {
        Monster m = MonsterType.INVERTER.create(grid, cell(10, 10), STRAIGHT);
        assertInstanceOf(Inverter.class, m);
        assertEquals(2, m.getCasePerMove());
    }
}

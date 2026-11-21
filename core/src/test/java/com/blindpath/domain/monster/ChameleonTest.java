package com.blindpath.domain.monster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.labyrinth.OpeningOrientation;
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
 * Tests of the Chameleon (POC_EXTREME-2, #81).
 *
 * <p>Rules: it chases the gnome, CASE_PER_MOVE cells per move, and is invisible except when the gnome
 * is within REVEAL_RANGE cells. The tests read the
 * constants, so the values can be tuned without touching them. Distance = square around the
 * monster: the largest of the column gap and the row gap.</p>
 */
class ChameleonTest {

    /** Fake pathfinder: one cell toward the goal, columns first, then rows. */
    private static final Pathfinder STRAIGHT = (grid, start, target) -> {
        int dc = Integer.compare(target.getColumn(), start.getColumn());
        int dr = Integer.compare(target.getRow(), start.getRow());
        if (dc != 0) return List.of(new GridPosition(start.getColumn() + dc, start.getRow()));
        if (dr != 0) return List.of(new GridPosition(start.getColumn(), start.getRow() + dr));
        return List.of();
    };

    private LabyrinthGrid grid;
    private Chameleon chameleon;

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    @BeforeEach
    void setUp() {
        grid = new LabyrinthGrid(new Labyrinth(20, 20,
                new Entrance(0, 10, OpeningOrientation.VERTICAL), new Exit(19, 2, OpeningOrientation.VERTICAL), List.of()),
                Monster.SIZE);
        chameleon = new Chameleon(grid, cell(10, 10), STRAIGHT);
    }

    // ---------- Configuration ----------

    private static final int R = Chameleon.REVEAL_RANGE;

    @Test
    void movesCasePerMoveCellsPerMove() {
        assertEquals(Chameleon.CASE_PER_MOVE, chameleon.getCasePerMove());
        assertEquals(Monster.DEFAULT_TICKS_PER_MOVE, chameleon.getTicksPerMove(), "default rhythm");
    }

    @Test
    void chasesCasePerMoveCellsOnAMoveTick() {
        chameleon.setTarget(cell(18, 10));
        chameleon.onTick(Monster.DEFAULT_TICKS_PER_MOVE);
        assertEquals(cell(10 + Chameleon.CASE_PER_MOVE, 10), chameleon.getPosition());
    }

    // ---------- Inversion range ----------

    @Test
    void noTargetHidden() {
        assertFalse(chameleon.isRevealed(), "the gnome is not known yet: hidden");
    }

    @Test
    void gnomeOnTheMonsterIsRevealed() {
        chameleon.setTarget(cell(10, 10));
        assertTrue(chameleon.isRevealed());
    }

    @Test
    void exactlyAtTheRangeIsRevealed() {
        chameleon.setTarget(cell(10 + R, 10));
        assertTrue(chameleon.isRevealed(), "REVEAL_RANGE cells to the right: inside");
        chameleon.setTarget(cell(10, 10 - R));
        assertTrue(chameleon.isRevealed(), "REVEAL_RANGE cells down: inside");
    }

    @Test
    void oneCellBeyondTheRangeStaysHidden() {
        chameleon.setTarget(cell(10 + R + 1, 10));
        assertFalse(chameleon.isRevealed(), "one cell too far to the right: outside");
        chameleon.setTarget(cell(10 - R - 1, 10));
        assertFalse(chameleon.isRevealed(), "one cell too far to the left: outside");
    }

    @Test
    void diagonalCornerOfTheSquareIsRevealed() {
        chameleon.setTarget(cell(10 + R, 10 + R));
        assertTrue(chameleon.isRevealed(), "corner of the square: inside");
    }

    @Test
    void oneGapTooBigIsEnoughToBeOutside() {
        chameleon.setTarget(cell(10 + R - 1, 10 + R + 1));
        assertFalse(chameleon.isRevealed(), "close in columns but too far in rows: outside");
    }

    @Test
    void followsTheGnomeWhenTheTargetChanges() {
        chameleon.setTarget(cell(10 + R, 10));
        assertTrue(chameleon.isRevealed());
        chameleon.setTarget(cell(18, 10));
        assertFalse(chameleon.isRevealed(), "the gnome ran away");
    }

    // ---------- Factory ----------

    @Test
    void monsterTypeCreatesAChameleon() {
        Monster m = MonsterType.CHAMELEON.create(grid, cell(10, 10), STRAIGHT);
        assertInstanceOf(Chameleon.class, m);
        assertEquals(Chameleon.CASE_PER_MOVE, m.getCasePerMove());
    }
}

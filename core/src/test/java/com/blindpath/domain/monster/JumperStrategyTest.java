package com.blindpath.domain.monster;

import com.blindpath.domain.labyrinth.OpeningOrientation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.labyrinth.Wall;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link JumperStrategy} (POC_EASY-17): the Jumper chases the hero,
 * raises an alert when the hero is within range, then jumps onto the hero's cell.
 *
 * <p>The chase uses a fake pathfinder that moves one column towards the target,
 * so the tests do not depend on gdx-ai.</p>
 */
class JumperStrategyTest {

    /** Fake pathfinder: one column towards the target, empty path when already on it. */
    private static final Pathfinder ONE_COLUMN = (grid, start, target) -> {
        int step = Integer.signum(target.getColumn() - start.getColumn());
        if (step == 0) {
            return List.of();
        }
        return List.of(new GridPosition(start.getColumn() + step, start.getRow()));
    };

    private LabyrinthGrid openGrid;
    private JumperStrategy strategy;

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    private static LabyrinthGrid gridWith(List<Obstacle> walls) {
        return new LabyrinthGrid(new Labyrinth(10, 10,
                new Entrance(0, 2, OpeningOrientation.VERTICAL), new Exit(9, 2, OpeningOrientation.VERTICAL), walls));
    }

    @BeforeEach
    void setUp() {
        openGrid = gridWith(List.of());
        strategy = new JumperStrategy(ONE_COLUMN);
    }

    // ---------- Default values ----------

    @Test
    void hasDefaultValues() {
        assertEquals(2, JumperStrategy.DEFAULT_JUMP_RANGE, "default jump range");
        assertEquals(2, strategy.getJumpRange(), "the default constructor must use the default range");
        assertEquals(JumperState.CHASING, strategy.getState(), "a new Jumper must be chasing");
    }

    // ---------- Chase ----------

    @Test
    void chasesHeroWhenOutOfRange() {
        GridPosition next = strategy.nextStep(openGrid, cell(1, 6), cell(6, 6));
        assertEquals(cell(2, 6), next, "hero 5 cells away: the Jumper takes one step towards him");
        assertEquals(JumperState.CHASING, strategy.getState(), "no alert when the hero is out of range");
    }

    @Test
    void staysInPlaceWhenNoPathExists() {
        Pathfinder noPath = (grid, start, target) -> List.of();
        JumperStrategy blocked = new JumperStrategy(noPath);
        assertEquals(cell(1, 6), blocked.nextStep(openGrid, cell(1, 6), cell(6, 6)),
                "no path: the Jumper stays on its cell");
    }

    @Test
    void noAlertWhenAlreadyOnHeroCell() {
        GridPosition next = strategy.nextStep(openGrid, cell(4, 6), cell(4, 6));
        assertEquals(cell(4, 6), next, "already on the hero cell: the Jumper does not move");
        assertEquals(JumperState.CHASING, strategy.getState(), "no alert at distance 0");
    }

    // ---------- Alert ----------

    @Test
    void raisesAlertWhenHeroIsWithinRange() {
        GridPosition next = strategy.nextStep(openGrid, cell(2, 6), cell(4, 6));
        assertEquals(cell(2, 6), next, "during the alert the Jumper does not move");
        assertEquals(JumperState.ALERT, strategy.getState(), "hero 2 cells away: the Jumper must raise an alert");
    }

    @Test
    void raisesAlertWhenHeroIsAdjacent() {
        strategy.nextStep(openGrid, cell(2, 6), cell(3, 6));
        assertEquals(JumperState.ALERT, strategy.getState(), "hero 1 cell away: alert");
    }

    @Test
    void rangeUsesColumnsAndRows() {
        strategy.nextStep(openGrid, cell(2, 6), cell(3, 7));
        assertEquals(JumperState.ALERT, strategy.getState(), "1 column + 1 row = 2 cells: alert");

        JumperStrategy other = new JumperStrategy(ONE_COLUMN);
        other.nextStep(openGrid, cell(2, 6), cell(4, 7));
        assertEquals(JumperState.CHASING, other.getState(), "2 columns + 1 row = 3 cells: no alert");
    }

    @Test
    void customRangeIsUsed() {
        JumperStrategy longRange = new JumperStrategy(ONE_COLUMN, 3);
        assertEquals(3, longRange.getJumpRange(), "custom range");
        longRange.nextStep(openGrid, cell(1, 6), cell(4, 6));
        assertEquals(JumperState.ALERT, longRange.getState(), "range 3, hero 3 cells away: alert");
    }

    // ---------- Jump ----------

    @Test
    void jumpsOnHeroCellAfterAlert() {
        strategy.nextStep(openGrid, cell(2, 6), cell(4, 6));
        GridPosition next = strategy.nextStep(openGrid, cell(2, 6), cell(4, 6));
        assertEquals(cell(4, 6), next, "after the alert the Jumper must land on the hero cell");
        assertEquals(JumperState.CHASING, strategy.getState(), "back to chasing after the jump");
    }

    @Test
    void jumpsOnTheNewHeroCellIfHeroMovedButStayedInRange() {
        strategy.nextStep(openGrid, cell(2, 6), cell(4, 6));
        GridPosition next = strategy.nextStep(openGrid, cell(2, 6), cell(3, 6));
        assertEquals(cell(3, 6), next, "the jump targets the hero's current cell");
    }

    @Test
    void doesNotJumpIfHeroLeftTheRange() {
        strategy.nextStep(openGrid, cell(2, 6), cell(4, 6));
        GridPosition next = strategy.nextStep(openGrid, cell(2, 6), cell(7, 6));
        assertEquals(cell(3, 6), next, "hero escaped during the alert: the Jumper only takes one step");
        assertEquals(JumperState.CHASING, strategy.getState(), "back to chasing when the hero escaped");
    }

    @Test
    void jumpGoesOverAWall() {
        LabyrinthGrid walled = gridWith(List.of(new Wall(3, 5, 1, 3)));
        strategy.nextStep(walled, cell(2, 6), cell(4, 6));
        assertEquals(cell(4, 6), strategy.nextStep(walled, cell(2, 6), cell(4, 6)),
                "the jump goes over the wall on column 3");
    }

    @Test
    void doesNotJumpOnAWallCell() {
        LabyrinthGrid walled = gridWith(List.of(new Wall(4, 5, 1, 3)));
        strategy.nextStep(walled, cell(2, 6), cell(4, 6));
        assertEquals(cell(3, 6), strategy.nextStep(walled, cell(2, 6), cell(4, 6)),
                "target cell is a wall: no jump, the Jumper only takes one step");
    }

    // ---------- With a real Monster and ticks ----------

    @Test
    void jumperAlertsThenJumpsOnTicks() {
        int move = Monster.DEFAULT_TICKS_PER_MOVE;
        Jumper jumper = new Jumper(openGrid, cell(2, 6), ONE_COLUMN);
        jumper.setTarget(cell(4, 6));
        assertEquals(JumperState.CHASING, jumper.getState(), "a new Jumper must be chasing");

        for (int t = 1; t <= move; t++) {
            jumper.onTick(t);
        }
        assertEquals(cell(2, 6), jumper.getPosition(), "first move: alert, the Jumper stays");
        assertEquals(JumperState.ALERT, jumper.getState(), "alert after the first move");

        for (int t = move + 1; t <= 2 * move; t++) {
            jumper.onTick(t);
        }
        assertEquals(cell(4, 6), jumper.getPosition(), "second move: the Jumper lands on the hero");
        assertEquals(JumperState.CHASING, jumper.getState(), "back to chasing after the jump");
    }

    // ---------- Invalid arguments ----------

    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new JumperStrategy(null), "null pathfinder");
        assertThrows(IllegalArgumentException.class, () -> new JumperStrategy(ONE_COLUMN, 0), "range 0");
    }
}
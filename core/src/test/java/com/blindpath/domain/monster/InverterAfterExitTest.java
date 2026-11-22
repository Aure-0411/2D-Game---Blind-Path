package com.blindpath.domain.monster;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the Inverter after-exit effect (POC_EXTREME-3, #82): the controls stay inverted
 * INVERT_AFTER_EXIT_DURATION seconds after the hero leaves the range.
 */
class InverterAfterExitTest {

    private static final float AFTER_EXIT = Inverter.INVERT_AFTER_EXIT_DURATION;
    private static final GridPosition INSIDE = new GridPosition(10, 10);
    private static final GridPosition OUTSIDE = new GridPosition(10 + Inverter.INVERT_RANGE + 1, 10);

    private Inverter inverter;

    @BeforeEach
    void setUp() {
        LabyrinthGrid grid = new LabyrinthGrid(new Labyrinth(20, 20,
                new Entrance(0, 10, OpeningOrientation.VERTICAL),
                new Exit(19, 2, OpeningOrientation.VERTICAL), List.of()),
                Monster.SIZE);
        // The monster never moves: the tests only check the timer.
        inverter = new Inverter(grid, (g, position, goal) -> position, new GridPosition(10, 10));
    }

    private void enterThenLeave() {
        inverter.setTarget(INSIDE);
        inverter.update(0.1f);
        inverter.setTarget(OUTSIDE);
        inverter.update(0.1f);
    }

    @Test
    void stillInvertedRightAfterLeaving() {
        enterThenLeave();
        assertTrue(inverter.invertsControls(), "the effect continues after leaving the range");
    }

    @Test
    void stillInvertedJustBeforeTheEnd() {
        enterThenLeave();
        inverter.update(AFTER_EXIT - 0.1f);
        assertTrue(inverter.invertsControls());
    }

    @Test
    void backToNormalAfterTheAfterExitDuration() {
        enterThenLeave();
        inverter.update(AFTER_EXIT + 0.1f);
        assertFalse(inverter.invertsControls(), "the effect ends after INVERT_AFTER_EXIT_DURATION");
    }

    @Test
    void neverInvertedIfNeverInRange() {
        inverter.setTarget(OUTSIDE);
        inverter.update(0.1f);
        assertFalse(inverter.invertsControls());
    }

    @Test
    void comingBackRestartsAFullDelayOnTheNextExit() {
        enterThenLeave();
        inverter.update(AFTER_EXIT - 0.5f);
        inverter.setTarget(INSIDE);
        inverter.update(0.1f);
        inverter.setTarget(OUTSIDE);
        inverter.update(0.1f);
        inverter.update(AFTER_EXIT - 0.5f);
        assertTrue(inverter.invertsControls(), "a new exit gives a full new delay");
    }

    @Test
    void afterExitIsActiveOnlyOutsideWhileTheTimerRuns() {
        inverter.setTarget(INSIDE);
        inverter.update(0.1f);
        assertFalse(inverter.isAfterExitActive(), "inside the range: not the after-exit phase");
        inverter.setTarget(OUTSIDE);
        inverter.update(0.1f);
        assertTrue(inverter.isAfterExitActive(), "just left: after-exit phase");
        inverter.update(AFTER_EXIT + 0.1f);
        assertFalse(inverter.isAfterExitActive(), "timer over");
    }
}

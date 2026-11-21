package com.blindpath.domain.monster;

import com.blindpath.domain.labyrinth.OpeningOrientation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the Screamer (POC_HARD-2, #73).
 *
 * <p>Rules (GDD 4.2): it chases the hero like the other monsters. It counts the snaps
 * (hero and dynamite). When the count reaches a threshold drawn between 5 and 8, it screams:
 * the scream stuns the hero (infinite range), the counter goes back to 0 and a new
 * threshold is drawn.</p>
 *
 * <p>A fake Random returns chosen values, so the thresholds are known in advance.</p>
 */
class ScreamerTest {

    /** Fake pathfinder: one cell toward the goal, columns first, then rows. */
    private static final Pathfinder STRAIGHT = (grid, start, target) -> {
        int dc = Integer.compare(target.getColumn(), start.getColumn());
        int dr = Integer.compare(target.getRow(), start.getRow());
        if (dc != 0) return List.of(new GridPosition(start.getColumn() + dc, start.getRow()));
        if (dr != 0) return List.of(new GridPosition(start.getColumn(), start.getRow() + dr));
        return List.of();
    };

    /** Fake Random: nextInt returns the given values in order (the last one is repeated). */
    private static class FixedRandom extends Random {
        private final Deque<Integer> values = new ArrayDeque<>();
        private int last;

        FixedRandom(int... values) {
            for (int v : values) this.values.add(v);
        }

        @Override
        public int nextInt(int bound) {
            if (!values.isEmpty()) last = values.poll();
            if (last < 0 || last >= bound) throw new IllegalStateException("value out of bound");
            return last;
        }
    }

    private LabyrinthGrid grid;

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    private static SnapEvent heroSnapAt(float x, float y) {
        return new SnapEvent(x, y, SnapSource.HERO, 1L);
    }

    /** Sends {@code count} snaps at (10.5, 10.5), the cell where the screamer stands. */
    private static void snap(Screamer s, int count) {
        for (int i = 0; i < count; i++) s.onSnap(heroSnapAt(10.5f, 10.5f));
    }

    private Screamer screamer(int... randomValues) {
        return new Screamer(grid, new ChaseStrategy(STRAIGHT), cell(10, 10), new FixedRandom(randomValues));
    }

    @BeforeEach
    void setUp() {
        grid = new LabyrinthGrid(new Labyrinth(20, 20,
                new Entrance(0, 10, OpeningOrientation.VERTICAL), new Exit(19, 2, OpeningOrientation.VERTICAL), List.of()),
                Monster.SIZE);
    }

    // ---------- Threshold ----------

    @Test
    void thresholdIsMinSnapsPlusTheRandomValue() {
        assertEquals(5, screamer(0).getScreamThreshold(), "nextInt = 0 -> 5 snaps");
        assertEquals(6, screamer(1).getScreamThreshold(), "nextInt = 1 -> 6 snaps");
        assertEquals(8, screamer(3).getScreamThreshold(), "nextInt = 3 -> 8 snaps");
    }

    @Test
    void thresholdIsAlwaysBetweenFiveAndEightWithARealRandom() {
        for (long seed = 0; seed < 200; seed++) {
            Screamer s = new Screamer(grid, new ChaseStrategy(STRAIGHT), cell(10, 10), new Random(seed));
            int threshold = s.getScreamThreshold();
            assertTrue(threshold >= Screamer.MIN_SNAPS && threshold <= Screamer.MAX_SNAPS,
                    "threshold " + threshold + " out of [5, 8]");
        }
    }

    @Test
    void nullRandomIsRefused() {
        assertThrows(IllegalArgumentException.class,
                () -> new Screamer(grid, new ChaseStrategy(STRAIGHT), cell(10, 10), null));
    }

    // ---------- Counting and screaming ----------

    @Test
    void eachSnapIsCounted() {
        Screamer s = screamer(3); // threshold 8
        snap(s, 3);
        assertEquals(3, s.getSnapCount());
    }

    @Test
    void noScreamBeforeTheThreshold() {
        Screamer s = screamer(1); // threshold 6
        snap(s, 5);
        assertFalse(s.consumeScream(), "5 snaps < 6: no scream yet");
    }

    @Test
    void screamsExactlyOnTheThresholdSnap() {
        Screamer s = screamer(1); // threshold 6
        snap(s, 6);
        assertTrue(s.consumeScream(), "6th snap: scream");
    }

    @Test
    void counterGoesBackToZeroAfterTheScream() {
        Screamer s = screamer(1);
        snap(s, 6);
        assertEquals(0, s.getSnapCount());
    }

    @Test
    void aNewThresholdIsDrawnAfterEachScream() {
        Screamer s = screamer(1, 3); // first threshold 6, then 8
        snap(s, 6);
        assertEquals(8, s.getScreamThreshold(), "second threshold drawn after the scream");
    }

    @Test
    void secondScreamUsesTheNewThreshold() {
        Screamer s = screamer(0, 2); // 5 then 7
        snap(s, 5);
        assertTrue(s.consumeScream(), "first scream on the 5th snap");
        snap(s, 6);
        assertFalse(s.consumeScream(), "6 snaps < 7: no second scream yet");
        snap(s, 1);
        assertTrue(s.consumeScream(), "second scream on the 7th snap");
    }

    @Test
    void screamIsConsumedOnlyOnce() {
        Screamer s = screamer(0);
        snap(s, 5);
        assertTrue(s.consumeScream());
        assertFalse(s.consumeScream(), "the same scream must not stun twice");
    }

    @Test
    void dynamiteSnapsCountToo() {
        Screamer s = screamer(0); // threshold 5
        for (int i = 0; i < 5; i++) s.onSnap(new SnapEvent(10.5f, 10.5f, SnapSource.DYNAMITE, 1L));
        assertTrue(s.consumeScream(), "dynamite snaps count like hero snaps");
    }

    @Test
    void snapsAreCountedEvenWhenImmobilized() {
        Screamer s = screamer(0); // threshold 5
        s.immobilize(10f);
        snap(s, 5);
        assertTrue(s.consumeScream(), "immobilized: it cannot move but it still hears");
    }

    // ---------- Movement ----------

    @Test
    void stillMovesTowardTheSnapLikeTheOtherMonsters() {
        Screamer s = screamer(3);
        s.onSnap(heroSnapAt(15.5f, 10.5f));
        assertEquals(cell(11, 10), s.getPosition(), "1 cell toward the snap (default snap distance)");
    }

    @Test
    void chasesTheGnomeOnTicks() {
        Screamer s = screamer(3);
        s.setTarget(cell(15, 10));
        s.onTick(Monster.DEFAULT_TICKS_PER_MOVE);
        assertEquals(cell(11, 10), s.getPosition(), "default rhythm: 1 cell every 30 ticks");
    }

    // ---------- Factory ----------

    @Test
    void monsterTypeCreatesAScreamer() {
        Monster m = MonsterType.SCREAMER.create(grid, cell(10, 10), STRAIGHT);
        assertInstanceOf(Screamer.class, m);
        int threshold = ((Screamer) m).getScreamThreshold();
        assertTrue(threshold >= 5 && threshold <= 8);
    }
}
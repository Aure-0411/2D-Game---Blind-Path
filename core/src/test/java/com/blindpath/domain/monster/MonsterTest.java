package com.blindpath.domain.monster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Wall;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.time.GameClock;
import com.blindpath.infrastructure.pathfinding.GdxAiPathfinder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the abstract {@link Monster} (through a test subclass) and {@link ChaseStrategy}
 * on a 10 x 10 map with a wall on column 3, rows 0 to 4. The monster starts on (0, 6).
 * Defaults: 30 ticks per move, 1 cell per move, 1 cell per snap.
 */
class MonsterTest {

    /** Minimal concrete monster, only used to test the abstract base class. */
    private static class TestMonster extends Monster {
        TestMonster(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
            super(grid, strategy, start);
        }
    }

    private static final int TICKS = 30;
    private static final int CASES = 1;
    private static final int SNAP_CASES = 1;
    private static final int START_ROW = 6;

    private LabyrinthGrid grid;
    private Monster monster;

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    private static SnapEvent snapAt(float x, float y) {
        return new SnapEvent(x, y, SnapSource.HERO, 1L);
    }

    private void tick(Monster m, int from, int to) {
        for (int t = from; t <= to; t++) {
            m.onTick(t);
        }
    }

    @BeforeEach
    void setUp() {
        grid = new LabyrinthGrid(new Labyrinth(10, 10,
                new Entrance(0, 2, Border.LEFT), new Exit(9, 2, Border.RIGHT),
                List.of(new Wall(3, 0, 1, 5))));
        monster = new TestMonster(grid, new ChaseStrategy(new GdxAiPathfinder()), cell(0, START_ROW));
    }

    @Test
    void hasDefaultValues() {
        assertEquals(TICKS, Monster.DEFAULT_TICKS_PER_MOVE);
        assertEquals(CASES, Monster.DEFAULT_CASE_PER_MOVE);
        assertEquals(SNAP_CASES, Monster.DEFAULT_SNAP_DISTANCE);
        assertEquals(TICKS, monster.getTicksPerMove());
        assertEquals(CASES, monster.getCasePerMove());
        assertEquals(SNAP_CASES, monster.getSnapDistance());
        assertEquals(cell(0, START_ROW), monster.getPosition());
    }

    @Test
    void doesNotMoveBeforeNTicks() {
        monster.setTarget(cell(0, 0));
        tick(monster, 1, TICKS - 1);
        assertEquals(cell(0, START_ROW), monster.getPosition());
    }

    @Test
    void movesOneMoveAtNTick() {
        monster.setTarget(cell(0, 0));
        tick(monster, 1, TICKS);
        assertEquals(cell(0, START_ROW - CASES), monster.getPosition());
    }

    @Test
    void movesThreeMovesAfterThreeTimesNTicks() {
        monster.setTarget(cell(0, 0));
        tick(monster, 1, TICKS * 3);
        assertEquals(cell(0, START_ROW - CASES * 3), monster.getPosition());
    }

    @Test
    void doesNotMoveWithoutTarget() {
        tick(monster, 1, TICKS * 5);
        assertEquals(cell(0, START_ROW), monster.getPosition());
    }

    @Test
    void followsTheGnomeWhenItMoves() {
        monster.setTarget(cell(0, 0));
        tick(monster, 1, TICKS);
        assertEquals(cell(0, START_ROW - CASES), monster.getPosition());
        monster.setTarget(cell(0, 9));
        tick(monster, TICKS + 1, TICKS * 2);
        assertEquals(cell(0, START_ROW), monster.getPosition(), "turns around towards the new target");
    }

    @Test
    void stopsOnTheGnomeCell() {
        monster.setTarget(cell(0, 5));
        tick(monster, 1, TICKS);
        assertEquals(cell(0, 5), monster.getPosition());
        tick(monster, TICKS + 1, TICKS * 5);
        assertEquals(cell(0, 5), monster.getPosition(), "stays on the target");
    }

    @Test
    void goesAroundTheWallAndNeverEntersIt() {
        Monster m = new TestMonster(grid, new ChaseStrategy(new GdxAiPathfinder()), cell(0, 2));
        m.setTarget(cell(6, 2));
        for (int t = 1; t <= TICKS * 30; t++) {
            m.onTick(t);
            assertTrue(grid.isWalkable(m.getPosition()), "never inside a wall or outside the map: " + m.getPosition());
        }
        assertEquals(cell(6, 2), m.getPosition(), "reaches the other side of the wall");
    }

    @Test
    void staysPutWhenTargetIsInAWall() {
        monster.setTarget(cell(3, 2));
        tick(monster, 1, TICKS * 5);
        assertEquals(cell(0, START_ROW), monster.getPosition());
    }

    @Test
    void snapMovesOneCellTowardsItsOrigin() {
        monster.onSnap(snapAt(0.5f, 0.5f));
        assertEquals(cell(0, START_ROW - SNAP_CASES), monster.getPosition());
    }

    @Test
    void snapUsesItsOriginNotTheGnome() {
        monster.setTarget(cell(0, 9));
        monster.onSnap(new SnapEvent(9.5f, 6.5f, SnapSource.DYNAMITE, 2L));
        assertEquals(cell(SNAP_CASES, START_ROW), monster.getPosition(), "moves towards (9, 6), not towards the gnome");
    }

    @Test
    void snapMovesSeveralCellsWhenSnapDistanceIsRaised() {
        monster.setSnapDistance(3);
        monster.onSnap(snapAt(0.5f, 0.5f));
        assertEquals(cell(0, 3), monster.getPosition());
    }

    @Test
    void snapStopsOnOriginWhenCloserThanSnapDistance() {
        monster.setSnapDistance(3);
        monster.onSnap(snapAt(0.5f, 5.5f));
        assertEquals(cell(0, 5), monster.getPosition());
    }

    @Test
    void snapDistanceZeroDoesNotMove() {
        monster.setSnapDistance(0);
        monster.onSnap(snapAt(0.5f, 0.5f));
        assertEquals(cell(0, START_ROW), monster.getPosition());
    }

    @Test
    void casePerMoveChangesTheTickMove() {
        monster.setCasePerMove(2);
        monster.setTarget(cell(0, 0));
        tick(monster, 1, TICKS);
        assertEquals(cell(0, START_ROW - 2), monster.getPosition());
        assertEquals(2, monster.getCasePerMove());
    }

    @Test
    void worksWithARealGameClock() {
        GameClock clock = new GameClock();
        clock.addListener(monster);
        monster.setTarget(cell(0, 0));
        // 2 frames = 1 tick at 60 fps; +10 frames of margin against float rounding
        int frames = TICKS * 2 + 10;
        for (int i = 0; i < frames; i++) {
            clock.update(1f / 60f);
        }
        assertEquals(cell(0, START_ROW - CASES), monster.getPosition(), "one move after " + TICKS + " ticks");
    }

    @Test
    void rejectsInvalidValues() {
        MovementStrategy strategy = new ChaseStrategy(new GdxAiPathfinder());
        assertThrows(IllegalArgumentException.class, () -> new TestMonster(null, strategy, cell(0, 0)));
        assertThrows(IllegalArgumentException.class, () -> new TestMonster(grid, null, cell(0, 0)));
        assertThrows(IllegalArgumentException.class, () -> new TestMonster(grid, strategy, null));
        assertThrows(IllegalArgumentException.class, () -> new ChaseStrategy(null));
        assertThrows(IllegalArgumentException.class, () -> monster.setTicksPerMove(0));
        assertThrows(IllegalArgumentException.class, () -> monster.setCasePerMove(0));
        assertThrows(IllegalArgumentException.class, () -> monster.setSnapDistance(-1));
    }

    @Test
    void ticksPerMoveCanBeChanged() {
        monster.setTicksPerMove(10);
        monster.setTarget(cell(0, 0));
        tick(monster, 1, 10);
        assertEquals(cell(0, START_ROW - CASES), monster.getPosition());
        assertEquals(10, monster.getTicksPerMove());
    }
}
package com.blindpath.domain.monster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.monster.Monster;
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
 * Tests of {@link Monster} and {@link ChaseStrategy} on a 10 x 10 map with a wall
 * on column 3, rows 0 to 4. The monster starts on (0, 6).
 */
class MonsterTest {

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
        monster = new Monster(grid, new ChaseStrategy(new GdxAiPathfinder()), cell(0, 6));
    }

    @Test
    void hasDefaultValues() {
        assertEquals(20, Monster.DEFAULT_TICKS_PER_MOVE);
        assertEquals(3, Monster.DEFAULT_SNAP_DISTANCE);
        assertEquals(20, monster.getTicksPerMove());
        assertEquals(3, monster.getSnapDistance());
        assertEquals(cell(0, 6), monster.getPosition());
    }

    @Test
    void doesNotMoveBeforeTick20() {
        monster.setTarget(cell(0, 0));
        tick(monster, 1, 19);
        assertEquals(cell(0, 6), monster.getPosition());
    }

    @Test
    void movesOneCellAtTick20() {
        monster.setTarget(cell(0, 0));
        tick(monster, 1, 20);
        assertEquals(cell(0, 5), monster.getPosition());
    }

    @Test
    void movesThreeCellsAfter60Ticks() {
        monster.setTarget(cell(0, 0));
        tick(monster, 1, 60);
        assertEquals(cell(0, 3), monster.getPosition());
    }

    @Test
    void doesNotMoveWithoutTarget() {
        tick(monster, 1, 100);
        assertEquals(cell(0, 6), monster.getPosition());
    }

    @Test
    void followsTheGnomeWhenItMoves() {
        monster.setTarget(cell(0, 0));
        tick(monster, 1, 20);
        assertEquals(cell(0, 5), monster.getPosition());
        monster.setTarget(cell(0, 9));
        tick(monster, 21, 40);
        assertEquals(cell(0, 6), monster.getPosition(), "turns around towards the new target");
    }

    @Test
    void stopsOnTheGnomeCell() {
        monster.setTarget(cell(0, 5));
        tick(monster, 1, 20);
        assertEquals(cell(0, 5), monster.getPosition());
        tick(monster, 21, 100);
        assertEquals(cell(0, 5), monster.getPosition(), "stays on the target");
    }

    @Test
    void goesAroundTheWallAndNeverEntersIt() {
        Monster m = new Monster(grid, new ChaseStrategy(new GdxAiPathfinder()), cell(0, 2));
        m.setTarget(cell(6, 2));
        for (int t = 1; t <= 400; t++) {
            m.onTick(t);
            assertTrue(grid.isWalkable(m.getPosition()), "never inside a wall or outside the map: " + m.getPosition());
        }
        assertEquals(cell(6, 2), m.getPosition(), "reaches the other side of the wall");
    }

    @Test
    void staysPutWhenTargetIsInAWall() {
        monster.setTarget(cell(3, 2));
        tick(monster, 1, 100);
        assertEquals(cell(0, 6), monster.getPosition());
    }

    @Test
    void snapMovesThreeCellsTowardsItsOrigin() {
        monster.onSnap(snapAt(0.5f, 0.5f));
        assertEquals(cell(0, 3), monster.getPosition());
    }

    @Test
    void snapUsesItsOriginNotTheGnome() {
        monster.setTarget(cell(0, 0));
        monster.onSnap(new SnapEvent(9.5f, 6.5f, SnapSource.DYNAMITE, 2L));
        assertEquals(cell(3, 6), monster.getPosition(), "moves towards (9, 6), not towards the gnome");
    }

    @Test
    void snapStopsOnOriginWhenCloserThanSnapDistance() {
        monster.onSnap(snapAt(0.5f, 5.5f));
        assertEquals(cell(0, 5), monster.getPosition());
    }

    @Test
    void snapDistanceZeroDoesNotMove() {
        monster.setSnapDistance(0);
        monster.onSnap(snapAt(0.5f, 0.5f));
        assertEquals(cell(0, 6), monster.getPosition());
    }

    @Test
    void workedWithARealGameClock() {
        GameClock clock = new GameClock();
        clock.addListener(monster);
        monster.setTarget(cell(0, 0));
        for (int i = 0; i < 60; i++) {
            clock.update(1f / 60f);
        }
        assertEquals(cell(0, 5), monster.getPosition(), "30 ticks = one move");
    }

    @Test
    void rejectsInvalidValues() {
        MovementStrategy strategy = new ChaseStrategy(new GdxAiPathfinder());
        assertThrows(IllegalArgumentException.class, () -> new Monster(null, strategy, cell(0, 0)));
        assertThrows(IllegalArgumentException.class, () -> new Monster(grid, null, cell(0, 0)));
        assertThrows(IllegalArgumentException.class, () -> new Monster(grid, strategy, null));
        assertThrows(IllegalArgumentException.class, () -> new ChaseStrategy(null));
        assertThrows(IllegalArgumentException.class, () -> monster.setTicksPerMove(0));
        assertThrows(IllegalArgumentException.class, () -> monster.setSnapDistance(-1));
    }

    @Test
    void ticksPerMoveCanBeChanged() {
        monster.setTicksPerMove(10);
        monster.setTarget(cell(0, 0));
        tick(monster, 1, 10);
        assertEquals(cell(0, 5), monster.getPosition());
        assertEquals(10, monster.getTicksPerMove());
    }
}
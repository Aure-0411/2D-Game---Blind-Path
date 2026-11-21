package com.blindpath.domain.monster;

import com.blindpath.domain.labyrinth.OpeningOrientation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the effect every {@link Monster} can receive: immobilization by the bear trap
 * (POC_EASY-10). Stun and push back were dropped with the sword (reduced scope, 19/11).
 *
 * <p>Movement uses a fake strategy that always moves one column to the right,
 * so the tests do not depend on the pathfinding.</p>
 */
class MonsterEffectsTest {

    /** Fake strategy: one column to the right at each step. */
    private static final MovementStrategy STEP_RIGHT =
            (grid, from, target) -> new GridPosition(from.getColumn() + 1, from.getRow());

    /** Ticks between two moves (one cell per move by default). */
    private static final int MOVE = Monster.DEFAULT_TICKS_PER_MOVE;

    private LabyrinthGrid openGrid;
    private Monster monster;

    private static GridPosition cell(int column, int row) {
        return new GridPosition(column, row);
    }

    private static LabyrinthGrid gridWith(List<Obstacle> walls) {
        return new LabyrinthGrid(new Labyrinth(10, 10,
                new Entrance(0, 2, OpeningOrientation.VERTICAL), new Exit(9, 2, OpeningOrientation.VERTICAL), walls));
    }

    /** Monster is abstract: an anonymous subclass is enough to test the shared effects. */
    private static Monster monsterAt(LabyrinthGrid grid, GridPosition start) {
        return new Monster(grid, STEP_RIGHT, start) { };
    }

    private static SnapEvent snapAt(float x, float y) {
        return new SnapEvent(x, y, SnapSource.HERO, 1L);
    }

    /** Sends ticks {@code from..to} to the monster (one move every MOVE ticks). */
    private static void tick(Monster m, int from, int to) {
        for (int t = from; t <= to; t++) {
            m.onTick(t);
        }
    }

    @BeforeEach
    void setUp() {
        openGrid = gridWith(List.of());
        monster = monsterAt(openGrid, cell(2, 6));
        monster.setTarget(cell(9, 6));
    }

    // ---------- Default state ----------

    @Test
    void newMonsterIsNotImmobilized() {
        assertFalse(monster.isImmobilized(), "a new monster must not be immobilized");
        assertTrue(monster.canMove(), "a new monster must be able to move");
    }

    @Test
    void monsterWithoutEffectMovesOnTicks() {
        tick(monster, 1, MOVE);
        assertEquals(cell(3, 6), monster.getPosition(), "one move after MOVE ticks");
    }

    // ---------- Immobilization (bear trap: 3 s) ----------

    @Test
    void immobilizedMonsterDoesNotMoveOnTicks() {
        monster.immobilize(3f);
        assertTrue(monster.isImmobilized(), "the monster must be immobilized");
        assertFalse(monster.canMove(), "an immobilized monster cannot move");

        tick(monster, 1, 3 * MOVE);
        assertEquals(cell(2, 6), monster.getPosition(), "an immobilized monster must not move on ticks");
    }

    @Test
    void immobilizedMonsterIgnoresSnap() {
        monster.immobilize(3f);
        monster.onSnap(snapAt(8.5f, 6.5f));
        assertEquals(cell(2, 6), monster.getPosition(), "an immobilized monster must ignore the snap");
    }

    @Test
    void immobilizationEndsExactlyAfterItsDuration() {
        monster.immobilize(3f);
        monster.update(2.9f);
        assertTrue(monster.isImmobilized(), "still immobilized after 2.9 s of a 3 s immobilization");
        monster.update(0.1f);
        assertFalse(monster.isImmobilized(), "no longer immobilized after 3 s");
        assertTrue(monster.canMove(), "the monster can move again after the immobilization");
    }

    @Test
    void monsterMovesAgainAfterImmobilization() {
        monster.immobilize(3f);
        monster.update(3f);
        tick(monster, 1, MOVE);
        assertEquals(cell(3, 6), monster.getPosition(), "the monster must move again after the immobilization");
    }

    @Test
    void shorterImmobilizationDoesNotShortenCurrentOne() {
        monster.immobilize(3f);
        monster.immobilize(1f);
        monster.update(2f);
        assertTrue(monster.isImmobilized(), "a shorter immobilization must not shorten the current one");
    }

    @Test
    void longerImmobilizationExtendsCurrentOne() {
        monster.immobilize(1f);
        monster.immobilize(3f);
        monster.update(2f);
        assertTrue(monster.isImmobilized(), "a longer immobilization must extend the current one");
    }

    @Test
    void zeroDurationDoesNotImmobilize() {
        monster.immobilize(0f);
        assertFalse(monster.isImmobilized(), "an immobilization of 0 s has no effect");
    }

    // ---------- Invalid arguments ----------

    @Test
    void negativeDurationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> monster.immobilize(-1f), "negative immobilization");
    }
}

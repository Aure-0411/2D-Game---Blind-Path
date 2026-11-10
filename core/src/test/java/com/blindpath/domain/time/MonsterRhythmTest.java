package com.blindpath.domain.time;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
/**
 * Shows how the future {@code Monster} of the UML will use the clock: it implements
 * {@link TickListener}, moves {@code distanceMove} cells every {@code ticksPerMove}
 * ticks, and skips its moves while a {@link GameTimer} stun is running.
 */
class MonsterRhythmTest {
 
    private static final float FRAME = 1f / 60;
 
    /** Minimal stand-in for Monster: only the time logic, no grid. */
    private static final class FakeMonster implements TickListener {
        private final int ticksPerMove;
        private final int distanceMove;
        private final GameTimer stun = new GameTimer();
        private int cellsMoved;
 
        FakeMonster(int ticksPerMove, int distanceMove) {
            this.ticksPerMove = ticksPerMove;
            this.distanceMove = distanceMove;
        }
 
        @Override
        public void onTick(long tick) {
            if (!stun.isRunning() && tick % ticksPerMove == 0) {
                cellsMoved += distanceMove;
            }
        }
 
        void update(float deltaTime) {
            stun.update(deltaTime);
        }
    }
 
    private GameClock clock;
 
    @BeforeEach
    void setUp() {
        clock = new GameClock();
    }
 
    /** Same order as Level.update(deltaTime): monsters first, then the clock. */
    private void runSeconds(FakeMonster monster, int seconds) {
        for (int i = 0; i < seconds * 60; i++) {
            monster.update(FRAME);
            clock.update(FRAME);
        }
    }
 
    @Test
    void defaultMonsterMovesOneCellPerSecond() {
        FakeMonster monster = new FakeMonster(20, 1);
        clock.addListener(monster);
        runSeconds(monster, 3);
        assertEquals(3, monster.cellsMoved, "GDD default: 1 cell every 20 ticks -> 3 cells in 3 s");
    }
 
    @Test
    void stunnedMonsterDoesNotMove() {
        FakeMonster monster = new FakeMonster(20, 1);
        clock.addListener(monster);
        monster.stun.start(2.5f);
        runSeconds(monster, 3);
        assertEquals(1, monster.cellsMoved, "stunned for 2.5 s: the moves at 1 s and 2 s are skipped, only the one at 3 s happens");
    }
 
    @Test
    void strongerMonsterAfterRetryMovesFaster() {
        FakeMonster monster = new FakeMonster(10, 1);
        clock.addListener(monster);
        runSeconds(monster, 3);
        assertEquals(6, monster.cellsMoved, "10 ticks between moves -> 6 cells in 3 s");
    }
}
 
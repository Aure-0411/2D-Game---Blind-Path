package com.blindpath.domain.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.monster.Screamer;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.AabbCollisionWorld;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the link between the Screamer and the gnome in Level (POC_HARD-2, #73):
 * when the Screamer screams, the gnome is stunned for Screamer.STUN_DURATION and cannot move.
 */
class LevelScreamTest {

    /** Fake Random: always 0, so the threshold is MIN_SNAPS (5). */
    private static class ZeroRandom extends Random {
        @Override
        public int nextInt(int bound) {
            return 0;
        }
    }

    private Gnome gnome;
    private Screamer screamer;
    private Level level;

    @BeforeEach
    void setUp() {
        Labyrinth labyrinth = new Labyrinth(10, 10,
                new Entrance(0, 4, Border.LEFT), new Exit(9, 4, Border.RIGHT), List.of());
        gnome = new Gnome();
        gnome.setPosition(1f, 4f);
        // The screamer never moves (strategy returns the current cell) and stands far from the gnome.
        screamer = new Screamer(new LabyrinthGrid(labyrinth), (grid, current, goal) -> current,
                new GridPosition(8, 8), new ZeroRandom());
        Pathfinder pathfinder = (grid, start, target) -> List.of();
        level = new Level(gnome, labyrinth, new AabbCollisionWorld(), screamer, pathfinder, List.of());
    }

    private void snaps(int count) {
        for (int i = 0; i < count; i++) {
            level.emitSnap(new SnapEvent(1.5f, 4.5f, SnapSource.HERO, 1L));
        }
    }

    @Test
    void gnomeIsNotStunnedBeforeTheScream() {
        snaps(Screamer.MIN_SNAPS - 1);
        assertFalse(gnome.isStunned());
    }

    @Test
    void screamStunsTheGnome() {
        snaps(Screamer.MIN_SNAPS);
        assertTrue(gnome.isStunned(), "5th snap: scream, the gnome is stunned (infinite range)");
    }

    @Test
    void stunWearsOffWithLevelUpdate() {
        snaps(Screamer.MIN_SNAPS);
        level.update(Screamer.STUN_DURATION);
        assertFalse(gnome.isStunned(), "after STUN_DURATION seconds the gnome is free");
    }

    @Test
    void stunnedGnomeCannotMove() {
        snaps(Screamer.MIN_SNAPS);
        float x = gnome.getX();
        assertFalse(level.tryMove(gnome, 1f, 0f, 0.1f), "stunned: tryMove refuses");
        assertEquals(x, gnome.getX(), 1e-6f);
    }

    @Test
    void gnomeMovesAgainOnceTheStunIsOver() {
        snaps(Screamer.MIN_SNAPS);
        level.update(Screamer.STUN_DURATION);
        assertTrue(level.tryMove(gnome, 1f, 0f, 0.1f));
    }

    @Test
    void invincibleGnomeIsNotStunnedByTheScream() {
        gnome.toggleInvincible();
        snaps(Screamer.MIN_SNAPS);
        assertFalse(gnome.isStunned());
    }
}

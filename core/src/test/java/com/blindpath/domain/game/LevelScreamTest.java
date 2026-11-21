package com.blindpath.domain.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.monster.Screamer;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.AabbCollisionWorld;

/**
 * Tests of the link between the Screamer and the hero in Level (POC_HARD-2, #73):
 * when the Screamer screams, the hero is stunned for Screamer.STUN_DURATION and cannot move.
 */
class LevelScreamTest {

    /** Fake Random: always 0, so the threshold is MIN_SNAPS (5). */
    private static class ZeroRandom extends Random {
        @Override
        public int nextInt(int bound) {
            return 0;
        }
    }

    private Character hero;
    private Screamer screamer;
    private Level<Character> level;

    @BeforeEach
    void setUp() {
        Labyrinth labyrinth = new Labyrinth(10, 10,
                new Entrance(0, 4, OpeningOrientation.VERTICAL), new Exit(9, 4, OpeningOrientation.VERTICAL), List.of());
        hero = new Character(3f, 1f, 4f, 0.8f, 0.8f) {};
        // The screamer never moves (strategy returns the current cell) and stands far from the hero.
        screamer = new Screamer(new LabyrinthGrid(labyrinth), (grid, current, goal) -> current,
                new GridPosition(8, 8), new ZeroRandom());
        Pathfinder pathfinder = (grid, start, target) -> List.of();
        level = new Level<>(hero, labyrinth, new AabbCollisionWorld(), screamer, pathfinder, List.of());
    }

    private void snaps(int count) {
        for (int i = 0; i < count; i++) {
            level.emitSnap(new SnapEvent(1.5f, 4.5f, SnapSource.HERO, 1L));
        }
    }

    @Test
    void heroIsNotStunnedBeforeTheScream() {
        snaps(Screamer.MIN_SNAPS - 1);
        assertFalse(hero.isStunned());
    }

    @Test
    void screamStunsTheHero() {
        snaps(Screamer.MIN_SNAPS);
        assertTrue(hero.isStunned(), "5th snap: scream, the hero is stunned (infinite range)");
    }

    @Test
    void stunWearsOffWithLevelUpdate() {
        snaps(Screamer.MIN_SNAPS);
        level.update(Screamer.STUN_DURATION);
        assertFalse(hero.isStunned(), "after STUN_DURATION seconds the hero is free");
    }

    @Test
    void stunnedHeroCannotMove() {
        snaps(Screamer.MIN_SNAPS);
        float x = hero.getX();
        assertFalse(level.tryMove(hero, 1f, 0f, 0.1f), "stunned: tryMove refuses");
        assertEquals(x, hero.getX(), 1e-6f);
    }

    @Test
    void heroMovesAgainOnceTheStunIsOver() {
        snaps(Screamer.MIN_SNAPS);
        level.update(Screamer.STUN_DURATION);
        assertTrue(level.tryMove(hero, 1f, 0f, 0.1f));
    }

    @Test
    void invincibleHeroIsNotStunnedByTheScream() {
        hero.toggleInvincible();
        snaps(Screamer.MIN_SNAPS);
        assertFalse(hero.isStunned());
    }
}
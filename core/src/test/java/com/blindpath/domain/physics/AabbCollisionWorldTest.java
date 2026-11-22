package com.blindpath.domain.physics;

import static org.junit.jupiter.api.Assertions.*;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Bounds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AabbCollisionWorldTest {

    private static final float EPS = 1e-3f;
    private AabbCollisionWorld world;
    private Character hero;

    @BeforeEach
    void setUp() {
        world = new AabbCollisionWorld();
        hero = new Character(3f, 1f, 1f, 0.8f, 0.8f) {};
        world.addDynamic(hero);
    }

    @Test
    void freeSpace_movesBySpeedTimesDelta() {
        boolean moved = world.move(hero, 1f, 0f, 0.5f);

        assertTrue(moved);
        assertEquals(1f + hero.getSpeed() * 0.5f, hero.getX(), EPS);
        assertEquals(1f, hero.getY(), EPS);
    }

    @Test
    void wall_stopsTheHero_withoutOverlap() {
        world.addStatic(new Bounds(3f, 0f, 1f, 10f));

        for (int i = 0; i < 60; i++) {
            world.move(hero, 1f, 0f, 1f / 30f);
        }

        assertTrue(hero.getX() + hero.getWidth() <= 3f + EPS, "x=" + hero.getX());
        assertFalse(hero.getBounds().intersects(new Bounds(3f, 0f, 1f, 10f)));
    }

    @Test
    void pushingAgainstWall_reportsNoMovement() {
        world.addStatic(new Bounds(3f, 0f, 1f, 10f));
        for (int i = 0; i < 60; i++) {
            world.move(hero, 1f, 0f, 1f / 30f);
        }

        assertFalse(world.move(hero, 1f, 0f, 1f / 30f));
    }

    @Test
    void diagonalAgainstWall_slidesAlongIt() {
        world.addStatic(new Bounds(3f, 0f, 1f, 10f));
        for (int i = 0; i < 60; i++) {
            world.move(hero, 1f, 0f, 1f / 30f);
        }
        float yBefore = hero.getY();

        world.move(hero, 0.7071f, 0.7071f, 0.2f);

        assertTrue(hero.getY() > yBefore, "doit glisser vers le haut");
        assertTrue(hero.getX() + hero.getWidth() <= 3f + EPS);
    }

    @Test
    void corridorOfTwoCells_isTraversable() {
        // Couloir horizontal de hauteur 2 entre y=1 et y=3
        world.addStatic(new Bounds(0f, 0f, 20f, 1f));
        world.addStatic(new Bounds(0f, 3f, 20f, 1f));
        hero.setPosition(1f, 1.5f); // 1.5 à 2.5 : entièrement dans le couloir

        for (int i = 0; i < 120; i++) {
            world.move(hero, 1f, 0f, 1f / 60f);
        }

        assertTrue(hero.getX() > 3f, "le héros doit avancer dans le couloir, x=" + hero.getX());
    }

    @Test
    void nullStatic_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> world.addStatic(null));
    }
}
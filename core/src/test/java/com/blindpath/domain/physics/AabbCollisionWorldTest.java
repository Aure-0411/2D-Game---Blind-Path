package com.blindpath.domain.physics;

import static org.junit.jupiter.api.Assertions.*;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.Bounds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AabbCollisionWorldTest {

    private static final float EPS = 1e-3f;
    private AabbCollisionWorld world;
    private Gnome gnome;

    @BeforeEach
    void setUp() {
        world = new AabbCollisionWorld();
        gnome = new Gnome();
        gnome.setPosition(1f, 1f);
        world.addDynamic(gnome);
    }

    @Test
    void freeSpace_movesBySpeedTimesDelta() {
        boolean moved = world.move(gnome, 1f, 0f, 0.5f);

        assertTrue(moved);
        assertEquals(1f + gnome.getSpeed() * 0.5f, gnome.getX(), EPS);
        assertEquals(1f, gnome.getY(), EPS);
    }

    @Test
    void wall_stopsTheGnome_withoutOverlap() {
        world.addStatic(new Bounds(3f, 0f, 1f, 10f));

        for (int i = 0; i < 60; i++) {
            world.move(gnome, 1f, 0f, 1f / 30f);
        }

        assertTrue(gnome.getX() + gnome.getWidth() <= 3f + EPS, "x=" + gnome.getX());
        assertFalse(gnome.getBounds().intersects(new Bounds(3f, 0f, 1f, 10f)));
    }

    @Test
    void pushingAgainstWall_reportsNoMovement() {
        world.addStatic(new Bounds(3f, 0f, 1f, 10f));
        for (int i = 0; i < 60; i++) {
            world.move(gnome, 1f, 0f, 1f / 30f);
        }

        assertFalse(world.move(gnome, 1f, 0f, 1f / 30f));
    }

    @Test
    void diagonalAgainstWall_slidesAlongIt() {
        world.addStatic(new Bounds(3f, 0f, 1f, 10f));
        for (int i = 0; i < 60; i++) {
            world.move(gnome, 1f, 0f, 1f / 30f);
        }
        float yBefore = gnome.getY();

        world.move(gnome, 0.7071f, 0.7071f, 0.2f);

        assertTrue(gnome.getY() > yBefore, "doit glisser vers le haut");
        assertTrue(gnome.getX() + gnome.getWidth() <= 3f + EPS);
    }

    @Test
    void corridorOfTwoCells_isTraversable() {
        // Couloir horizontal de hauteur 2 entre y=1 et y=3
        world.addStatic(new Bounds(0f, 0f, 20f, 1f));
        world.addStatic(new Bounds(0f, 3f, 20f, 1f));
        gnome.setPosition(1f, 1.5f); // 1.5 à 2.5 : entièrement dans le couloir

        for (int i = 0; i < 120; i++) {
            world.move(gnome, 1f, 0f, 1f / 60f);
        }

        assertTrue(gnome.getX() > 3f, "le gnome doit avancer dans le couloir, x=" + gnome.getX());
    }

    @Test
    void nullStatic_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> world.addStatic(null));
    }
}
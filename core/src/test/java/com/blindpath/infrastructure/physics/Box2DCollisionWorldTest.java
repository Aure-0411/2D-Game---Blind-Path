package com.blindpath.infrastructure.physics;

import static org.junit.jupiter.api.Assertions.*;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.labyrinth.RotatingWall;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Ignoré (et non en échec) si les natives Box2D ne sont pas sur le classpath de test. */
class Box2DCollisionWorldTest {

    private static final float EPS = 0.02f; // Box2D garde une petite marge autour des formes
    private Box2DCollisionWorld world;
    private Gnome gnome;

    @BeforeEach
    void setUp() {
        try {
            world = new Box2DCollisionWorld();
        } catch (Throwable nativesMissing) {
            Assumptions.abort("Natives Box2D indisponibles : " + nativesMissing);
        }
        gnome = new Gnome();
        gnome.setPosition(1f, 1f);
        world.addDynamic(gnome);
    }

    @AfterEach
    void tearDown() {
        if (world != null) world.dispose();
    }

    @Test
    void freeSpace_movesBySpeedTimesDelta() {
        assertTrue(world.move(gnome, 1f, 0f, 1f / 60f));
        assertEquals(1f + gnome.getSpeed() / 60f, gnome.getX(), 1e-3f);
    }

    @Test
    void wall_blocksTheGnome() {
        world.addStatic(new Bounds(3f, 0f, 1f, 10f));
        for (int i = 0; i < 120; i++) world.move(gnome, 1f, 0f, 1f / 60f);

        assertTrue(gnome.getX() + gnome.getWidth() <= 3f + EPS, "x=" + gnome.getX());
    }

    @Test
    void wall_gnomeSlidesAlongIt() {
        world.addStatic(new Bounds(3f, 0f, 1f, 10f));
        for (int i = 0; i < 120; i++) world.move(gnome, 1f, 0f, 1f / 60f);
        float yBefore = gnome.getY();

        for (int i = 0; i < 30; i++) world.move(gnome, 0.7071f, 0.7071f, 1f / 60f);

        assertTrue(gnome.getY() > yBefore + 0.3f, "doit glisser, y=" + gnome.getY());
    }

    @Test
    void corridorOfTwoCells_isTraversable() {
        world.addStatic(new Bounds(0f, 0f, 20f, 1f));
        world.addStatic(new Bounds(0f, 3f, 20f, 1f));
        gnome.setPosition(1f, 1.5f);

        for (int i = 0; i < 180; i++) world.move(gnome, 1f, 0f, 1f / 60f);

        assertTrue(gnome.getX() > 3f, "x=" + gnome.getX());
    }

    @Test
    void unregisteredCharacter_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> world.move(new Gnome(), 1f, 0f, 0.1f));
    }

    @Test
    void changingObstacleUpdatesNativeBodyPositionAndCollisionState() {
        RotatingWall wall = new RotatingWall(3, 0, OpeningOrientation.HORIZONTAL, 3, 1, 90);
        world.addChangingObstacle(wall);
        wall.rotate(); // Nouvelle zone : x=3..4, y=1..3.
        world.syncChangingObstacles();
        for (int i = 0; i < 120; i++) world.move(gnome, 1, 0, 1f / 60f);
        assertTrue(gnome.getX() + gnome.getWidth() <= 3f + EPS);
        wall.setCollide(false);
        for (int i = 0; i < 120; i++) world.move(gnome, 1, 0, 1f / 60f);
        assertTrue(gnome.getX() > 4f, "la collision désactivée doit laisser sortir le gnome");
    }
}

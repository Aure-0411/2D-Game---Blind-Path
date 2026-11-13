package com.blindpath.domain.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Wall;
import com.blindpath.domain.physics.AabbCollisionWorld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LevelTest {

    private Gnome gnome;
    private Level level;

    @BeforeEach
    void setUp() {
        Labyrinth labyrinth = new Labyrinth(10, 10,
        new Entrance(0, 4, Border.LEFT),
        new Exit(9, 4, Border.RIGHT),
        List.of(new Wall(5f, 0f, 1f, 10f)));
        gnome = new Gnome();
        gnome.setPosition(1f, 4f);
        level = new Level(gnome, labyrinth, new AabbCollisionWorld());
    }

    @Test
    void tryMove_inOpenSpace_moves() {
        float initialY = gnome.getY();
        float deltaTime = 0.5f;

        assertTrue(level.tryMove(gnome, 0f, 1f, deltaTime));

        float expectedY = initialY + gnome.getSpeed() * deltaTime;

        assertEquals(expectedY, gnome.getY(), 1e-3f);
    }

    @Test
    void tryMove_againstWall_neverEntersIt() {
        for (int i = 0; i < 200; i++) {
            level.tryMove(gnome, 1f, 0f, 1f / 60f);
        }
        assertTrue(gnome.getX() + gnome.getWidth() <= 5f + 1e-3f, "x=" + gnome.getX());
    }

    @Test
    void tryMove_withNullCharacter_throws() {
        assertThrows(IllegalArgumentException.class, () -> level.tryMove(null, 1f, 0f, 0.1f));
    }

    @Test
    void constructor_rejectsNulls() {
        Labyrinth l = level.getLabyrinth();
        assertThrows(IllegalArgumentException.class, () -> new Level(null, l, new AabbCollisionWorld()));
        assertThrows(IllegalArgumentException.class, () -> new Level(gnome, null, new AabbCollisionWorld()));
        assertThrows(IllegalArgumentException.class, () -> new Level(gnome, l, null));
    }

    @Test
    void disabledObstacle_isNotRegistered() {
        Wall ghost = new Wall(3f, 0f, 1f, 10f);
        ghost.setCollide(false);
        Labyrinth l = new Labyrinth(10, 10, new Entrance(0, 4, Border.LEFT),
                new Exit(9, 4, Border.RIGHT), List.of(ghost));
        Gnome g = new Gnome();
        g.setPosition(1f, 4f);
        Level lvl = new Level(g, l, new AabbCollisionWorld());

        for (int i = 0; i < 120; i++) {
            lvl.tryMove(g, 1f, 0f, 1f / 60f);
        }
        assertTrue(g.getX() > 4f, "un mur désactivé ne bloque pas");
    }

    @Test
    void boundary_keepsGnomeInsideMap() {
        for (int i = 0; i < 300; i++) {
            level.tryMove(gnome, -1f, -1f, 1f / 60f);
        }
        assertTrue(gnome.getX() >= -1e-3f, "x=" + gnome.getX());
        assertTrue(gnome.getY() >= -1e-3f, "y=" + gnome.getY());
    }

    @Test
    void boundary_stopsGnomeAtTheExitOpening() {
        Labyrinth open = new Labyrinth(10, 10, new Entrance(0, 4, Border.LEFT),
                new Exit(9, 4, Border.RIGHT), List.of());
        Gnome g = new Gnome();
        g.setPosition(7f, 4f);
        Level lvl = new Level(g, open, new AabbCollisionWorld());

        for (int i = 0; i < 300; i++) {
            lvl.tryMove(g, 1f, 0f, 1f / 60f);
        }
        assertTrue(g.getX() + g.getWidth() <= 10f + 1e-3f, "ne doit pas sortir de la carte, x=" + g.getX());
        assertTrue(g.getX() > 8.9f, "doit atteindre la case de sortie, x=" + g.getX());
    }

    @Test
    void labyrinth_exposesFourBoundaryWalls() {
        assertEquals(4, level.getLabyrinth().getBoundaryWalls().size());
    }
}
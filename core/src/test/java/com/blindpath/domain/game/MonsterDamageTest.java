package com.blindpath.domain.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Wall;
import com.blindpath.domain.monster.ChaseStrategy;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.monster.MovementStrategy;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.physics.AabbCollisionWorld;
import com.blindpath.infrastructure.pathfinding.GdxAiPathfinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Chaîne complète des dégâts (POC_EASY-19, #127) : un monstre posé sur le gnome,
 * Level.update() simule les frames du jeu.
 * Un coup de monstre = 1 cœur (2 PV), suivi de 2 s d'immunité ; 3 coups = mort.
 */
class MonsterDamageTest {

    /** Monster est abstrait : une sous-classe vide suffit pour les tests. */
    private static class TestMonster extends Monster {
        TestMonster(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
            super(grid, strategy, start);
        }
    }

    /** Une frame courte (≈ 10 images par seconde), bien plus petite que l'immunité. */
    private static final float FRAME = 0.1f;

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

        // Le monstre est posé sur le gnome et ne bouge pas (aucun tick d'horloge ici).
        LabyrinthGrid grid = new LabyrinthGrid(labyrinth);
        MovementStrategy strategy = new ChaseStrategy(new GdxAiPathfinder());
        level.setMonster(new TestMonster(grid, strategy, new GridPosition(1, 4)));
    }

    @Test
    void firstContact_removesOneFullHeart() {
        level.update(FRAME);

        assertEquals(Gnome.MAX_LIFE - 2, gnome.getLife());
        assertTrue(gnome.isImmune());
    }

    @Test
    void contactDuringImmunity_removesNothing() {
        level.update(FRAME);

        // 10 frames de contact pendant l'immunité : aucun dégât de plus.
        for (int i = 0; i < 10; i++) {
            level.update(FRAME);
        }

        assertEquals(Gnome.MAX_LIFE - 2, gnome.getLife());
    }

    @Test
    void contactAfterImmunity_removesAnotherHeart() {
        level.update(FRAME);
        level.update(Gnome.IMMUNITY_DURATION);

        assertEquals(Gnome.MAX_LIFE - 4, gnome.getLife());
    }

    @Test
    void threeHits_killTheGnome() {
        level.update(FRAME);
        level.update(Gnome.IMMUNITY_DURATION);
        assertFalse(gnome.isDead());

        level.update(Gnome.IMMUNITY_DURATION);

        assertEquals(0, gnome.getLife());
        assertTrue(gnome.isDead());
    }

    @Test
    void deadGnome_neverGoesBelowZero() {
        for (int i = 0; i < 6; i++) {
            level.update(Gnome.IMMUNITY_DURATION);
        }

        assertEquals(0, gnome.getLife());
        assertTrue(gnome.isDead());
    }

    @Test
    void noMonster_noDamage() {
        Labyrinth labyrinth = new Labyrinth(10, 10,
                new Entrance(0, 4, Border.LEFT),
                new Exit(9, 4, Border.RIGHT),
                List.of());
        Gnome lonelyGnome = new Gnome();
        Level emptyLevel = new Level(lonelyGnome, labyrinth, new AabbCollisionWorld());

        emptyLevel.update(Gnome.IMMUNITY_DURATION);

        assertEquals(Gnome.MAX_LIFE, lonelyGnome.getLife());
        assertFalse(lonelyGnome.isDead());
    }
}

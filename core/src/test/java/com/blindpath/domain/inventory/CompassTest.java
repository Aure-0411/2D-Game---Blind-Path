package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.physics.AabbCollisionWorld;

class CompassTest {

    private static class TestHero extends Character {
        TestHero() {
            super(3f, 2f, 2f, 0.8f, 0.8f);
        }
    }

    private Level<Character> createLevel(Character hero, Exit exit) {
        Labyrinth labyrinth = new Labyrinth(10, 10, new Entrance(0, 4, OpeningOrientation.VERTICAL), exit, List.of());
        Monster monster = new Monster(new LabyrinthGrid(labyrinth), (g, c, target) -> c, new GridPosition(8, 8)) {};
        return new Level<>(hero, labyrinth, new AabbCollisionWorld(), monster, (g, s, t) -> List.of(), List.of());
    }

    @Test
    void useFromInventory_activatesCompassForFiveSecondsAndConsumesItem() {
        Character hero = new TestHero();
        Level<Character> level = createLevel(hero, new Exit(9, 2, OpeningOrientation.VERTICAL));

        hero.getInventory().add(new Compass(), 1);
        assertTrue(hero.getInventory().use(0, hero, level));

        assertAll(
                () -> assertTrue(hero.isCompassActive()),
                () -> assertEquals(5f, hero.getCompassEffect().getRemainingTime(), 1e-3f),
                () -> assertTrue(hero.getInventory().getStacks().isEmpty())
        );

        level.update(3f);
        assertTrue(hero.isCompassActive());

        level.update(2.1f);
        assertFalse(hero.isCompassActive());
    }

    @Test
    void computeAngleDegrees_pointsTowardCenterOfExit() {
        Character hero = new TestHero();
        Exit exitRight = new Exit(9, 2, OpeningOrientation.VERTICAL);

        float angle = hero.getCompassEffect().computeAngleDegrees(hero, exitRight);

        assertTrue(angle > -15f && angle < 15f, "La flèche doit pointer vers la droite, angle=" + angle);
    }

    @Test
    void resetForRetry_cancelsActiveCompass() {
        Character hero = new TestHero();
        Level<Character> level = createLevel(hero, new Exit(9, 4, OpeningOrientation.VERTICAL));

        new Compass().use(hero, level);
        assertTrue(hero.isCompassActive());

        hero.resetForRetry();
        assertFalse(hero.isCompassActive());
    }

    @Test
    void constructor_rejectsNonPositiveDuration() {
        assertThrows(IllegalArgumentException.class, () -> new Compass(0f));
    }
}
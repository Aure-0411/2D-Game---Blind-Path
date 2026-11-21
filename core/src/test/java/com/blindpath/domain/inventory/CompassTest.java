package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.character.Gnome;
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

    private Level<Gnome> createLevel(Gnome gnome, Exit exit) {
        Labyrinth labyrinth = new Labyrinth(10, 10, new Entrance(0, 4, OpeningOrientation.VERTICAL), exit, List.of());
        Monster monster = new Monster(new LabyrinthGrid(labyrinth), (g, c, target) -> c, new GridPosition(8, 8)) {};
        return new Level<>(gnome, labyrinth, new AabbCollisionWorld(), monster, (g, s, t) -> List.of(), List.of());
    }

        @Test
    void compassAndInventory_workWithAnyCharacterSubclass() {
        class TestCharacter extends Character {
            TestCharacter() { super(3f, 2f, 2f, 0.8f, 0.8f); }
        }
        TestCharacter customHero = new TestCharacter();
        Labyrinth labyrinth = new Labyrinth(10, 10, new Entrance(0, 4, OpeningOrientation.VERTICAL), new Exit(9, 2, OpeningOrientation.VERTICAL), List.of());
        Monster monster = new Monster(new LabyrinthGrid(labyrinth), (g, c, target) -> c, new GridPosition(8, 8)) {};
        Level<TestCharacter> level = new Level<>(customHero, labyrinth, new AabbCollisionWorld(), monster, (g, s, t) -> List.of(), List.of());

        customHero.getInventory().add(new Compass(), 1);
        assertTrue(customHero.getInventory().use(0, customHero, level));
        assertTrue(customHero.isCompassActive());
    }

    @Test
    void useFromInventory_activatesCompassForFiveSecondsAndConsumesItem() {
        Gnome gnome = new Gnome();
        gnome.setPosition(2f, 2f);
        Level<Gnome> level = createLevel(gnome, new Exit(9, 2, OpeningOrientation.VERTICAL));

        gnome.getInventory().add(new Compass(), 1);
        assertTrue(gnome.getInventory().use(0, gnome, level));

        assertAll(
                () -> assertTrue(gnome.isCompassActive()),
                () -> assertEquals(5f, gnome.getCompassEffect().getRemainingTime(), 1e-3f),
                () -> assertTrue(gnome.getInventory().getStacks().isEmpty())
        );

        level.update(3f);
        assertTrue(gnome.isCompassActive());

        level.update(2.1f);
        assertFalse(gnome.isCompassActive());
    }

    @Test
    void computeAngleDegrees_pointsTowardCenterOfExit() {
        Gnome gnome = new Gnome();
        gnome.setPosition(2f, 2f); // centre (2.5, 2.5)
        Exit exitRight = new Exit(9, 2, OpeningOrientation.VERTICAL); // taille 1x2 -> centre (9.5, 3.0)

        float angle = gnome.getCompassEffect().computeAngleDegrees(gnome, exitRight);

        assertTrue(angle > -15f && angle < 15f, "La flèche doit pointer vers la droite, angle=" + angle);
    }

    @Test
    void resetForRetry_cancelsActiveCompass() {
        Gnome gnome = new Gnome();
        Level<Gnome> level = createLevel(gnome, new Exit(9, 4, OpeningOrientation.VERTICAL));

        new Compass().use(gnome, level);
        assertTrue(gnome.isCompassActive());

        gnome.resetForRetry();
        assertFalse(gnome.isCompassActive());
    }

    @Test
    void constructor_rejectsNonPositiveDuration() {
        assertThrows(IllegalArgumentException.class, () -> new Compass(0f));
    }
}
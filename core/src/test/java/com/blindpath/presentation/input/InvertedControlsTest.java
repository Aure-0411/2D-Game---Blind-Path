package com.blindpath.presentation.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.badlogic.gdx.Input.Keys;

import static com.blindpath.domain.labyrinth.OpeningOrientation.VERTICAL;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.character.Direction;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.monster.Inverter;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.AabbCollisionWorld;

/**
 * Tests of the inverted controls (POC_EXTREME-3, #82): Level.areControlsInverted()
 * and InputController, which turns the pressed direction around when it is true.
 */
class InvertedControlsTest {

    private static final float DT = 0.1f;

    private Labyrinth labyrinth;
    private Character hero;
    private Pathfinder pathfinder;

    @BeforeEach
    void setUp() {
        labyrinth = new Labyrinth(
            20, 
            20,
            new Entrance(0, 10, VERTICAL), 
            new Exit(19, 10, VERTICAL), 
            List.of()
        );
        hero = new Character(3f, 10f, 10f, 0.8f, 0.8f) {};
        pathfinder = (grid, start, target) -> List.of();
    }

    /** Level whose monster stands at (column, row); its target is the hero's cell (10, 10). */
    private Level<Character> levelWithInverterAt(int column, int row) {
        Inverter inverter = new Inverter(new LabyrinthGrid(labyrinth, Monster.SIZE),
                (grid, current, goal) -> current, new GridPosition(column, row));
        inverter.setTarget(new GridPosition(10, 10));
        return new Level<>(hero, labyrinth, new AabbCollisionWorld(), inverter, pathfinder, List.of());
    }

    private Level<Character> levelWithOtherMonster() {
        Monster other = new Monster(new LabyrinthGrid(labyrinth, Monster.SIZE),
                (grid, current, goal) -> current, new GridPosition(10, 10)) {};
        other.setTarget(new GridPosition(10, 10));
        return new Level<>(hero, labyrinth, new AabbCollisionWorld(), other, pathfinder, List.of());
    }

    private static InputController pressing(Level<?> level, int key) {
        return new InputController(level, k -> k == key);
    }

    // ---------- Level.areControlsInverted ----------

    @Test
    void invertedWhenTheInverterIsClose() {
        assertTrue(levelWithInverterAt(12, 10).areControlsInverted());
    }

    @Test
    void notInvertedWhenTheInverterIsFar() {
        assertFalse(levelWithInverterAt(17, 10).areControlsInverted());
    }

    @Test
    void otherMonstersNeverInvert() {
        assertFalse(levelWithOtherMonster().areControlsInverted());
    }

    // ---------- InputController ----------

    @Test
    void normalControlsWhenFar() {
        Level<Character> level = levelWithInverterAt(17, 10);
        pressing(level, Keys.D).handleInput(DT);
        assertTrue(hero.getX() > 10f, "D goes right");
        assertEquals(Direction.RIGHT, hero.getFacing());
    }

    @Test
    void rightKeyGoesLeftWhenInverted() {
        Level<Character> level = levelWithInverterAt(12, 10);
        pressing(level, Keys.D).handleInput(DT);
        assertTrue(hero.getX() < 10f, "inverted: D goes left");
        assertEquals(Direction.LEFT, hero.getFacing(), "the hero looks where it really goes");
    }

    @Test
    void upKeyGoesDownWhenInverted() {
        Level<Character> level = levelWithInverterAt(12, 10);
        pressing(level, Keys.W).handleInput(DT);
        assertTrue(hero.getY() < 10f, "inverted: W goes down");
    }
}
package com.blindpath.presentation.input;

import static com.blindpath.domain.labyrinth.OpeningOrientation.VERTICAL;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;

import com.badlogic.gdx.Input.Keys;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.character.Direction;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.inventory.Compass;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.physics.AabbCollisionWorld;

class InputControllerTest {

    private static IntPredicate pressed(int... keys) {
        Set<Integer> set = new HashSet<>();
        for (int k : keys) set.add(k);
        return set::contains;
    }

    @Test
    void noKey_givesNone() {
        assertEquals(Direction.NONE, InputController.readDirection(pressed()));
    }

    @Test
    void wasd_givesCardinalDirections() {
        assertEquals(Direction.UP, InputController.readDirection(pressed(Keys.W)));
        assertEquals(Direction.LEFT, InputController.readDirection(pressed(Keys.A)));
        assertEquals(Direction.DOWN, InputController.readDirection(pressed(Keys.S)));
        assertEquals(Direction.RIGHT, InputController.readDirection(pressed(Keys.D)));
    }

    @Test
    void arrows_giveCardinalDirections() {
        assertEquals(Direction.UP, InputController.readDirection(pressed(Keys.UP)));
        assertEquals(Direction.LEFT, InputController.readDirection(pressed(Keys.LEFT)));
        assertEquals(Direction.DOWN, InputController.readDirection(pressed(Keys.DOWN)));
        assertEquals(Direction.RIGHT, InputController.readDirection(pressed(Keys.RIGHT)));
    }

    @Test
    void twoKeys_giveDiagonals() {
        assertEquals(Direction.UP_RIGHT, InputController.readDirection(pressed(Keys.W, Keys.D)));
        assertEquals(Direction.DOWN_LEFT, InputController.readDirection(pressed(Keys.S, Keys.A)));
        assertEquals(Direction.UP_LEFT, InputController.readDirection(pressed(Keys.UP, Keys.LEFT)));
    }

    @Test
    void wasdAndArrowsTogether_doNotDoubleCount() {
        assertEquals(Direction.UP, InputController.readDirection(pressed(Keys.W, Keys.UP)));
    }

    @Test
    void oppositeKeys_cancelOut() {
        assertEquals(Direction.NONE, InputController.readDirection(pressed(Keys.A, Keys.D)));
        assertEquals(Direction.NONE, InputController.readDirection(pressed(Keys.W, Keys.S)));
        assertEquals(Direction.RIGHT, InputController.readDirection(pressed(Keys.W, Keys.S, Keys.D)));
    }

    @Test
    void handleInput_worksWithAnyCharacterSubclassAndActivatesInventorySlot() {
        Character hero = new Character(3f, 2f, 2f, 0.8f, 0.8f) {};
        Labyrinth labyrinth = new Labyrinth(10, 10,
                new Entrance(0, 4, VERTICAL), new Exit(9, 4, VERTICAL), List.of());
        Monster monster = new Monster(new LabyrinthGrid(labyrinth),
                (grid, current, goal) -> current, new GridPosition(8, 8)) {};
        Level<Character> level = new Level<>(hero, labyrinth, new AabbCollisionWorld(),
                monster, (grid, start, target) -> List.of(), List.of());

        hero.getInventory().add(new Compass(), 1);
        InputController controller = new InputController(level, hero, pressed(Keys.D), pressed(Keys.NUM_1));

        controller.handleInput(0.1f);

        assertTrue(controller.isMoving());
        assertTrue(hero.getX() > 2f);
        assertTrue(hero.isCompassActive());
        assertFalse(hero.getInventory().getStacks().iterator().hasNext());
    }
}
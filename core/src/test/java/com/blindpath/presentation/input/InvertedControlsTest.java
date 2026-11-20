package com.blindpath.presentation.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.Input.Keys;
import com.blindpath.domain.character.Direction;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.monster.Inverter;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.AabbCollisionWorld;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the inverted controls (POC_EXTREME-3, #82): Level.areControlsInverted()
 * and InputController, which turns the pressed direction around when it is true.
 */
class InvertedControlsTest {

    private static final float DT = 0.1f;

    private Labyrinth labyrinth;
    private Gnome gnome;
    private Pathfinder pathfinder;

    @BeforeEach
    void setUp() {
        labyrinth = new Labyrinth(20, 20,
                new Entrance(0, 10, Border.LEFT), new Exit(19, 10, Border.RIGHT), List.of());
        gnome = new Gnome();
        gnome.setPosition(10f, 10f);
        pathfinder = (grid, start, target) -> List.of();
    }

    /** Level whose monster stands at (column, row); its target is the gnome's cell (10, 10). */
    private Level levelWithInverterAt(int column, int row) {
        Inverter inverter = new Inverter(new LabyrinthGrid(labyrinth, Monster.SIZE),
                (grid, current, goal) -> current, new GridPosition(column, row));
        inverter.setTarget(new GridPosition(10, 10));
        return new Level(gnome, labyrinth, new AabbCollisionWorld(), inverter, pathfinder, List.of());
    }

    private Level levelWithOtherMonster() {
        Monster other = new Monster(new LabyrinthGrid(labyrinth, Monster.SIZE),
                (grid, current, goal) -> current, new GridPosition(10, 10)) {};
        other.setTarget(new GridPosition(10, 10));
        return new Level(gnome, labyrinth, new AabbCollisionWorld(), other, pathfinder, List.of());
    }

    private static InputController pressing(Level level, int key) {
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
        Level level = levelWithInverterAt(17, 10);
        pressing(level, Keys.D).handleInput(DT);
        assertTrue(gnome.getX() > 10f, "D goes right");
        assertEquals(Direction.RIGHT, gnome.getFacing());
    }

    @Test
    void rightKeyGoesLeftWhenInverted() {
        Level level = levelWithInverterAt(12, 10);
        pressing(level, Keys.D).handleInput(DT);
        assertTrue(gnome.getX() < 10f, "inverted: D goes left");
        assertEquals(Direction.LEFT, gnome.getFacing(), "the gnome looks where it really goes");
    }

    @Test
    void upKeyGoesDownWhenInverted() {
        Level level = levelWithInverterAt(12, 10);
        pressing(level, Keys.W).handleInput(DT);
        assertTrue(gnome.getY() < 10f, "inverted: W goes down");
    }
}

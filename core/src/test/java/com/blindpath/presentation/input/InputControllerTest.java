package com.blindpath.presentation.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.gdx.Input.Keys;
import com.blindpath.domain.character.Direction;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntPredicate;
import org.junit.jupiter.api.Test;

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
        assertEquals(Direction.LEFT,  InputController.readDirection(pressed(Keys.A)));
        assertEquals(Direction.DOWN, InputController.readDirection(pressed(Keys.S)));
        assertEquals(Direction.RIGHT,  InputController.readDirection(pressed(Keys.D)));
    }

    @Test
    void arrows_giveCardinalDirections() {
        assertEquals(Direction.UP, InputController.readDirection(pressed(Keys.UP)));
        assertEquals(Direction.LEFT,  InputController.readDirection(pressed(Keys.LEFT)));
        assertEquals(Direction.DOWN, InputController.readDirection(pressed(Keys.DOWN)));
        assertEquals(Direction.RIGHT,  InputController.readDirection(pressed(Keys.RIGHT)));
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
}
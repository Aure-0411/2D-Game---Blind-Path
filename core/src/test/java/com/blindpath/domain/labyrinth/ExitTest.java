package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Character;

class ExitTest {

    private static Character newHero() {
        return new Character(3f, 0f, 0f, 0.8f, 0.8f) {};
    }

    @Test
    void verticalExitHasOneByTwoHitbox() {
        Exit exit = new Exit(9, 4, OpeningOrientation.VERTICAL);

        assertAll(
                () -> assertEquals(9, exit.getX()),
                () -> assertEquals(4, exit.getY()),
                () -> assertEquals(1, exit.getWidth()),
                () -> assertEquals(2, exit.getHeight()),
                () -> assertEquals(OpeningOrientation.VERTICAL, exit.getOrientation()));
    }

    @Test
    void horizontalExitHasTwoByOneHitbox() {
        Exit exit = new Exit(4, 9, OpeningOrientation.HORIZONTAL);

        assertAll(
                () -> assertEquals(2, exit.getWidth()),
                () -> assertEquals(1, exit.getHeight()),
                () -> assertEquals(OpeningOrientation.HORIZONTAL, exit.getOrientation()));
    }

    @Test
    void isReached_detectsHeroEnteringExit() {
        Exit exit = new Exit(9, 4, OpeningOrientation.VERTICAL);
        Character hero = newHero();

        hero.setPosition(3f, 4f);
        assertFalse(exit.isReached(hero));

        hero.setPosition(8.5f, 4.5f);
        assertTrue(exit.isReached(hero));
        assertFalse(exit.isReached(null));
    }

    @Test
    void constructor_rejectsNullOrientation() {
        assertThrows(IllegalArgumentException.class, () -> new Exit(0, 0, null));
    }
}
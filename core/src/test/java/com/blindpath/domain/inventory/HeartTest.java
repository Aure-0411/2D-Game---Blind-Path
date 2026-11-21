package com.blindpath.domain.inventory;

import static com.blindpath.domain.inventory.InventoryTestSupport.level;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

class HeartTest {

    private static Character newHero() {
        return new Character(3f, 0f, 0f, 0.8f, 0.8f) {};
    }

    @Test
    void restoresUpToTwoHalfHeartsWithoutExceedingMaxLife() {
        Heart heart = new Heart();
        Character hero = newHero();

        hero.setLife(hero.getMaxLife() - 1);
        assertTrue(heart.canUse(hero, level(hero)));
        heart.use(hero, level(hero));
        assertEquals(hero.getMaxLife(), hero.getLife());

        hero.setLife(1);
        heart.use(hero, level(hero));
        assertEquals(3, hero.getLife());
    }

    @Test
    void cannotBeUsedWhenFullOrDeadAndDirectUseDoesNothing() {
        Heart heart = new Heart();
        Character hero = newHero();
        for (int life : new int[] {hero.getMaxLife(), 0}) {
            hero.setLife(life);
            assertFalse(heart.canUse(hero, level(hero)));
            heart.use(hero, level(hero));
            assertEquals(life, hero.getLife());
        }
    }

    @Test
    void missingContextIsRejected() {
        Character hero = newHero();
        Level<?> level = level(hero);
        Heart heart = new Heart();
        assertThrows(IllegalArgumentException.class, () -> heart.canUse(null, level));
        assertThrows(IllegalArgumentException.class, () -> heart.canUse(hero, null));
        assertThrows(IllegalArgumentException.class, () -> heart.use(null, level));
        assertThrows(IllegalArgumentException.class, () -> heart.use(hero, null));
    }
}
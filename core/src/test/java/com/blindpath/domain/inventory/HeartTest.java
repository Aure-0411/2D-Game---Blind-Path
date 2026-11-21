package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;
import static com.blindpath.domain.inventory.InventoryTestSupport.level;

class HeartTest {

    private static Character newHero() {
        return new Character(3f, 0f, 0f, 0.8f, 0.8f) {};
    }

    @Test void restoresOneFullHeartWhenMissingAtLeastTwoHalfHearts() {
        Heart heart = new Heart();
        Character hero = newHero();
        hero.setLife(hero.getMaxLife() - 2);
        assertTrue(heart.canUse(hero, level(hero)));
        heart.use(hero, level(hero));
        assertEquals(hero.getMaxLife(), hero.getLife());
        hero.setLife(1);
        heart.use(hero, level(hero));
        assertEquals(3, hero.getLife());
    }

    @Test void cannotBeUsedWhenFullDeadOrMissingOnlyOneHalfHeart() {
        Heart heart = new Heart();
        Character hero = newHero();
        for (int life : new int[] {hero.getMaxLife(), hero.getMaxLife() - 1, 0}) {
            hero.setLife(life);
            assertFalse(heart.canUse(hero, level(hero)));
            assertThrows(IllegalStateException.class, () -> heart.use(hero, level(hero)));
            assertEquals(life, hero.getLife());
        }
    }

    @Test void missingContextIsRejected() {
        Character hero = newHero();
        Level<?> level = level(hero);
        Heart heart = new Heart();
        assertThrows(IllegalArgumentException.class, () -> heart.canUse(null, level));
        assertThrows(IllegalArgumentException.class, () -> heart.canUse(hero, null));
        assertThrows(IllegalArgumentException.class, () -> heart.use(null, level));
        assertThrows(IllegalArgumentException.class, () -> heart.use(hero, null));
    }
}
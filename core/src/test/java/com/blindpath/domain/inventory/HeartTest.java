package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;

class HeartTest {
    @Test void injuredHeroGainsExactlyOneLife() {
        Gnome hero = new Gnome();
        Level level = InventoryTestSupport.level(hero);
        hero.setLife(hero.getMaxLife() - 2);
        Heart heart = new Heart();
        assertTrue(heart.canUse(hero, level));
        heart.use(hero, level);
        assertEquals(hero.getMaxLife() - 1, hero.getLife());
        heart.use(hero, level);
        assertEquals(hero.getMaxLife(), hero.getLife());
    }
    @Test void fullHealthAndDeadHeroesCannotBeHealed() {
        Gnome hero = new Gnome();
        Level level = InventoryTestSupport.level(hero);
        Heart heart = new Heart();
        assertFalse(heart.canUse(hero, level));
        heart.use(hero, level);
        assertEquals(hero.getMaxLife(), hero.getLife());
        hero.setLife(0);
        assertFalse(heart.canUse(hero, level));
        heart.use(hero, level);
        assertEquals(0, hero.getLife());
    }
    @Test void missingContextIsRejected() {
        Gnome hero = new Gnome();
        Level level = InventoryTestSupport.level(hero);
        Heart heart = new Heart();
        assertThrows(IllegalArgumentException.class, () -> heart.canUse(null, level));
        assertThrows(IllegalArgumentException.class, () -> heart.canUse(hero, null));
        assertThrows(IllegalArgumentException.class, () -> heart.use(null, level));
        assertThrows(IllegalArgumentException.class, () -> heart.use(hero, null));
    }
}

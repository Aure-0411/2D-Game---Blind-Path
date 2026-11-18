package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;

class ItemTest {
    @Test void nameIsPreserved() {
        assertEquals("Sword", new InventoryTestSupport.TestItem("Sword").getName());
    }
    @Test void missingOrBlankNamesAreRejected() {
        for (String name : new String[] {null, "", "   ", "\t\n"}) {
            assertThrows(IllegalArgumentException.class,
                () -> new InventoryTestSupport.TestItem(name));
        }
    }
    @Test void defaultCanUseRequiresUserAndLevel() {
        Item item = new Item("Base") {
            @Override public void use(Gnome user, Level level) {}
        };
        Gnome hero = new Gnome();
        Level level = InventoryTestSupport.level(hero);
        assertTrue(item.canUse(hero, level));
        assertThrows(IllegalArgumentException.class, () -> item.canUse(null, level));
        assertThrows(IllegalArgumentException.class, () -> item.canUse(hero, null));
    }
}

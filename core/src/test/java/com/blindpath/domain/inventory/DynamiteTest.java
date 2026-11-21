package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;

class DynamiteTest {

    @Test
    void use_withLivingUser_placesDynamite() {
        Gnome gnome = new Gnome();
        Level level = InventoryTestSupport.level(gnome);
        Dynamite dynamite = new Dynamite();

        dynamite.use(gnome, level);

        assertEquals(1, level.getPlacedObjects().size());
    }

    @Test
    void use_withDeadUser_placesNothing() {
        Gnome gnome = new Gnome();
        Level level = InventoryTestSupport.level(gnome);
        Dynamite dynamite = new Dynamite();
        gnome.setLife(0);

        dynamite.use(gnome, level);

        assertEquals(0, level.getPlacedObjects().size());
    }
}
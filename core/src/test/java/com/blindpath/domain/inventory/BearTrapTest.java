package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;

class BearTrapTest {

    @Test
    void use_withLivingUser_placesBearTrap() {
        Gnome gnome = new Gnome();
        Level level = InventoryTestSupport.level(gnome);
        BearTrap bearTrap = new BearTrap();

        bearTrap.use(gnome, level);

        assertEquals(1, level.getPlacedObjects().size());
    }

    @Test
    void use_withDeadUser_placesNothing() {
        Gnome gnome = new Gnome();
        Level level = InventoryTestSupport.level(gnome);
        BearTrap bearTrap = new BearTrap();
        gnome.setLife(0);

        bearTrap.use(gnome, level);

        assertEquals(0, level.getPlacedObjects().size());
    }
}
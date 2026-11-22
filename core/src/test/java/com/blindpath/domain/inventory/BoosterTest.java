package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;

class BoosterTest {
    @Test
    void use_withLivingUser_boostLantern() {
        Gnome gnome = new Gnome();
        Level level = InventoryTestSupport.level(gnome);
        Booster booster = new Booster();

        booster.use(gnome, level);

        assertTrue(gnome.isLanternBoosted());
    }

    @Test
    void use_withDeadUser_doesNotBoostLantern() {
        Gnome gnome = new Gnome();
        Level level = InventoryTestSupport.level(gnome);
        Booster booster = new Booster();
        gnome.setLife(0);

        booster.use(gnome, level);

        assertFalse(gnome.isLanternBoosted());
    }

    @Test
    void canUse_whileLanternBoosted_returnsFalse() {
        Gnome gnome = new Gnome();
        Level level = InventoryTestSupport.level(gnome);
        Booster booster = new Booster();

        booster.use(gnome, level);

        assertFalse(booster.canUse(gnome, level));
    }
}
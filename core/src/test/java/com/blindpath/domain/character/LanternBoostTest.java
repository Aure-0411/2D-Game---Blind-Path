package com.blindpath.domain.character;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LanternBoostTest {
    @Test
    void isLanternBoosted_atStart_returnsFalse() {
        Gnome gnome = new Gnome();

        assertFalse(gnome.isLanternBoosted());
    }

    @Test
    void isLanternBoosted_afterBoost_returnsTrue() {
        Gnome gnome = new Gnome();

        gnome.boostLantern(5);

        assertTrue(gnome.isLanternBoosted());
    }

    @Test
    void isLanternBoosted_afterFiveSnaps_returnsFalse() {
        Gnome gnome = new Gnome();
        gnome.boostLantern(5);

        for (int i = 0; i < 5; i++) {
            gnome.consumeBoostedSnap();
        }

        assertFalse(gnome.isLanternBoosted());
    }

    @Test
    void consumeBoostedSnap_afterFiveSnaps_returnsFalse() {
        Gnome gnome = new Gnome();
        gnome.boostLantern(5);

        for (int i = 0; i < 5; i++) {
            gnome.consumeBoostedSnap();
        }

        assertFalse(gnome.consumeBoostedSnap());
    }

    @Test
    void isLanternBoosted_afterDeath_returnsFalse() {
        Gnome gnome = new Gnome();
        gnome.boostLantern(5);

        gnome.resetForRetry();

        assertFalse(gnome.isLanternBoosted());
    }
}
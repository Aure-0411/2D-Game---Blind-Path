package com.blindpath.domain.character;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the gnome stun (POC_HARD-2, #73): the Screamer's scream stuns the gnome.
 * Same model as the immunity: a timer in seconds, decreased by updateStun(deltaTime).
 */
class GnomeStunTest {

    private Gnome gnome;

    @BeforeEach
    void setUp() {
        gnome = new Gnome();
    }

    @Test
    void notStunnedAtStart() {
        assertFalse(gnome.isStunned());
    }

    @Test
    void stunnedAfterStun() {
        gnome.stun(2f);
        assertTrue(gnome.isStunned());
    }

    @Test
    void stillStunnedBeforeTheEnd() {
        gnome.stun(2f);
        gnome.updateStun(1.9f);
        assertTrue(gnome.isStunned(), "1.9 s < 2 s: still stunned");
    }

    @Test
    void noLongerStunnedAfterTheDuration() {
        gnome.stun(2f);
        gnome.updateStun(1f);
        gnome.updateStun(1f);
        assertFalse(gnome.isStunned(), "2 s elapsed: free again");
    }

    @Test
    void aShorterStunDoesNotCutALongerOne() {
        gnome.stun(2f);
        gnome.stun(0.5f);
        gnome.updateStun(1f);
        assertTrue(gnome.isStunned(), "the longest stun wins");
    }

    @Test
    void negativeDurationIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> gnome.stun(-1f));
    }

    @Test
    void invincibleGnomeIsNotStunned() {
        gnome.toggleInvincible();
        gnome.stun(2f);
        assertFalse(gnome.isStunned(), "debug key I: no damage and no stun");
    }

    @Test
    void retryRemovesTheStun() {
        gnome.stun(2f);
        gnome.resetForRetry();
        assertFalse(gnome.isStunned());
    }

    @Test
    void stunDoesNotHurt() {
        int life = gnome.getLife();
        gnome.stun(2f);
        assertTrue(gnome.getLife() == life, "the scream stuns but does not remove life");
    }
}

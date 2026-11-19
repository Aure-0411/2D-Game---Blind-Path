package com.blindpath.domain.character;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests de la vie du gnome (POC_EASY-19, #127).
 * La vie est comptée en demi-cœurs : 1 cœur = 2 PV, 3 cœurs = 6 PV.
 */
class GnomeLifeTest {

    private Gnome gnome;

    @BeforeEach
    void setUp() {
        gnome = new Gnome();
    }

    // ---- Valeurs de départ ----

    @Test
    void newGnome_hasThreeFullHearts() {
        assertEquals(6, Character.MAX_LIFE);
        assertEquals(Character.MAX_LIFE, gnome.getMaxLife());
        assertEquals(Character.MAX_LIFE, gnome.getLife());
    }

    @Test
    void newGnome_isNotImmune() {
        assertFalse(gnome.isImmune());
    }

    @Test
    void gnome_isDamageable() {
        Damageable target = gnome;
        target.takeDamage(1);

        assertEquals(5, gnome.getLife());
    }

    // ---- takeDamage ----

    @Test
    void takeDamage_halfHeart_removesOnePoint() {
        gnome.takeDamage(1);

        assertEquals(5, gnome.getLife());
    }

    @Test
    void takeDamage_makesGnomeImmune() {
        gnome.takeDamage(1);

        assertTrue(gnome.isImmune());
    }

    @Test
    void takeDamage_whileImmune_isIgnored() {
        gnome.takeDamage(2);
        gnome.takeDamage(2);

        assertEquals(4, gnome.getLife());
    }

    @Test
    void takeDamage_neverGoesBelowZero() {
        gnome.takeDamage(100);

        assertEquals(0, gnome.getLife());
        assertTrue(gnome.isDead());
    }

    // ---- takeMonsterHit (1 cœur complet) ----

    @Test
    void monsterDamage_isOneFullHeart() {
        assertEquals(2, Gnome.MONSTER_DAMAGE);
    }

    @Test
    void takeMonsterHit_removesOneFullHeart() {
        gnome.takeMonsterHit();

        assertEquals(4, gnome.getLife());
        assertTrue(gnome.isImmune());
    }

    @Test
    void takeMonsterHit_whileImmune_isIgnored() {
        gnome.takeMonsterHit();
        gnome.takeMonsterHit();

        assertEquals(4, gnome.getLife());
    }

    // ---- Fin de l'immunité ----

    @Test
    void immunity_stillActiveJustBeforeTwoSeconds() {
        gnome.takeMonsterHit();
        gnome.updateImmunity(Gnome.IMMUNITY_DURATION - 0.1f);

        assertTrue(gnome.isImmune());
    }

    @Test
    void immunity_endsAfterTwoSeconds_andGnomeCanBeHitAgain() {
        gnome.takeMonsterHit();
        gnome.updateImmunity(Gnome.IMMUNITY_DURATION);

        assertFalse(gnome.isImmune());

        gnome.takeMonsterHit();
        assertEquals(2, gnome.getLife());
    }

    // ---- Mort ----

    @Test
    void threeMonsterHits_killTheGnome() {
        for (int i = 0; i < 3; i++) {
            gnome.takeMonsterHit();
            gnome.updateImmunity(Gnome.IMMUNITY_DURATION);
        }

        assertEquals(0, gnome.getLife());
        assertTrue(gnome.isDead());
    }

    @Test
    void halfHeartHits_needSixHitsToKill() {
        for (int i = 0; i < 5; i++) {
            gnome.takeDamage(1);
            gnome.updateImmunity(Gnome.IMMUNITY_DURATION);
        }
        assertFalse(gnome.isDead());
        assertEquals(1, gnome.getLife());

        gnome.takeDamage(1);
        assertTrue(gnome.isDead());
    }
}

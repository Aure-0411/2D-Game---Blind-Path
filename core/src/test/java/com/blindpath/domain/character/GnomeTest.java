package com.blindpath.domain.character;

import static org.junit.jupiter.api.Assertions.*;

import com.blindpath.domain.labyrinth.Bounds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GnomeTest {

    private Gnome gnome;

    @BeforeEach
    void setUp() {
        gnome = new Gnome();
        gnome.setPosition(1f, 1f);
    }

    @Test
    void newGnome_startsWithFullLife() {
        assertEquals(gnome.getMaxLife(), gnome.getLife());
    }

    @Test
    void newGnome_defaultSpeed() {
        assertEquals(2.75f, gnome.getSpeed());
    }

    @Test
    void newGnome_fitsInTwoCellCorridor() {
        // doit pouvoir passer deux cases
        assertTrue(gnome.getWidth() < 2f);
        assertTrue(gnome.getHeight() < 2f);
    }

    @Test
    void setPosition_updatesCoordinates() {
        gnome.setPosition(2.5f, 3.5f);

        assertEquals(2.5f, gnome.getX());
        assertEquals(3.5f, gnome.getY());
    }

    @Test
    void getBounds_followsPositionAndSize() {
        gnome.setPosition(2f, 3f);
        Bounds bounds = gnome.getBounds();

        assertTrue(bounds.intersects(new Bounds(2f, 3f, gnome.getWidth(), gnome.getHeight())));
        assertFalse(bounds.intersects(new Bounds(10f, 10f, 1f, 1f)));
    }

    @Test
    void gnome_isCollidable_andCollidesWithOverlappingWall() {
        gnome.setPosition(2f, 2f);

        assertTrue(gnome.canCollide());
        assertTrue(gnome.collide(new com.blindpath.domain.labyrinth.Wall(2.5f, 2.5f, 1f, 1f)));
        assertFalse(gnome.collide(new com.blindpath.domain.labyrinth.Wall(5f, 5f, 1f, 1f)));
    }

    @Test
    void newGnome_facesSouthByDefault() {
        assertEquals(Direction.DOWN, gnome.getFacing());
    }

    @Test
    void setFacing_remembersDirection() {
        gnome.setFacing(Direction.UP_RIGHT);

        assertEquals(Direction.UP_RIGHT, gnome.getFacing());
    }

    @Test
    void setFacing_noneKeepsLastDirection() {
        gnome.setFacing(Direction.LEFT);
        gnome.setFacing(Direction.NONE);

        assertEquals(Direction.LEFT, gnome.getFacing());
    }

    @Test
    void takeMonsterHitRemovesTwoHalfHearts() {
        gnome.takeMonsterHit();

        assertEquals(4, gnome.getLife());
    }

    @Test
    void takeMonsterHitStartsImmunity() {
        gnome.takeMonsterHit();

        assertTrue(gnome.isImmune());
    }

    @Test
    void secondHitDuringImmunityRemovesNothing() {
        gnome.takeMonsterHit();
        gnome.takeMonsterHit();

        assertEquals(4, gnome.getLife());
    }

    @Test
    void immunityEndsAfterTwoSeconds() {
        gnome.takeMonsterHit();

        gnome.updateImmunity(2f);

        assertFalse(gnome.isImmune());
    }
}
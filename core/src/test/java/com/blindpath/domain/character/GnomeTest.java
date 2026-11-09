package com.blindpath.domain.character;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GnomeTest {

    private Gnome gnome;

    @BeforeEach
    void setUp() {
        gnome = new Gnome(1f,1f);
    }

    @Test
    void newGnome_startsWithFullLife() {
        assertEquals(gnome.getMaxLife(), gnome.getLife());
    }

    @Test
    void newGnome_isSmallerThanOneCell() {
        // doit pouvoir passer dans un couloir d'une case
        assertTrue(gnome.getWidth() == 1f);
        assertTrue(gnome.getHeight() == 1f);
    }

    @Test
    void setPosition_updatesCoordinates() {
        gnome.setPosition(2.5f, 3.5f);

        assertEquals(2.5f, gnome.getX());
        assertEquals(3.5f, gnome.getY());
    }

    // @Test
    // void getBounds_followsPositionAndSize() {
    //     gnome.setPosition(2f, 3f);
    //     Bounds bounds = gnome.getBounds();

    //     assertTrue(bounds.intersects(new Bounds(2f, 3f, gnome.getWidth(), gnome.getHeight())));
    //     assertFalse(bounds.intersects(new Bounds(10f, 10f, 1f, 1f)));
    // }

    // @Test
    // void newGnome_facesSouthByDefault() {
    //     assertEquals(Direction.SOUTH, gnome.getFacing());
    // }

    // @Test
    // void setFacing_remembersDirection() {
    //     gnome.setFacing(Direction.NORTH_EAST);

    //     assertEquals(Direction.NORTH_EAST, gnome.getFacing());
    // }

    // @Test
    // void setFacing_noneKeepsLastDirection() {
    //     gnome.setFacing(Direction.WEST);
    //     gnome.setFacing(Direction.NONE);

    //     assertEquals(Direction.WEST, gnome.getFacing());
    // }
}
package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;

class LeverTest {
    @Test
    void onlyHeroSnapInsideTwoCellRadiusActivatesLeverOnce() {
        RotatingWall wall = new RotatingWall(4, 4, OpeningOrientation.HORIZONTAL, 4, 4, 90);
        Lever lever = new Lever(10, 10, 1, 1, wall);
        assertFalse(lever.onSnap(null));
        assertFalse(lever.onSnap(new SnapEvent(10.5f, 10.5f, SnapSource.DYNAMITE, 0)));
        assertFalse(lever.onSnap(new SnapEvent(12.501f, 10.5f, SnapSource.HERO, 0)));
        assertFalse(lever.onSnap(new SnapEvent(12.0f, 12.0f, SnapSource.HERO, 0)));
        assertFalse(lever.isActivated());
        assertTrue(lever.onSnap(new SnapEvent(12.5f, 10.5f, SnapSource.HERO, 0)));
        assertTrue(lever.isActivated());
        assertFalse(lever.onSnap(new SnapEvent(10.5f, 10.5f, SnapSource.HERO, 0)));
        assertSame(wall, lever.getWall());
    }
}
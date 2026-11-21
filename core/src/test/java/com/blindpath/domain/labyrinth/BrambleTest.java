package com.blindpath.domain.labyrinth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;

class BrambleTest {

    private SnapEvent snap(float x, float y) {
        return new SnapEvent(x, y, SnapSource.HERO, 0);
    }

    @Test
    void snapRangeIsThreeCellsFromGroundAreaIncludingBoundary() {
        Bramble bramble = new Bramble(4, 4, OpeningOrientation.HORIZONTAL);
        bramble.onSnap(snap(9.01f, 4.5f));
        assertFalse(bramble.isActive());
        bramble.onSnap(snap(9f, 4.5f));
        assertTrue(bramble.isActive());
        bramble.deactivate();
        bramble.onSnap(snap(0.9f, 4.5f));
        assertFalse(bramble.isActive());
        bramble.onSnap(snap(1f, 4.5f));
        assertTrue(bramble.isActive());
    }

    @Test
    void diagonalDistanceIsCircularAndOtherSourcesDoNotTriggerRonces() {
        Bramble bramble = new Bramble(4, 4, OpeningOrientation.HORIZONTAL);
        bramble.onSnap(snap(8.5f, 7.5f));
        assertFalse(bramble.isActive());
        bramble.onSnap(new SnapEvent(5, 4, SnapSource.DYNAMITE, 0));
        assertFalse(bramble.isActive());
        bramble.onSnap(null);
        assertFalse(bramble.isActive());
    }

    @ParameterizedTest
    @CsvSource({"HORIZONTAL, 4, 3, 2, 3", "VERTICAL, 3, 4, 3, 2"})
    void extendsInstantlyAndStaysFullyExtendedForHalfASecond(
            OpeningOrientation orientation, float x, float y, float width, float height) {
        Bramble bramble = new Bramble(4, 4, orientation);
        bramble.activate();
        assertEquals(1f, bramble.getExtension());
        Bounds bounds = bramble.getDamageBounds();
        assertEquals(x, bounds.getX());
        assertEquals(y, bounds.getY());
        assertEquals(width, bounds.getWidth());
        assertEquals(height, bounds.getHeight());
        bramble.update(0.25f);
        bramble.onSnap(snap(4, 4)); // Ne prolonge pas une activation en cours.
        bramble.update(0.249f);
        assertTrue(bramble.isActive());
        assertEquals(1f, bramble.getExtension());
        bramble.update(0.001f);
        assertFalse(bramble.isActive());
        assertEquals(0f, bramble.getExtension());
        bramble.onSnap(snap(4, 4));
        assertTrue(bramble.isActive());
    }

    @Test
    void damageAreaExpandsInstantlyAndDoesNotHurtAtRestOrOnBoundary() {
        Bramble bramble = new Bramble(4, 4, OpeningOrientation.HORIZONTAL);
        Bounds extension = new Bounds(4, 3.1f, 1, 0.4f);
        assertFalse(bramble.damages(new Bounds(4, 4, 1, 1)));
        bramble.activate();
        assertTrue(bramble.damages(extension));
        assertFalse(bramble.damages(new Bounds(4, 2, 1, 1)));
        bramble.update(0.5f);
        assertFalse(bramble.damages(extension));
    }

    @ParameterizedTest
    @CsvSource({"HORIZONTAL, 2, 1", "VERTICAL, 1, 2"})
    void boundsMatchOrientation(OpeningOrientation orientation, float width, float height) {
        Bramble bramble = new Bramble(3.5f, 4f, orientation);
        Bounds bounds = bramble.getBounds();
        assertEquals(orientation, bramble.getOrientation());
        assertEquals(3.5f, bounds.getX());
        assertEquals(4f, bounds.getY());
        assertEquals(width, bounds.getWidth());
        assertEquals(height, bounds.getHeight());
    }

    @Test
    void startsAtRestAndCanBeActivatedAndDeactivated() {
        Bramble bramble = new Bramble(0, 0, OpeningOrientation.HORIZONTAL);
        assertFalse(bramble.isActive());
        bramble.activate();
        bramble.activate();
        assertTrue(bramble.isActive());
        bramble.deactivate();
        bramble.deactivate();
        assertFalse(bramble.isActive());
    }

    @Test
    void activationDoesNotChangeGroundArea() {
        Bramble bramble = new Bramble(3, 4, OpeningOrientation.VERTICAL);
        Bounds before = bramble.getBounds();
        bramble.activate();
        Bounds after = bramble.getBounds();
        assertNotSame(before, after);
        assertEquals(before.getX(), after.getX());
        assertEquals(before.getY(), after.getY());
        assertEquals(before.getWidth(), after.getWidth());
        assertEquals(before.getHeight(), after.getHeight());
    }

    @Test
    void rejectsMissingOrientationAndNonFinitePosition() {
        assertThrows(IllegalArgumentException.class, () -> new Bramble(0, 0, null));
        for (float invalid : new float[] {Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Bramble(invalid, 0, OpeningOrientation.HORIZONTAL));
            assertThrows(IllegalArgumentException.class,
                    () -> new Bramble(0, invalid, OpeningOrientation.HORIZONTAL));
        }
    }
}

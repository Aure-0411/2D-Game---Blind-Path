package com.blindpath.domain.light;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
 
import org.junit.jupiter.api.Test;
 
class SnapEventTest {
 
    @Test
    void eventKeepsItsValues() {
        SnapEvent event = new SnapEvent(4f, 7.5f, SnapSource.DYNAMITE, 12L);
        assertEquals(4f, event.getSourceX(), "x of the snap");
        assertEquals(7.5f, event.getSourceY(), "y of the snap");
        assertEquals(SnapSource.DYNAMITE, event.getSource(), "source of the snap");
        assertEquals(12L, event.getGroupId(), "group of the snap");
    }
 
    @Test
    void doubleSnapSharesOneGroup() {
        SnapEvent first = new SnapEvent(1f, 1f, SnapSource.DOUBLE_SNAP, 3L);
        SnapEvent second = new SnapEvent(1f, 1f, SnapSource.DOUBLE_SNAP, 3L);
        assertEquals(first.getGroupId(), second.getGroupId(), "the two snaps of a double snap count as one group");
    }
 
    @Test
    void fourSourcesExist() {
        assertEquals(4, SnapSource.values().length, "HERO, DYNAMITE, DASH, DOUBLE_SNAP");
        assertEquals(SnapSource.HERO, SnapSource.valueOf("HERO"), "the hero is a snap source");
    }
}
 
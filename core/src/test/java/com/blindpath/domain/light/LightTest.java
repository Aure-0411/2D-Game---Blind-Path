package com.blindpath.domain.light;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
 
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
/**
 * Tests of {@link Light} with the provisional values: radius 3 cells,
 * reveal 1 s (0.5 s growing, 0.5 s shrinking), cooldown 2 s.
 */
class LightTest {
 
    private static final float FRAME = 1f / 60;
    private static final float DELTA = 0.001f;
 
    private Light light;
 
    @BeforeEach
    void setUp() {
        light = new Light();
        light.setOrigin(10f, 10f);
    }
 
    private void runSeconds(float seconds) {
        int frames = Math.round(seconds * 60);
        for (int i = 0; i < frames; i++) {
            light.update(FRAME);
        }
    }
 
    @Test
    void newLightIsDark() {
        assertFalse(light.isRevealing(), "nothing is revealed before the first snap");
        assertFalse(light.isOnCooldown(), "no cooldown before the first snap");
        assertEquals(0f, light.getCurrentRadius(), DELTA, "radius 0 before the first snap");
        assertEquals(0f, light.getRevealProgress(), DELTA, "progress 0 before the first snap");
    }
 
    @Test
    void firstSnapIsAccepted() {
        assertTrue(light.snap(), "the first snap is accepted");
        assertTrue(light.isRevealing(), "the area is being revealed");
        assertTrue(light.isOnCooldown(), "the cooldown has started");
    }
 
    @Test
    void snapDuringCooldownIsRefused() {
        light.snap();
        runSeconds(1.5f);
        assertFalse(light.snap(), "a snap 1.5 s later is refused (cooldown 2 s)");
    }
 
    @Test
    void snapIsPossibleAgainAfterCooldown() {
        light.snap();
        runSeconds(2f);
        assertFalse(light.isOnCooldown(), "the cooldown ends after 2 s");
        assertTrue(light.snap(), "a new snap is accepted after the cooldown");
    }
 
    @Test
    void snapRemembersWhereItHappened() {
        light.setOrigin(4f, 5f);
        light.snap();
        light.setOrigin(8f, 9f);
        assertEquals(4f, light.getSnapX(), DELTA, "the snap stays where the hero was when snapping");
        assertEquals(5f, light.getSnapY(), DELTA, "the snap stays where the hero was when snapping");
    }
 
    @Test
    void radiusGrowsThenShrinks() {
        light.snap();
        assertEquals(0f, light.getCurrentRadius(), DELTA, "radius 0 at the moment of the snap");
        light.update(0.25f);
        assertEquals(1.5f, light.getCurrentRadius(), DELTA, "half the radius after 0.25 s");
        light.update(0.25f);
        assertEquals(3f, light.getCurrentRadius(), DELTA, "full radius at mid-reveal (0.5 s)");
        assertEquals(0.5f, light.getRevealProgress(), DELTA, "progress 0.5 at mid-reveal");
        light.update(0.25f);
        assertEquals(1.5f, light.getCurrentRadius(), DELTA, "back to half the radius after 0.75 s");
        light.update(0.25f);
        assertEquals(0f, light.getCurrentRadius(), DELTA, "radius 0 after 1 s");
        assertFalse(light.isRevealing(), "darkness is back after the reveal duration");
    }
 
    @Test
    void bonusIncreasesTheRadius() {
        assertEquals(3f, light.getRevealRadius(), DELTA, "base radius 3");
        light.addBonusPercent(20);
        assertEquals(3.6f, light.getRevealRadius(), DELTA, "+20 % -> 3.6");
        light.addBonusPercent(20);
        assertEquals(40, light.getBonusPercent(), "two upgrades -> +40 %");
        assertEquals(4.2f, light.getRevealRadius(), DELTA, "+40 % -> 4.2");
    }
 
    @Test
    void refusedSnapNotifiesNobody() {
        List<SnapEvent> received = new ArrayList<>();
        SnapListener monster = received::add;
        long groupId = 0;
        for (int i = 0; i < 3; i++) {
            // what Level / the hero will do: publish only if the snap really happened
            if (light.snap()) {
                monster.onSnap(new SnapEvent(light.getSnapX(), light.getSnapY(), SnapSource.HERO, ++groupId));
            }
            runSeconds(0.5f);
        }
        assertEquals(1, received.size(), "3 snaps in 1.5 s: only the first one is published");
        assertEquals(10f, received.get(0).getSourceX(), DELTA, "the event carries the snap origin");
        assertEquals(SnapSource.HERO, received.get(0).getSource(), "the event comes from the hero");
    }
 
    @Test
    void invalidValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Light(0f, 1f, 2f), "radius must be > 0");
        assertThrows(IllegalArgumentException.class, () -> new Light(3f, 0f, 2f), "reveal duration must be > 0");
        assertThrows(IllegalArgumentException.class, () -> new Light(3f, 1f, -1f), "cooldown must be > 0");
        assertThrows(IllegalArgumentException.class, () -> light.addBonusPercent(-10), "bonus must be >= 0");
    }
}
 
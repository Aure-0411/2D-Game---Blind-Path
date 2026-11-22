package com.blindpath.domain.light;
 
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
/**
 * Tests of {@link Light} with the provisional values: radius 3 cells,
 * reveal 1 s (0.5 s growing, 0.5 s shrinking), cooldown 2 s.
 */
class LightTest {
 
    private static final float FRAME = 1f / 60;
    private static final float DELTA = 0.001f;
    // Valeur attendue du réglage permanent actuel, indépendante du code testé.
    private static final float EXPECTED_PERMANENT_RADIUS = 0.8f;
 
    private Light light;
 
    @BeforeEach
    void setUp() {
        // Paramètres explicites pour tester les règles indépendamment du réglage visuel.
        light = new Light(3f, 1f, 2f);
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

    @Test
    void permanentVisibilityHasDiameterOnePointSix() {
        light.updateHeroPosition(0f, 0f);
        assertEquals(EXPECTED_PERMANENT_RADIUS, light.getPermanentVisibilityRadius(), DELTA);
        assertTrue(light.isVisible(0f, 0f));
        assertTrue(light.isVisible(0.3f, 0.3f));
        assertTrue(light.isVisible(0.5f, 0.5f));
        assertFalse(light.isVisible(0.6f, 0.6f));
        assertFalse(light.isVisible(0.81f, 0f));
        assertFalse(light.isVisible(0f, -0.81f));
    }

    @Test
    void circleBoundaryIsVisibleInAllDirections() {
        light.updateHeroPosition(0f, 0f);
        assertTrue(light.isVisible(EXPECTED_PERMANENT_RADIUS, 0f));
        assertTrue(light.isVisible(-EXPECTED_PERMANENT_RADIUS, 0f));
        assertTrue(light.isVisible(0f, EXPECTED_PERMANENT_RADIUS));
        assertTrue(light.isVisible(0f, -EXPECTED_PERMANENT_RADIUS));
    }

    @Test
    void circleFollowsFractionalMovementInsideSameCell() {
        light.updateHeroPosition(3.1f, 5.5f);
        assertTrue(light.isVisible(2.7f, 5.5f));
        assertFalse(light.isVisible(4.3f, 5.5f));

        light.updateHeroPosition(3.9f, 5.5f);

        assertFalse(light.isVisible(2.7f, 5.5f));
        assertTrue(light.isVisible(4.3f, 5.5f));
        assertEquals(3.9f, light.getHeroCenterX(), DELTA);
        assertEquals(5.5f, light.getHeroCenterY(), DELTA);
    }

    @Test
    void circleFollowsVerticalMovementAndNegativePositions() {
        light.updateHeroPosition(-0.25f, -1.25f);
        assertTrue(light.isVisible(-0.25f, -1.25f));

        light.updateHeroPosition(-0.25f, -2.25f);

        assertFalse(light.isVisible(-0.25f, -1.25f));
        assertTrue(light.isVisible(-0.25f, -2.25f));
        assertEquals(-2.25f, light.getHeroCenterY(), DELTA);
    }

    @Test
    void movingHeroDoesNotMoveActiveSnapOrigin() {
        light.setOrigin(3.5f, 5.5f);
        light.snap();
        light.updateHeroPosition(4.2f, 6.2f);

        assertTrue(light.isVisible(4.2f, 6.2f));
        assertFalse(light.isVisible(3.5f, 5.5f));
        assertEquals(3.5f, light.getSnapX(), DELTA);
        assertEquals(5.5f, light.getSnapY(), DELTA);
    }

    @Test
    void defaultLightUsesConfiguredSnapSettings() {
        Light defaults = new Light();
        assertEquals(4.5f, defaults.getRevealRadius(), DELTA);
        assertTrue(defaults.snap());
        defaults.update(0.5f);
        assertEquals(0.5f, defaults.getRevealProgress(), DELTA);
        defaults.update(0.5f);
        assertFalse(defaults.isRevealing());
        assertTrue(defaults.isOnCooldown());
        defaults.update(0.5f);
        assertFalse(defaults.isOnCooldown());
        assertTrue(defaults.snap());
    }

    @Test
    void cooldownBarStartsFullThenFillsOverOnePointFiveSeconds() {
        Light defaults = new Light();
        assertEquals(1f, defaults.getCooldownProgress(), DELTA);
        defaults.snap();
        assertEquals(0f, defaults.getCooldownProgress(), DELTA);
        defaults.update(0.375f);
        assertEquals(0.25f, defaults.getCooldownProgress(), DELTA);
        defaults.update(0.375f);
        assertEquals(0.5f, defaults.getCooldownProgress(), DELTA);
        assertFalse(defaults.snap());
        assertEquals(0.5f, defaults.getCooldownProgress(), DELTA);
        defaults.update(0.375f);
        assertEquals(0.75f, defaults.getCooldownProgress(), DELTA);
        defaults.update(0.375f);
        assertEquals(1f, defaults.getCooldownProgress(), DELTA);
        defaults.update(10f);
        assertEquals(1f, defaults.getCooldownProgress(), DELTA);
        assertTrue(defaults.snap());
        assertEquals(0f, defaults.getCooldownProgress(), DELTA);
    }

    @Test
    void revealProgressAdvancesWithoutChangingMaskScale() {
        light.snap();
        assertEquals(0f, light.getRevealProgress(), DELTA);

        light.update(0.25f);
        assertEquals(0.25f, light.getRevealProgress(), DELTA);
        assertEquals(3f, light.getRevealRadius(), DELTA);

        light.update(0.5f);
        assertEquals(0.75f, light.getRevealProgress(), DELTA);
        assertEquals(3f, light.getRevealRadius(), DELTA);

        light.update(0.25f);
        assertFalse(light.isRevealing());
        assertEquals(0f, light.getRevealProgress(), DELTA);
    }

    @Test
    void refusedSnapDoesNotRestartTimersOrMoveOrigin() {
        light.setOrigin(2f, 3f);
        light.snap();
        light.update(0.25f);
        light.setOrigin(8f, 9f);

        assertFalse(light.snap());
        assertEquals(0.25f, light.getRevealProgress(), DELTA);
        assertEquals(2f, light.getSnapX(), DELTA);
        assertEquals(3f, light.getSnapY(), DELTA);

        light.update(0.75f);
        assertFalse(light.isRevealing());
        assertTrue(light.isOnCooldown());
        light.update(1f);
        assertFalse(light.isOnCooldown());
    }

    @Test
    void nextAcceptedSnapUsesNewOriginAndRestartsProgress() {
        light.snap();
        light.update(2f);
        light.setOrigin(-2.5f, 7.25f);

        assertTrue(light.snap());
        assertEquals(-2.5f, light.getSnapX(), DELTA);
        assertEquals(7.25f, light.getSnapY(), DELTA);
        assertEquals(0f, light.getRevealProgress(), DELTA);
        assertTrue(light.isRevealing());
        assertTrue(light.isOnCooldown());
    }

    @Test
    void permanentVisibilityRemainsAfterSnapEnds() {
        light.updateHeroPosition(2f, 3f);
        light.snap();
        light.update(2f);

        assertFalse(light.isRevealing());
        assertTrue(light.isVisible(2f, 3f));
        assertFalse(light.isVisible(3f, 3f));
    }

    @Test
    void revealTimingDoesNotDependOnNumberOfFrames() {
        Light singleStep = new Light(3f, 1f, 2f);
        Light multipleSteps = new Light(3f, 1f, 2f);
        singleStep.snap();
        multipleSteps.snap();

        singleStep.update(0.75f);
        for (int i = 0; i < 3; i++) {
            multipleSteps.update(0.25f);
        }

        assertEquals(singleStep.getRevealProgress(), multipleSteps.getRevealProgress(), DELTA);
        assertEquals(singleStep.isRevealing(), multipleSteps.isRevealing());
        assertEquals(singleStep.isOnCooldown(), multipleSteps.isOnCooldown());
    }
}

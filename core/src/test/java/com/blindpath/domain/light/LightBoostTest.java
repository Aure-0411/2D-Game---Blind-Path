package com.blindpath.domain.light;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


class LightBoostTest {
    private static final float DELTA = 1e-4f;

    @Test
    void getRevealRadius_whenNotBoosted_returnsBaseRadius() {
        Light light = new Light();

        assertEquals(Light.DEFAULT_RADIUS, light.getRevealRadius(), DELTA);
    }

    @Test
    void getRevealRadius_whenBoosted_returnsBoostedRadius() {
        Light light = new Light();
        light.setBoosted(true);

        assertEquals(Light.DEFAULT_RADIUS * Light.BOOST_MULTIPLIER, light.getRevealRadius(), DELTA);
    }
}
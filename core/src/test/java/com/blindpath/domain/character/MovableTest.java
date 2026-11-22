package com.blindpath.domain.character;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovableTest {

    private static final float DELTA = 0.5f;
    private Character hero;

    @BeforeEach
    void setUp() {
        hero = new Character(3f, 5f, 5f, 0.8f, 0.8f) {};
    }

    private void move(Direction d, float deltaTime) {
        hero.move(d.getNormalizedX(), d.getNormalizedY(), deltaTime);
    }

    @Test
    void characterIsMovable() {
        assertTrue(hero instanceof Movable);
    }

    @Test
    void moveEast_advancesBySpeedTimesDelta() {
        move(Direction.RIGHT, DELTA);

        assertEquals(5f + hero.getSpeed() * DELTA, hero.getX(), 1e-5f);
        assertEquals(5f, hero.getY(), 1e-5f);
    }

    @Test
    void moveNorth_increasesY() {
        move(Direction.UP, DELTA);

        assertEquals(5f, hero.getX(), 1e-5f);
        assertEquals(5f + hero.getSpeed() * DELTA, hero.getY(), 1e-5f);
    }

    @Test
    void everyDirection_coversTheSameDistance() {
        for (Direction d : Direction.values()) {
            if (d == Direction.NONE) continue;
            hero.setPosition(5f, 5f);
            move(d, DELTA);
            double distance = Math.hypot(hero.getX() - 5f, hero.getY() - 5f);
            assertEquals(hero.getSpeed() * DELTA, distance, 1e-4, d.name());
        }
    }

    @Test
    void diagonalSigns_areRespected() {
        move(Direction.UP_RIGHT, DELTA);
        assertTrue(hero.getX() > 5f && hero.getY() > 5f);

        hero.setPosition(5f, 5f);
        move(Direction.DOWN_LEFT, DELTA);
        assertTrue(hero.getX() < 5f && hero.getY() < 5f);
    }

    @Test
    void noneDirection_doesNotMove() {
        move(Direction.NONE, DELTA);

        assertEquals(5f, hero.getX());
        assertEquals(5f, hero.getY());
    }

    @Test
    void zeroDelta_doesNotMove() {
        move(Direction.RIGHT, 0f);

        assertEquals(5f, hero.getX());
    }
}
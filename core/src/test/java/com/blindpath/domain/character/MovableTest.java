package com.blindpath.domain.character;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovableTest {

    private static final float DELTA = 0.5f;
    private Gnome gnome;

    @BeforeEach
    void setUp() {
        gnome = new Gnome();
        gnome.setPosition(5f, 5f);
    }

    private void move(Direction d, float deltaTime) {
        gnome.move(d.getNormalizedX(), d.getNormalizedY(), deltaTime);
    }

    @Test
    void gnomeIsMovable() {
        assertTrue(gnome instanceof Movable);
    }

    @Test
    void moveEast_advancesBySpeedTimesDelta() {
        move(Direction.RIGHT, DELTA);

        assertEquals(5f + gnome.getSpeed() * DELTA, gnome.getX(), 1e-5f);
        assertEquals(5f, gnome.getY(), 1e-5f);
    }

    @Test
    void moveNorth_increasesY() {
        move(Direction.UP, DELTA);

        assertEquals(5f, gnome.getX(), 1e-5f);
        assertEquals(5f + gnome.getSpeed() * DELTA, gnome.getY(), 1e-5f);
    }

    @Test
    void everyDirection_coversTheSameDistance() {
        for (Direction d : Direction.values()) {
            if (d == Direction.NONE) continue;
            gnome.setPosition(5f, 5f);
            move(d, DELTA);
            double distance = Math.hypot(gnome.getX() - 5f, gnome.getY() - 5f);
            assertEquals(gnome.getSpeed() * DELTA, distance, 1e-4, d.name());
        }
    }

    @Test
    void diagonalSigns_areRespected() {
        move(Direction.UP_RIGHT, DELTA);
        assertTrue(gnome.getX() > 5f && gnome.getY() > 5f);

        gnome.setPosition(5f, 5f);
        move(Direction.DOWN_LEFT, DELTA);
        assertTrue(gnome.getX() < 5f && gnome.getY() < 5f);
    }

    @Test
    void noneDirection_doesNotMove() {
        move(Direction.NONE, DELTA);

        assertEquals(5f, gnome.getX());
        assertEquals(5f, gnome.getY());
    }

    @Test
    void zeroDelta_doesNotMove() {
        move(Direction.RIGHT, 0f);

        assertEquals(5f, gnome.getX());
    }
}
package com.blindpath.domain.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.*;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.*;
import com.blindpath.domain.physics.AabbCollisionWorld;

class LeverWallLevelTest {
    @Test
    void snapRotatesWallLetsTrappedHeroOutThenBlocksNewArea() {
        RotatingWall wall = new RotatingWall(4, 4, OpeningOrientation.HORIZONTAL, 4, 4, 90);
        Lever lever = new Lever(2.5f, 3.5f, 1, 1, wall);
        Labyrinth labyrinth = new Labyrinth(10, 10,
                new Entrance(0, 0, OpeningOrientation.VERTICAL),
                new Exit(9, 0, OpeningOrientation.VERTICAL), List.of(wall), List.of(), List.of(lever));
        Character hero = new Character(3f, 3f, 4f, 0.8f, 0.8f) {};
        Monster monster = new Monster(new LabyrinthGrid(labyrinth),
                (grid, current, goal) -> current, new GridPosition(8, 8)) {};
        Level<Character> level = new Level<>(hero, labyrinth, new AabbCollisionWorld(), monster,
                (grid, start, target) -> List.of(), List.of());
        level.emitSnap(new SnapEvent(3.5f, 4.5f, SnapSource.HERO, 0));
        assertTrue(wall.isRotated());
        assertFalse(wall.canCollide());
        assertEquals(6, hero.getLife());
        assertTrue(level.tryMove(hero, 1, 0, 0.5f));
        assertTrue(hero.getX() > 4f);
        assertTrue(wall.canCollide());
        LabyrinthGrid grid = new LabyrinthGrid(labyrinth);
        assertTrue(grid.isWalkable(new GridPosition(4, 4)), "l'ancienne position est libérée");
        assertFalse(grid.isWalkable(new GridPosition(3, 4)), "la nouvelle position devient bloquante");
        hero.setPosition(1, 4);
        level.tryMove(hero, 1, 0, 0.5f);
        assertTrue(hero.getX() + hero.getWidth() <= 3f + 0.001f);
        level.emitSnap(new SnapEvent(3, 4, SnapSource.HERO, 1));
        assertEquals(3f, wall.getBounds().getX(), "le second snap ne déplace plus le mur");
    }
}
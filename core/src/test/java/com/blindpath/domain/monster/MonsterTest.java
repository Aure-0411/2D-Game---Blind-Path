package com.blindpath.domain.monster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.infrastructure.pathfinding.GdxAiPathfinder;

class MonsterTest {

    private Labyrinth labyrinth;
    private LabyrinthGrid grid;
    private Pathfinder pathfinder;

    @BeforeEach
    void setUp() {
        labyrinth = new Labyrinth(10, 10,
                new Entrance(0, 4, OpeningOrientation.VERTICAL), new Exit(9, 4, OpeningOrientation.VERTICAL), List.of());
        grid = new LabyrinthGrid(labyrinth, Monster.SIZE);
        pathfinder = new GdxAiPathfinder();
    }

    private Jumper newJumper(int x, int y) {
        return new Jumper(grid, new ChaseStrategy(pathfinder), new GridPosition(x, y));
    }

    @Test
    void onTick_movesTowardsTargetAtInterval() {
        Jumper jumper = newJumper(2, 2);
        jumper.setTarget(new GridPosition(5, 2));

        jumper.onTick(1);
        assertEquals(new GridPosition(2, 2), jumper.getPosition());

        jumper.onTick(Monster.DEFAULT_TICKS_PER_MOVE);
        assertEquals(new GridPosition(3, 2), jumper.getPosition());
    }

    @Test
    void onSnap_movesImmediatelyTowardsSoundOrigin() {
        Jumper jumper = newJumper(2, 2);
        jumper.setTarget(new GridPosition(2, 8));

        jumper.onSnap(new SnapEvent(7f, 2f, SnapSource.HERO, 5L));

        assertEquals(new GridPosition(3, 2), jumper.getPosition());
    }

    @Test
    void bounds_areTwoByTwoOnGridPosition() {
        Jumper jumper = newJumper(4, 6);

        assertAll(
                () -> assertEquals(4f, jumper.getBounds().getX()),
                () -> assertEquals(6f, jumper.getBounds().getY()),
                () -> assertEquals(2f, jumper.getBounds().getWidth()),
                () -> assertEquals(2f, jumper.getBounds().getHeight()),
                () -> assertTrue(jumper.canCollide()));
    }

    @Test
    void collide_detectsOverlapWithHero() {
        Jumper jumper = newJumper(4, 4);
        Character hero = new Character(3f, 4.5f, 4.5f, 0.8f, 0.8f) {};

        assertTrue(hero.collide(jumper));

        hero.setPosition(1f, 1f);
        assertFalse(hero.collide(jumper));
    }

    @Test
    void setters_rejectNonPositiveValues() {
        Jumper jumper = newJumper(2, 2);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> jumper.setTicksPerMove(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> jumper.setCasePerMove(-1)),
                () -> assertThrows(IllegalArgumentException.class, () -> jumper.setSnapDistance(-1)));
    }
}
package com.blindpath.domain.object;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;

class PlacedBearTrapTest {
    private Monster createMonster() {
        Labyrinth map = new Labyrinth(10, 10, new Entrance(0, 2, OpeningOrientation.VERTICAL),
            new Exit(9, 2, OpeningOrientation.VERTICAL), List.of());
        Monster dummyMonster = new Monster(
            new LabyrinthGrid(map),
            (grid, current, goal) -> current,
            new GridPosition(0, 0)) {};
        return dummyMonster;
    }

    @Test
    void isDone_beforeTouch_returnsFalse() {
        PlacedBearTrap trap = new PlacedBearTrap(5f, 5f);

        assertFalse(trap.isDone());
    }

    @Test
    void isDone_afterTouch_returnsTrue() {
        PlacedBearTrap trap = new PlacedBearTrap(5f, 5f);
        Monster monster = createMonster();

        trap.onTouch(monster);

        assertTrue(trap.isDone());
    }

    @Test
    void onTouch_immobilizesMonster() {
        PlacedBearTrap trap = new PlacedBearTrap(5f, 5f);
        Monster monster = createMonster();

        trap.onTouch(monster);

        assertTrue(monster.isImmobilized());
    }
}
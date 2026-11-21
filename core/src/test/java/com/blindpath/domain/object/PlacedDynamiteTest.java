package com.blindpath.domain.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.AabbCollisionWorld;

class PlacedDynamiteTest {
    private ArrayList<SnapEvent> receivedSnaps = new ArrayList<>();
    private Level createSpyLevel() {
          Labyrinth map = new Labyrinth(10, 10, new Entrance(0, 2, OpeningOrientation.VERTICAL),
            new Exit(9, 2, OpeningOrientation.VERTICAL), List.of());
        Monster dummyMonster = new Monster(
        new LabyrinthGrid(map),
        (grid, current, goal) -> current,
        new GridPosition(0, 0)) {};
        Pathfinder dummyPathfinder = (grid, start, target) -> List.of();
        return new Level(new Gnome(), map, new AabbCollisionWorld(), dummyMonster, dummyPathfinder, List.of()) {
            @Override
            public void emitSnap(SnapEvent event) {
                receivedSnaps.add(event);
            }
        };
    }

    @Test
    void update_beforeEndOfCountdown_doesNotExplode() {
        Level level = createSpyLevel();
        PlacedDynamite dynamite = new PlacedDynamite(5f, 4f, level);

        dynamite.update(2.9f);

        assertEquals(0, receivedSnaps.size());
        assertFalse(dynamite.isDone());
    }

    @Test
    void update_atEndOfCountdown_explodesOnce() {
        Level level = createSpyLevel();
        PlacedDynamite dynamite = new PlacedDynamite(5f, 4f, level);

        dynamite.update(3f);

        assertEquals(1, receivedSnaps.size());
        assertTrue(dynamite.isDone());
    }

    @Test
    void update_afterEndOfCountdown_doesNotExplodeAnymore() {
        Level level = createSpyLevel();
        PlacedDynamite dynamite = new PlacedDynamite(5f, 4f, level);

        dynamite.update(3f);
        dynamite.update(1f);

        assertEquals(1, receivedSnaps.size());
        assertTrue(dynamite.isDone());
    }
}
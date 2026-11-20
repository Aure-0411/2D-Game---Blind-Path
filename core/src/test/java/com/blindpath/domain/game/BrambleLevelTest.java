package com.blindpath.domain.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.*;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.*;
import com.blindpath.domain.physics.AabbCollisionWorld;

class BrambleLevelTest {

    private Level level(Gnome hero, Bramble bramble) {
        Labyrinth labyrinth = new Labyrinth(10, 10,
                new Entrance(0, 0, OpeningOrientation.VERTICAL),
                new Exit(9, 0, OpeningOrientation.VERTICAL), List.of(), List.of(bramble));
        Monster monster = new Monster(new LabyrinthGrid(labyrinth),
                (grid, current, goal) -> current, new GridPosition(8, 8)) {};
        return new Level(hero, labyrinth, new AabbCollisionWorld(), monster,
                (grid, start, target) -> List.of(), List.of());
    }

    @Test
    void heroSnapActivatesRoncesAndExtensionRemovesOneWholeHeartOnlyOnce() {
        Gnome hero = new Gnome();
        hero.setPosition(4, 3);
        Bramble bramble = new Bramble(4, 4, OpeningOrientation.HORIZONTAL);
        Level level = level(hero, bramble);
        level.emitSnap(new SnapEvent(4.5f, 3.5f, SnapSource.HERO, 0));
        assertTrue(bramble.isActive());
        level.update(0.1f);
        assertEquals(4, hero.getLife());
        assertTrue(hero.isImmune());
        level.update(0.1f);
        assertEquals(4, hero.getLife());
        level.update(0.3f);
        assertFalse(bramble.isActive());
    }

    @Test
    void invincibilityPreventsDamageAndLowHealthDoesNotGoNegative() {
        Gnome hero = new Gnome();
        hero.setPosition(4, 4);
        hero.toggleInvincible();
        Bramble bramble = new Bramble(4, 4, OpeningOrientation.HORIZONTAL);
        Level level = level(hero, bramble);
        bramble.activate();
        level.update(0.1f);
        assertEquals(6, hero.getLife());
        hero.toggleInvincible();
        hero.setLife(1);
        level.update(0.1f);
        assertEquals(0, hero.getLife());
    }

    @Test
    void activeRoncesDoNotBlockMovementOrMonsterPathfinding() {
        Gnome hero = new Gnome();
        hero.setPosition(3, 4);
        Bramble bramble = new Bramble(4, 4, OpeningOrientation.HORIZONTAL);
        Level level = level(hero, bramble);
        bramble.activate();
        assertTrue(level.tryMove(hero, 1, 0, 0.5f));
        assertTrue(hero.getX() > 4f);
        assertTrue(new LabyrinthGrid(level.getLabyrinth()).isWalkable(new GridPosition(4, 4)));
        assertThrows(UnsupportedOperationException.class,
                () -> level.getLabyrinth().getBrambles().clear());
    }
}

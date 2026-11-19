package com.blindpath.domain.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.chest.Chest;
import com.blindpath.domain.chest.LootTable;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.monster.MonsterType;
import com.blindpath.domain.physics.AabbCollisionWorld;

public class GameSessionTest {

    private Gnome gnome;
    private GameSession session;

    @BeforeEach
    void setUp() {
        gnome = new Gnome();

        // Simule 2 paliers : Palier 1 = niveaux 1..3, Palier 2 = niveaux 4..5
        LevelCatalog testCatalog = new LevelCatalog() {
            @Override
            public LevelConfig getLevel(int levelNumber) {
                return new LevelConfig("test_map_" + levelNumber, MonsterType.JUMPER);
            }

            @Override
            public int totalLevels() {
                return 5;
            }

            @Override
            public int tierStartLevel(int levelNumber) {
                return (levelNumber <= 3) ? 1 : 4;
            }
        };

        LevelFactory factory = new LevelFactory(
                testCatalog,
                path -> simpleLabyrinth(),
                new LootTable(),
                new Random(3),
                AabbCollisionWorld::new);

        session = new GameSession(factory, gnome);
    }

    private static Labyrinth simpleLabyrinth() {
        return new Labyrinth(
                30, 30,
                new Entrance(0, 10, Border.LEFT),
                new Exit(29, 10, Border.RIGHT),
                List.of());
    }

    @Test
    void retryKeepsTheSameChestsAndTheirState() {
        session.startLevel(1);
        Chest chest = session.getLevel().getChests().get(0);
        chest.open(gnome);

        session.retry();

        Chest afterRetry = session.getLevel().getChests().get(0);
        assertSame(chest, afterRetry);
        assertTrue(afterRetry.isOpened());
    }

    @Test
    void retryGivesNoSecondLoot() {
        session.startLevel(1);
        session.getLevel().getChests().get(0).open(gnome);

        session.retry();
        session.getLevel().getChests().get(0).open(gnome);

        assertEquals(1, gnome.getInventory().getStacks().get(0).getQuantity());
    }

    @Test
    void retryKeepsTheInventory() {
        session.startLevel(1);
        session.getLevel().getChests().get(0).open(gnome);
        int stacksBefore = gnome.getInventory().getStacks().size();

        session.retry();

        assertEquals(stacksBefore, gnome.getInventory().getStacks().size());
    }

    @Test
    void retryRestoresLifeAndRecreatesTheMonster() {
        session.startLevel(1);
        Monster first = session.getLevel().getMonster();
        gnome.takeMonsterHit();

        session.retry();

        assertAll(
                () -> assertEquals(gnome.getMaxLife(), gnome.getLife()),
                () -> assertNotSame(first, session.getLevel().getMonster()));
    }

    @Test
    void retryReturnsToFirstLevelOfCurrentTier() {
        session.startLevel(5); // Niveau 5 appartient au palier 2 (niveaux 4..5)
        gnome.takeMonsterHit();

        session.retry();

        assertAll(
                () -> assertEquals(4, session.getCurrentLevelNumber()),
                () -> assertEquals(1, session.getRetryCount()),
                () -> assertEquals(gnome.getMaxLife(), gnome.getLife()));
    }

    @Test
    void finishingLevelFiveWinsTheGame() {
        session.startLevel(5);
        session.nextLevel();
        assertTrue(session.isWon());
    }
}
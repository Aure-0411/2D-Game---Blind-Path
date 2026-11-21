package com.blindpath.domain.game;

import com.blindpath.domain.labyrinth.OpeningOrientation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.chest.Chest;
import com.blindpath.domain.chest.LootTable;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.monster.MonsterType;
import com.blindpath.domain.physics.AabbCollisionWorld;

public class GameSessionTest {

    private static class TestHero extends Character {
        TestHero() {
            super(3f, 0f, 0f, 0.8f, 0.8f);
        }
    }

    private GameSession<Character> session;
    private Character hero;

    @BeforeEach
    void setUp() {
        hero = new TestHero();

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

        session = new GameSession<>(factory, hero);
    }

    private static Labyrinth simpleLabyrinth() {
        return new Labyrinth(
                30, 30,
                new Entrance(0, 10, OpeningOrientation.VERTICAL),
                new Exit(29, 10, OpeningOrientation.VERTICAL),
                List.of());
    }

    @Test
    void retryKeepsTheSameChestsAndTheirState() {
        session.startLevel(1);
        Chest chest = session.getLevel().getChests().get(0);
        chest.open(hero);

        session.retry();

        Chest afterRetry = session.getLevel().getChests().get(0);
        assertSame(chest, afterRetry);
        assertTrue(afterRetry.isOpened());
    }

    @Test
    void retryGivesNoSecondLoot() {
        session.startLevel(1);
        session.getLevel().getChests().get(0).open(hero);

        session.retry();
        session.getLevel().getChests().get(0).open(hero);

        assertEquals(1, hero.getInventory().getStacks().get(0).getQuantity());
    }

    @Test
    void retryKeepsTheInventory() {
        session.startLevel(1);
        session.getLevel().getChests().get(0).open(hero);
        int stacksBefore = hero.getInventory().getStacks().size();

        session.retry();

        assertEquals(stacksBefore, hero.getInventory().getStacks().size());
    }

    @Test
    void retryRestoresLifeAndRecreatesTheMonster() {
        session.startLevel(1);
        Monster first = session.getLevel().getMonster();
        hero.takeMonsterHit();

        session.retry();

        assertAll(
                () -> assertEquals(hero.getMaxLife(), hero.getLife()),
                () -> assertNotSame(first, session.getLevel().getMonster()));
    }

    @Test
    void retryReturnsToFirstLevelOfCurrentTier() {
        session.startLevel(5);
        hero.takeMonsterHit();

        session.retry();

        assertAll(
                () -> assertEquals(4, session.getCurrentLevelNumber()),
                () -> assertEquals(1, session.getRetryCount()),
                () -> assertEquals(hero.getMaxLife(), hero.getLife()));
    }

    @Test
    void finishingLevelFiveWinsTheGame() {
        session.startLevel(5);
        session.nextLevel();
        assertTrue(session.isWon());
    }
}
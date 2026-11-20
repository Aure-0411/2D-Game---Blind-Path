package com.blindpath.domain.game;

import com.blindpath.domain.labyrinth.OpeningOrientation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Random;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.chest.LootTable;
import com.blindpath.domain.inventory.ItemType;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.LabyrinthLoader;
import com.blindpath.domain.monster.Chameleon;
import com.blindpath.domain.monster.Inverter;
import com.blindpath.domain.monster.Jumper;
import com.blindpath.domain.monster.MonsterType;
import com.blindpath.domain.monster.Runaway;
import com.blindpath.domain.monster.Screamer;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.physics.AabbCollisionWorld;

class LevelFactoryTest {

    private LevelFactory factory;

    @BeforeEach
    void setUp() {
        List<LevelConfig> configs = List.of(
            new LevelConfig("lvl1", MonsterType.JUMPER),
            new LevelConfig("lvl2", MonsterType.RUNAWAY),
            new LevelConfig("lvl3", MonsterType.SCREAMER),
            new LevelConfig("lvl4", MonsterType.CHAMELEON),
            new LevelConfig("lvl5", MonsterType.INVERTER)
        );

        LevelCatalog testCatalog = new LevelCatalog() {
            @Override
            public LevelConfig getLevel(int levelNumber) {
                if (levelNumber < 1 || levelNumber > configs.size()) {
                    throw new IllegalArgumentException("Niveau inconnu : " + levelNumber);
                }
                return configs.get(levelNumber - 1);
            }
            @Override
            public int totalLevels() {
                return configs.size();
            }
            @Override public int tierStartLevel(int levelNumber) { return 1; }
        };

        factory = new LevelFactory(
                testCatalog,
                path -> simpleLabyrinth(),
                new LootTable(),
                new Random(42),
                AabbCollisionWorld::new
        );
    }

    private static Labyrinth simpleLabyrinth() {
        Entrance entrance = new Entrance(0, 2, OpeningOrientation.VERTICAL);
        Exit exit = new Exit(9, 2, OpeningOrientation.VERTICAL);
        return new Labyrinth(10, 10, entrance, exit, List.of());
    }

    @Test
    void monsterTypeMatchesLevel() {
        Class<?>[] expected = {
                Jumper.class, Runaway.class, Screamer.class,
                Chameleon.class, Inverter.class
        };
        for (int n = 1; n <= 5; n++) {
            Level level = factory.create(n, new Gnome(), factory.createChests(n));
            assertInstanceOf(expected[n - 1], level.getMonster(), "niveau " + n);
        }
    }

    @Test
    void monsterStartsOnItsConfiguredCell() {
        // Par défaut, LabyrinthLoader renvoie GridPosition(5, 5)
        Level level = factory.create(1, new Gnome(), factory.createChests(1));
        assertEquals(new GridPosition(5, 5), level.getMonster().getPosition());
    }

    @Test
    void eachLevelHasExactlyOneChest() {
        for (int n = 1; n <= 5; n++) {
            assertEquals(1, factory.createChests(n).size(), "niveau " + n);
        }
    }

    @Test
    void gnomeStartsAtTheEntrance() {
        Gnome gnome = new Gnome();
        Level level = factory.create(1, gnome, factory.createChests(1));
        Entrance entrance = level.getLabyrinth().getEntrance();

        assertAll(
                () -> assertEquals(entrance.getX(), gnome.getX()),
                () -> assertEquals(entrance.getY(), gnome.getY())
        );
    }

    @Test
    void unknownLevelIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> factory.configFor(6));
    }

    @Test
    void create_usesFixedLootEvenIfExcludedFromRandomDrop() {
        LabyrinthLoader fixedLootLoader = new LabyrinthLoader() {
            @Override
            public Labyrinth load(String path) {
                return new Labyrinth(10, 10, new Entrance(0, 4, OpeningOrientation.VERTICAL), new Exit(9, 4, OpeningOrientation.VERTICAL), List.of());
            }

            @Override
            public List<ChestSpawn> loadChestSpawns(String path) {
                return List.of(new ChestSpawn(2f, 2f, Border.BOTTOM, ItemType.HEART));
            }
        };

        LevelFactory customFactory = new LevelFactory(
                new LevelCatalog() {
                    @Override public LevelConfig getLevel(int n) { return new LevelConfig("map.tmx", MonsterType.JUMPER); }
                    @Override public int totalLevels() { return 1; }
                    @Override public int tierStartLevel(int n) { return 1; }
                },
                fixedLootLoader,
                new LootTable(),
                new Random(1),
                AabbCollisionWorld::new);

        Gnome gnome = new Gnome();
        Level level = customFactory.create(1, gnome, customFactory.createChests(1));
        level.getChests().get(0).open(gnome);

        assertEquals("Heart", gnome.getInventory().getStacks().get(0).getItem().getName());
    }
}
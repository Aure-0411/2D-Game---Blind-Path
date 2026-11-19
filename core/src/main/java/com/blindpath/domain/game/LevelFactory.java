package com.blindpath.domain.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.chest.Chest;
import com.blindpath.domain.chest.LootResult;
import com.blindpath.domain.chest.LootTable;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.LabyrinthLoader;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.CollisionWorld;
import com.blindpath.infrastructure.pathfinding.GdxAiPathfinder;
import com.blindpath.infrastructure.physics.Box2DCollisionWorld;

public class LevelFactory {

    public static final float CELL = 1f; // 1 unité = 1 case, aligné avec GameScreen

    private final LevelCatalog registry;
    private final LabyrinthLoader loader;
    private final LootTable lootTable;
    private final Random random;
    private final Supplier<CollisionWorld> collisionWorldFactory;

    public LevelFactory(LevelCatalog registry, LabyrinthLoader loader, LootTable lootTable, Random random) {
        this(registry, loader, lootTable, random, Box2DCollisionWorld::new);
    }

    public LevelFactory(
            LevelCatalog registry,
            LabyrinthLoader loader,
            LootTable lootTable,
            Random random,
            Supplier<CollisionWorld> collisionWorldFactory) {
        if (registry == null || loader == null || lootTable == null || random == null || collisionWorldFactory == null) {
            throw new IllegalArgumentException("Tous les paramètres de LevelFactory doivent être renseignés.");
        }
        this.registry = registry;
        this.loader = loader;
        this.lootTable = lootTable;
        this.random = random;
        this.collisionWorldFactory = collisionWorldFactory;
    }

    public LevelConfig configFor(int number) {
        return registry.getLevel(number);
    }

    public int totalLevels() {
        return registry.totalLevels();
    }

    /** Tire le loot et crée les coffres. À appeler une seule fois par niveau. */
    public List<Chest> createChests(int number) {
        LevelConfig cfg = configFor(number);
        List<LabyrinthLoader.ChestSpawn> spawns = loader.loadChestSpawns(cfg.mapPath());
        List<Chest> chests = new ArrayList<>();

        for (LabyrinthLoader.ChestSpawn spawn : spawns) {
            LootResult loot = lootTable.roll(random);
            chests.add(new Chest(spawn.x(), spawn.y(), spawn.orientation(), loot));
        }
        return chests;
    }

    public Level create(int number, Gnome gnome, List<Chest> chests) {
        LevelConfig cfg = configFor(number);

        Labyrinth labyrinth = loader.load(cfg.mapPath());
        LabyrinthGrid monsterGrid = new LabyrinthGrid(labyrinth, Monster.SIZE);
        Pathfinder pathfinder = new GdxAiPathfinder();

        GridPosition monsterSpawn = loader.loadMonsterSpawn(cfg.mapPath());
        Monster monster = cfg.monsterType().create(monsterGrid, monsterSpawn, pathfinder);

        gnome.setPosition(
                labyrinth.getEntrance().getX(),
                labyrinth.getEntrance().getY()
        );

        return new Level(gnome, labyrinth, collisionWorldFactory.get(), monster, pathfinder, chests);
    }

    public int tierStartFor(int number) {
        return registry.tierStartLevel(number);
    }

}
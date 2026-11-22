package com.blindpath.domain.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.chest.Chest;
import com.blindpath.domain.chest.LootResult;
import com.blindpath.domain.chest.LootTable;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.LabyrinthLoader;
import com.blindpath.domain.labyrinth.LabyrinthLoader.LabyrinthData;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.monster.MonsterType;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.CollisionWorld;
import com.blindpath.infrastructure.pathfinding.GdxAiPathfinder;
import com.blindpath.infrastructure.physics.Box2DCollisionWorld;

/**
 * Fabrique les niveaux (patron Factory) : charge la carte, crée le monstre, place le héros
 * à l'entrée et prépare le monde de collisions. Crée aussi les coffres et tire leur contenu.
 */
public class LevelFactory {

    /** Taille d'une case en unités du monde (1 unité = 1 case, comme dans GameScreen). */
    public static final float CELL = 1f; // 1 unité = 1 case, aligné avec GameScreen

    private final LevelCatalog registry;
    private final LabyrinthLoader loader;
    private final LootTable lootTable;
    private final Random random;
    private final Supplier<CollisionWorld> collisionWorldFactory;

    /**
     * Crée une fabrique qui utilise le moteur physique Box2D pour les collisions.
     *
     * @param registry la liste des niveaux
     * @param loader le chargeur de cartes
     * @param lootTable la table de tirage du contenu des coffres
     * @param random le générateur aléatoire du tirage
     */
    public LevelFactory(LevelCatalog registry, LabyrinthLoader loader, LootTable lootTable, Random random) {
        this(registry, loader, lootTable, random, Box2DCollisionWorld::new);
    }

    /**
     * Crée une fabrique avec un monde de collisions choisi (utile pour les tests).
     *
     * @param registry la liste des niveaux
     * @param loader le chargeur de cartes
     * @param lootTable la table de tirage du contenu des coffres
     * @param random le générateur aléatoire du tirage
     * @param collisionWorldFactory crée un nouveau monde de collisions pour chaque niveau
     * @throws IllegalArgumentException si l'un des paramètres est {@code null}
     */
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

    /**
     * @param number le numéro du niveau
     * @return sa configuration
     */
    public LevelConfig configFor(int number) {
        return registry.getLevel(number);
    }

    /** @return le nombre total de niveaux */
    public int totalLevels() {
        return registry.totalLevels();
    }

    /**
     * Crée les coffres d'un niveau et tire leur contenu, sauf pour les coffres
     * au contenu fixé dans la carte. À appeler une seule fois par niveau.
     *
     * @param number le numéro du niveau
     * @return les coffres du niveau
     */
    public List<Chest> createChests(int number) {
        LevelConfig cfg = configFor(number);
        List<LabyrinthLoader.ChestSpawn> spawns = loader.loadChestSpawns(cfg.mapPath());
        List<Chest> chests = new ArrayList<>();

        for (LabyrinthLoader.ChestSpawn spawn : spawns) {
            LootResult loot = (spawn.fixedLoot() != null)
                    ? new LootResult(spawn.fixedLoot().create(), 1)
                    : lootTable.roll(random);
            chests.add(new Chest(spawn.x(), spawn.y(), spawn.orientation(), loot));
        }
        return chests;
    }

    /**
     * Construit un niveau : carte, monstre (celui de la carte, sinon celui de la configuration),
     * héros placé à l'entrée et nouveau monde de collisions.
     *
     * @param <T> le type du héros
     * @param number le numéro du niveau
     * @param hero le héros
     * @param chests les coffres du niveau, créés par {@link #createChests(int)}
     * @return le niveau prêt à jouer
     */
    public <T extends Character> Level<T> create(int number, T hero, List<Chest> chests) {
        LevelConfig cfg = configFor(number);

        LabyrinthData data = loader.loadAll(cfg.mapPath());
        Labyrinth labyrinth = data.labyrinth();
        LabyrinthGrid monsterGrid = new LabyrinthGrid(labyrinth, Monster.SIZE);
        Pathfinder pathfinder = new GdxAiPathfinder();

        MonsterType effectiveType = (data.monsterType() != null) ? data.monsterType() : cfg.monsterType();
        Monster monster = effectiveType.create(monsterGrid, data.monsterSpawn(), pathfinder);

        hero.setPosition(
                labyrinth.getEntrance().getX(),
                labyrinth.getEntrance().getY()
        );

        return new Level<>(hero, labyrinth, collisionWorldFactory.get(), monster, pathfinder, chests);
    }

    /**
     * @param number le numéro d'un niveau
     * @return le premier niveau de son palier
     */
    public int tierStartFor(int number) {
        return registry.tierStartLevel(number);
    }

}
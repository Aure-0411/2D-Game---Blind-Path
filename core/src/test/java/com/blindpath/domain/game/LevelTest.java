package com.blindpath.domain.game;

import com.blindpath.domain.labyrinth.OpeningOrientation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.chest.Chest;
import com.blindpath.domain.chest.LootResult;
import com.blindpath.domain.inventory.Heart;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Wall;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.monster.ChaseStrategy;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.monster.MovementStrategy;
import com.blindpath.domain.object.PlacedBearTrap;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.AabbCollisionWorld;
import com.blindpath.infrastructure.pathfinding.GdxAiPathfinder;

class LevelTest {

    private static final float DELTA = 1e-3f;
    private static final float TICK = 1f / 60f;

    private static class TestHero extends Character {
        TestHero() {
            super(3f, 1f, 4f, 0.8f, 0.8f);
        }
    }

    private Character hero;
    private Labyrinth labyrinth;
    private Monster monster;
    private Pathfinder pathfinder;
    private Level<Character> level;

    @BeforeEach
    void setUp() {
        labyrinth = new Labyrinth(
                10, 10,
                new Entrance(0, 4, OpeningOrientation.VERTICAL),
                new Exit(9, 4, OpeningOrientation.VERTICAL),
                List.of(new Wall(5f, 0f, 1f, 10f))
        );
        hero = new TestHero();
        monster = new Monster(
                new LabyrinthGrid(labyrinth),
                (grid, current, goal) -> current,
                new GridPosition(8, 4)
        ) {};
        pathfinder = (grid, start, target) -> List.of();
        level = createLevel(hero, labyrinth);
    }

    private Level<Character> createLevel(Character c, Labyrinth l) {
        return new Level<>(c, l, new AabbCollisionWorld(), monster, pathfinder, List.of());
    }

    @Test
    void updateAutomaticallyConsumesHeartForInjuredHero() {
        hero.setLife(hero.getMaxLife() - 1);
        hero.getInventory().add(new Heart(), 1);

        level.update(TICK);

        assertEquals(hero.getMaxLife(), hero.getLife());
        assertTrue(hero.getInventory().getStacks().isEmpty());
    }

    @Test
    void updatePreservesHeartAtFullHealthAndAfterDeath() {
        hero.getInventory().add(new Heart(), 1);
        level.update(TICK);
        assertEquals(1, hero.getInventory().getStacks().get(0).getQuantity());

        hero.setLife(0);
        level.update(TICK);
        assertEquals(0, hero.getLife());
        assertEquals(1, hero.getInventory().getStacks().get(0).getQuantity());
    }

    @Test
    void updateHealsAfterNonLethalMonsterHit() {
        hero.setPosition(monster.getPosition().getColumn(), monster.getPosition().getRow());
        hero.getInventory().add(new Heart(), 1);

        level.update(TICK);

        assertEquals(hero.getMaxLife() - Character.MONSTER_DAMAGE + 2, hero.getLife());
        assertTrue(hero.getInventory().getStacks().isEmpty());
    }

    @Test
    void constructor_rejectsNulls() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level<>(null, labyrinth, new AabbCollisionWorld(), monster, pathfinder, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level<>(hero, null, new AabbCollisionWorld(), monster, pathfinder, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level<>(hero, labyrinth, null, monster, pathfinder, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level<>(hero, labyrinth, new AabbCollisionWorld(), null, pathfinder, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level<>(hero, labyrinth, new AabbCollisionWorld(), monster, null, List.of()))
        );
    }

    @Test
    void tryMove_inOpenSpace_moves() {
        float initialY = hero.getY();
        float deltaTime = 0.5f;

        assertTrue(level.tryMove(hero, 0f, 1f, deltaTime));

        float expectedY = initialY + hero.getSpeed() * deltaTime;
        assertEquals(expectedY, hero.getY(), DELTA);
    }

    @Test
    void tryMove_againstWall_neverEntersIt() {
        for (int i = 0; i < 200; i++) {
            level.tryMove(hero, 1f, 0f, TICK);
        }
        assertTrue(hero.getX() + hero.getWidth() <= 5f + DELTA, "x=" + hero.getX());
    }

    @Test
    void tryMove_withNullCharacter_throws() {
        assertThrows(IllegalArgumentException.class, () -> level.tryMove(null, 1f, 0f, 0.1f));
    }

    @Test
    void disabledObstacle_isNotRegistered() {
        Wall ghost = new Wall(3f, 0f, 1f, 10f);
        ghost.setCollide(false);
        Labyrinth l = new Labyrinth(
                10, 10,
                new Entrance(0, 4, OpeningOrientation.VERTICAL),
                new Exit(9, 4, OpeningOrientation.VERTICAL),
                List.of(ghost)
        );
        Character c = new TestHero();
        Level<Character> lvl = createLevel(c, l);

        for (int i = 0; i < 120; i++) {
            lvl.tryMove(c, 1f, 0f, TICK);
        }
        assertTrue(c.getX() > 4f, "un mur désactivé ne bloque pas");
    }

    @Test
    void boundary_keepsHeroInsideMap() {
        for (int i = 0; i < 300; i++) {
            level.tryMove(hero, -1f, -1f, TICK);
        }
        assertAll(
                () -> assertTrue(hero.getX() >= -DELTA, "x=" + hero.getX()),
                () -> assertTrue(hero.getY() >= -DELTA, "y=" + hero.getY())
        );
    }

    @Test
    void boundary_stopsHeroAtTheExitOpening() {
        Labyrinth open = new Labyrinth(
                10, 10,
                new Entrance(0, 4, OpeningOrientation.VERTICAL),
                new Exit(9, 4, OpeningOrientation.VERTICAL),
                List.of()
        );
        Character c = new TestHero();
        c.setPosition(7f, 4f);
        Level<Character> lvl = createLevel(c, open);

        for (int i = 0; i < 300; i++) {
            lvl.tryMove(c, 1f, 0f, TICK);
        }
        assertAll(
                () -> assertTrue(c.getX() + c.getWidth() <= 10f + DELTA, "ne doit pas sortir de la carte, x=" + c.getX()),
                () -> assertTrue(c.getX() > 8.9f, "doit atteindre la case de sortie, x=" + c.getX())
        );
    }

    @Test
    void labyrinth_exposesFourBoundaryWalls() {
        assertEquals(4, level.getLabyrinth().getBoundaryWalls().size());
    }

    @Test
    void isCompleted_atStart_returnsFalse() {
        assertFalse(level.isCompleted());
    }

    @Test
    void isCompleted_onExit_returnsTrue() {
        hero.setPosition(9f, 5f);
        assertTrue(level.isCompleted());
    }

    @Test
    void update_afterImmunityDuration_heroIsNoLongerImmune() {
        hero.takeMonsterHit();

        level.update(2f);

        assertFalse(hero.isImmune());
    }

    @Test
    void update_afterCollisionWithMonster_heroLosesTwoHalfHearts() {
        Monster closeMonster = new Monster(
                new LabyrinthGrid(labyrinth),
                (grid, current, goal) -> current,
                new GridPosition(1, 4)
        ) {};
        Level<Character> lvl = new Level<>(hero, labyrinth, new AabbCollisionWorld(), closeMonster, pathfinder, List.of());

        lvl.update(0.1f);

        assertEquals(hero.getMaxLife() - 2, hero.getLife());
    }

    @Test
    void emitSnap_opensChestWithinTwoCellsAndAddsLootToHero() {
        Chest chest = new Chest(2f, 4f, Border.RIGHT, new LootResult(new Heart(), 1));
        Level<Character> lvl = new Level<>(hero, labyrinth, new AabbCollisionWorld(), monster, pathfinder, List.of(chest));

        lvl.emitSnap(new SnapEvent(1.5f, 4.5f, SnapSource.HERO, 0));

        assertAll(
                () -> assertTrue(chest.isOpened()),
                () -> assertEquals(1, hero.getInventory().getStacks().size()));
    }

    @Test
    void emitSnap_ignoresChestFurtherThanTwoCells() {
        Chest chest = new Chest(6f, 4f, Border.RIGHT, new LootResult(new Heart(), 1));
        Level<Character> lvl = new Level<>(hero, labyrinth, new AabbCollisionWorld(), monster, pathfinder, List.of(chest));

        lvl.emitSnap(new SnapEvent(1.5f, 4.5f, SnapSource.HERO, 0));

        assertFalse(chest.isOpened());
    }

    @Test
    void update_monsterOnBearTrap_monsterIsImmobilized() {
        LabyrinthGrid grid = new LabyrinthGrid(level.getLabyrinth());
        MovementStrategy strategy = new ChaseStrategy(new GdxAiPathfinder());
        Monster trappedMonster = new Monster(grid, strategy, new GridPosition(5, 4)) {};
        level.setMonster(trappedMonster);
        PlacedBearTrap placedBearTrap = new PlacedBearTrap(5f, 4f);
        level.addPlacedObject(placedBearTrap);

        level.update(0.1f);

        assertTrue(trappedMonster.isImmobilized());
    }
}
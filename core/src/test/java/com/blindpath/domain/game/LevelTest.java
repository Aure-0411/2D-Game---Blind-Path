package com.blindpath.domain.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.blindpath.domain.character.Gnome;
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

    private Gnome gnome;
    private Labyrinth labyrinth;
    private Monster monster;
    private Pathfinder pathfinder;
    private Level level;

    @BeforeEach
    void setUp() {
        labyrinth = new Labyrinth(
                10, 10,
                new Entrance(0, 4, Border.LEFT),
                new Exit(9, 4, Border.RIGHT),
                List.of(new Wall(5f, 0f, 1f, 10f))
        );
        gnome = new Gnome();
        gnome.setPosition(1f, 4f);
        monster = new Monster(
                new LabyrinthGrid(labyrinth),
                (grid, current, goal) -> current,
                new GridPosition(8, 4)
        ) {};
        pathfinder = (grid, start, target) -> List.of();
        level = createLevel(gnome, labyrinth);
    }

    private Level createLevel(Gnome g, Labyrinth l) {
        return new Level(g, l, new AabbCollisionWorld(), monster, pathfinder, List.of());
    }

    @Test
    void updateAutomaticallyConsumesHeartForInjuredGnome() {
        gnome.setLife(gnome.getMaxLife() - 1);
        gnome.getInventory().add(new Heart(), 1);

        level.update(TICK);

        assertEquals(gnome.getMaxLife(), gnome.getLife());
        assertTrue(gnome.getInventory().getStacks().isEmpty());
    }

    @Test
    void updatePreservesHeartAtFullHealthAndAfterDeath() {
        gnome.getInventory().add(new Heart(), 1);
        level.update(TICK);
        assertEquals(1, gnome.getInventory().getStacks().get(0).getQuantity());

        gnome.setLife(0);
        level.update(TICK);
        assertEquals(0, gnome.getLife());
        assertEquals(1, gnome.getInventory().getStacks().get(0).getQuantity());
    }

    @Test
    void updateHealsAfterNonLethalMonsterHit() {
        monster.setPosition(gnome.getX(), gnome.getY());
        gnome.getInventory().add(new Heart(), 1);

        level.update(TICK);

        assertEquals(gnome.getMaxLife() - Gnome.MONSTER_DAMAGE + 1, gnome.getLife());
        assertTrue(gnome.getInventory().getStacks().isEmpty());
    }

    @Test
    void constructor_rejectsNulls() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level(null, labyrinth, new AabbCollisionWorld(), monster, pathfinder, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level(gnome, null, new AabbCollisionWorld(), monster, pathfinder, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level(gnome, labyrinth, null, monster, pathfinder, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level(gnome, labyrinth, new AabbCollisionWorld(), null, pathfinder, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Level(gnome, labyrinth, new AabbCollisionWorld(), monster, null, List.of()))
        );
    }

    @Test
    void tryMove_inOpenSpace_moves() {
        float initialY = gnome.getY();
        float deltaTime = 0.5f;

        assertTrue(level.tryMove(gnome, 0f, 1f, deltaTime));

        float expectedY = initialY + gnome.getSpeed() * deltaTime;
        assertEquals(expectedY, gnome.getY(), DELTA);
    }

    @Test
    void tryMove_againstWall_neverEntersIt() {
        for (int i = 0; i < 200; i++) {
            level.tryMove(gnome, 1f, 0f, TICK);
        }
        assertTrue(gnome.getX() + gnome.getWidth() <= 5f + DELTA, "x=" + gnome.getX());
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
                new Entrance(0, 4, Border.LEFT),
                new Exit(9, 4, Border.RIGHT),
                List.of(ghost)
        );
        Gnome g = new Gnome();
        g.setPosition(1f, 4f);
        Level lvl = createLevel(g, l);

        for (int i = 0; i < 120; i++) {
            lvl.tryMove(g, 1f, 0f, TICK);
        }
        assertTrue(g.getX() > 4f, "un mur désactivé ne bloque pas");
    }

    @Test
    void boundary_keepsGnomeInsideMap() {
        for (int i = 0; i < 300; i++) {
            level.tryMove(gnome, -1f, -1f, TICK);
        }
        assertAll(
                () -> assertTrue(gnome.getX() >= -DELTA, "x=" + gnome.getX()),
                () -> assertTrue(gnome.getY() >= -DELTA, "y=" + gnome.getY())
        );
    }

    @Test
    void boundary_stopsGnomeAtTheExitOpening() {
        Labyrinth open = new Labyrinth(
                10, 10,
                new Entrance(0, 4, Border.LEFT),
                new Exit(9, 4, Border.RIGHT),
                List.of()
        );
        Gnome g = new Gnome();
        g.setPosition(7f, 4f);
        Level lvl = createLevel(g, open);

        for (int i = 0; i < 300; i++) {
            lvl.tryMove(g, 1f, 0f, TICK);
        }
        assertAll(
                () -> assertTrue(g.getX() + g.getWidth() <= 10f + DELTA, "ne doit pas sortir de la carte, x=" + g.getX()),
                () -> assertTrue(g.getX() > 8.9f, "doit atteindre la case de sortie, x=" + g.getX())
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
        gnome.setPosition(9f, 5f);
        assertTrue(level.isCompleted());
    }

    @Test
    void update_afterImmunityDuration_gnomeIsNoLongerImmune() {
        gnome.takeMonsterHit();

        level.update(2f);

        assertFalse(gnome.isImmune());
    }

    @Test
    void update_afterCollisionWithMonster_gnomeLosesTwoHalfHearts() {
        Monster closeMonster = new Monster(
                new LabyrinthGrid(labyrinth),
                (grid, current, goal) -> current,
                new GridPosition(1, 4)
        ) {};
        Level lvl = new Level(gnome, labyrinth, new AabbCollisionWorld(), closeMonster, pathfinder, List.of());

        lvl.update(0.1f);

        assertEquals(gnome.getMaxLife() - 2, gnome.getLife());
    }

    @Test
    void emitSnap_opensChestWithinTwoCellsAndAddsLootToGnome() {
        Chest chest = new Chest(2f, 4f, Border.RIGHT, new LootResult(new Heart(), 1));
        Level lvl = new Level(gnome, labyrinth, new AabbCollisionWorld(), monster, pathfinder, List.of(chest));

        lvl.emitSnap(new SnapEvent(1.5f, 4.5f, SnapSource.HERO, 0));

        assertAll(
                () -> assertTrue(chest.isOpened()),
                () -> assertEquals(1, gnome.getInventory().getStacks().size()));
    }

    @Test
    void emitSnap_ignoresChestFurtherThanTwoCells() {
        Chest chest = new Chest(6f, 4f, Border.RIGHT, new LootResult(new Heart(), 1));
        Level lvl = new Level(gnome, labyrinth, new AabbCollisionWorld(), monster, pathfinder, List.of(chest));

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

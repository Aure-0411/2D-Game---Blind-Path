package com.blindpath.domain.chest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.inventory.Heart;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;

class ChestTest {

    private static Character newHero() {
        return new Character(3f, 0f, 0f, 0.8f, 0.8f) {};
    }

    @Test
    void verticalChestHasOneByTwoHitbox() {
        Chest chest = new Chest(5f, 3f, Border.LEFT, new LootResult(new Heart(), 1));

        assertAll(
                () -> assertEquals(5f, chest.getBounds().getX()),
                () -> assertEquals(3f, chest.getBounds().getY()),
                () -> assertEquals(1f, chest.getBounds().getWidth()),
                () -> assertEquals(2f, chest.getBounds().getHeight()),
                () -> assertTrue(chest.canCollide()));
    }

    @Test
    void horizontalChestHasTwoByOneHitbox() {
        Chest chest = new Chest(5f, 3f, Border.TOP, new LootResult(new Heart(), 1));

        assertAll(
                () -> assertEquals(2f, chest.getBounds().getWidth()),
                () -> assertEquals(1f, chest.getBounds().getHeight()));
    }

    @Test
    void open_givesLootOnce() {
        Chest chest = new Chest(0f, 0f, Border.RIGHT, new LootResult(new Heart(), 2));
        Character hero = newHero();

        assertTrue(chest.open(hero));
        assertTrue(chest.isOpened());
        assertEquals(2, hero.getInventory().getStacks().get(0).getQuantity());

        assertFalse(chest.open(hero));
        assertEquals(2, hero.getInventory().getStacks().get(0).getQuantity());
    }

    @Test
    void isInRange_checksEllipseOneCellAroundTheChest() {
        Chest chest = new Chest(5f, 5f, Border.LEFT, new LootResult(new Heart(), 1));
        // Centre du coffre 1x2 en (5.5, 6), demi-axes 1.5 et 2.0 :
        // rayon de 1 case autour de chaque face (x in, y in [4, 8]).
        assertTrue(chest.isInRange(5.5f, 6f));
        assertTrue(chest.isInRange(4.1f, 6f));
        assertTrue(chest.isInRange(5.5f, 4.1f));
        assertFalse(chest.isInRange(3.9f, 6f));
        assertFalse(chest.isInRange(5.5f, 3.9f));
        assertFalse(chest.isInRange(7f, 8f));
    }

    @Test
    void onSnap_opensWhenSnapIsWithinRange() {
        Chest chest = new Chest(5f, 5f, Border.LEFT, new LootResult(new Heart(), 1));
        Character hero = newHero();
        chest.setOpener(hero);

        chest.onSnap(new SnapEvent(4.2f, 6f, SnapSource.HERO, 0));

        assertAll(
                () -> assertTrue(chest.isOpened()),
                () -> assertEquals(1, hero.getInventory().getStacks().size()));
    }

    @Test
    void onSnap_ignoresFarSnap() {
        Chest chest = new Chest(5f, 5f, Border.LEFT, new LootResult(new Heart(), 1));
        Character hero = newHero();
        chest.setOpener(hero);

        chest.onSnap(new SnapEvent(2f, 6f, SnapSource.HERO, 0));

        assertFalse(chest.isOpened());
        assertTrue(hero.getInventory().getStacks().isEmpty());
    }

    @Test
    void constructor_rejectsNullOrientationOrLoot() {
        LootResult loot = new LootResult(new Heart(), 1);
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Chest(0f, 0f, null, loot)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Chest(0f, 0f, Border.LEFT, null)));
    }
}
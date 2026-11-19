package com.blindpath.domain.chest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.inventory.Heart;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;

public class ChestTest {

    @Test
    void openingGivesTheItemOnlyOnce() {
        Gnome gnome = new Gnome();
        Chest chest = new Chest(0, 0, Border.BOTTOM, new LootResult(new Heart(), 1));

        assertTrue(chest.open(gnome));
        assertFalse(chest.open(gnome));

        assertAll(
                () -> assertTrue(chest.isOpened()),
                () -> assertEquals(1, gnome.getInventory().getStacks().size()),
                () -> assertEquals(1, gnome.getInventory().getStacks().get(0).getQuantity()));
    }

    @Test
    void onSnap_opensChestWhenWithinTwoCellsOfCenter() {
        Gnome gnome = new Gnome();
        // Coffre BOTTOM en (0, 0) -> taille 2x1, centre en (1.0f, 0.5f)
        Chest chest = new Chest(0, 0, Border.BOTTOM, new LootResult(new Heart(), 1));
        chest.setOpener(gnome);

        // Snap à exactement 2.0f du centre (3.0f, 0.5f)
        chest.onSnap(new SnapEvent(3.0f, 0.5f, SnapSource.HERO, 0));

        assertAll(
                () -> assertTrue(chest.isOpened()),
                () -> assertEquals(2f, chest.getActivationRadius()),
                () -> assertEquals(1, gnome.getInventory().getStacks().size()));
    }

    @Test
    void onSnap_doesNotOpenChestWhenBeyondTwoCells() {
        Gnome gnome = new Gnome();
        // Centre en (1.0f, 0.5f)
        Chest chest = new Chest(0, 0, Border.BOTTOM, new LootResult(new Heart(), 1));
        chest.setOpener(gnome);

        // Snap à 2.1f du centre (3.1f, 0.5f) -> hors de portée
        chest.onSnap(new SnapEvent(3.1f, 0.5f, SnapSource.HERO, 0));

        assertAll(
                () -> assertFalse(chest.isOpened()),
                () -> assertTrue(gnome.getInventory().getStacks().isEmpty()));
    }

    @Test
    void onSnap_withoutOpenerOrNullEvent_doesNothing() {
        Chest chest = new Chest(0, 0, Border.BOTTOM, new LootResult(new Heart(), 1));

        chest.onSnap(null);
        chest.onSnap(new SnapEvent(1.0f, 0.5f, SnapSource.HERO, 0));

        assertFalse(chest.isOpened());
    }

    @Test
    void horizontalOrientation_givesTwoByOneSize() {
        Chest topChest = new Chest(0, 0, Border.TOP, new LootResult(new Heart(), 1));
        Chest bottomChest = new Chest(0, 0, Border.BOTTOM, new LootResult(new Heart(), 1));

        assertAll(
                () -> assertEquals(Border.TOP, topChest.getOrientation()),
                () -> assertEquals(2f, topChest.getWidth()),
                () -> assertEquals(1f, topChest.getHeight()),
                () -> assertEquals(Border.BOTTOM, bottomChest.getOrientation()),
                () -> assertEquals(2f, bottomChest.getWidth()),
                () -> assertEquals(1f, bottomChest.getHeight()));
    }

    @Test
    void verticalOrientation_givesOneByTwoSize() {
        Chest leftChest = new Chest(0, 0, Border.LEFT, new LootResult(new Heart(), 1));
        Chest rightChest = new Chest(0, 0, Border.RIGHT, new LootResult(new Heart(), 1));

        assertAll(
                () -> assertEquals(Border.LEFT, leftChest.getOrientation()),
                () -> assertEquals(1f, leftChest.getWidth()),
                () -> assertEquals(2f, leftChest.getHeight()),
                () -> assertEquals(Border.RIGHT, rightChest.getOrientation()),
                () -> assertEquals(1f, rightChest.getWidth()),
                () -> assertEquals(2f, rightChest.getHeight()));
    }

    @Test
    void nullArguments_areRejected() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Chest(0, 0, null, new LootResult(new Heart(), 1))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Chest(0, 0, Border.BOTTOM, null)));
    }
}
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
    void onSnap_usesEllipticalRangeMatchingChestOrientation() {
        Gnome gnome = new Gnome();
        // Coffre BOTTOM en (0, 0) -> taille 2x1, centre en (1.0f, 0.5f)
        // Avec DEFAULT_ACTIVATION_MARGIN = 1.0f : rx = 2.0f, ry = 1.5f
        Chest chest = new Chest(0, 0, Border.BOTTOM, new LootResult(new Heart(), 1));
        chest.setOpener(gnome);

        // À 1.9f horizontalement du centre (2.9f, 0.5f) : dans l'ellipse (1.9 <= 2.0), alors que verticalement (ry = 1.5) ce serait rejeté
        chest.onSnap(new SnapEvent(2.9f, 0.5f, SnapSource.HERO, 0));

        assertAll(
                () -> assertTrue(chest.isOpened()),
                () -> assertEquals(2.0f, chest.getActivationRadiusX()),
                () -> assertEquals(1.5f, chest.getActivationRadiusY()));
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

    @Test
    void onSnap_rejectsPointOutsideEllipseShortAxis() {
        Gnome gnome = new Gnome();
        // Coffre BOTTOM en (0, 0) -> centre en (1.0f, 0.5f), ry = 1.5f
        Chest chest = new Chest(0, 0, Border.BOTTOM, new LootResult(new Heart(), 1));
        chest.setOpener(gnome);

        // À 1.6f verticalement du centre (1.0f, 2.1f) : hors du petit axe (1.6 > 1.5)
        chest.onSnap(new SnapEvent(1.0f, 2.1f, SnapSource.HERO, 0));

        assertFalse(chest.isOpened());
    }
}
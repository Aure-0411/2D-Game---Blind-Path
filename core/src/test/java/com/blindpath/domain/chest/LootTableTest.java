package com.blindpath.domain.chest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.blindpath.domain.inventory.ItemType;

class LootTableTest {

    private final LootTable lootTable = new LootTable();

    @Test
    void roll_returnsValidItemWithQuantityOne() {
        LootResult result = lootTable.roll(new Random(1));

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.item()),
                () -> assertEquals(1, result.quantity())
        );
    }

    @Test
    void roll_withSameSeed_producesSameItemType() {
        LootResult first = lootTable.roll(new Random(42));
        LootResult second = lootTable.roll(new Random(42));

        assertEquals(first.item().getClass(), second.item().getClass());
        assertNotSame(first.item(), second.item(), "chaque tirage doit créer une nouvelle instance");
    }

@Test
    void roll_onlyProducesRandomDroppableItems() {
        LootTable table = new LootTable();
        Random rng = new Random(42);

        Set<String> expectedNames = Stream.of(ItemType.values())
                .filter(ItemType::isRandomDroppable)
                .map(type -> type.create().getName())
                .collect(Collectors.toSet());

        for (int i = 0; i < 100; i++) {
            LootResult result = table.roll(rng);
            assertNotNull(result);
            assertEquals(1, result.quantity());
            assertTrue(expectedNames.contains(result.item().getName()),
                    "Objet non autorisé en tirage aléatoire : " + result.item().getName());
        }
    }

    @Test
    void roll_withNullRandom_throws() {
        assertThrows(IllegalArgumentException.class, () -> lootTable.roll(null));
    }


    @Test
    void constructor_rejectsEmptyPool() {
        assertThrows(IllegalArgumentException.class, () -> new LootTable(List.of()));
    }
}
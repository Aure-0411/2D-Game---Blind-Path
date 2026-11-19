package com.blindpath.domain.chest;

import com.blindpath.domain.inventory.BearTrap;
import com.blindpath.domain.inventory.Booster;
import com.blindpath.domain.inventory.Compass;
import com.blindpath.domain.inventory.Dynamite;
import com.blindpath.domain.inventory.Heart;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void roll_canProduceAllConfiguredItemTypes() {
        Random random = new Random(0);
        Set<Class<?>> rolledTypes = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            rolledTypes.add(lootTable.roll(random).item().getClass());
        }

        Set<Class<?>> expectedTypes = Set.of(
                Heart.class,
                BearTrap.class,
                Dynamite.class,
                Compass.class,
                Booster.class
        );
        assertEquals(expectedTypes, rolledTypes);
    }

    @Test
    void roll_withNullRandom_throws() {
        assertThrows(NullPointerException.class, () -> lootTable.roll(null));
    }
}
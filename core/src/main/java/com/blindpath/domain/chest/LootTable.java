package com.blindpath.domain.chest;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.blindpath.domain.inventory.ItemType;

public class LootTable {

    private final List<ItemType> randomPool;

    public LootTable() {
        this(Arrays.stream(ItemType.values())
                .filter(ItemType::isRandomDroppable)
                .toList());
    }

    public LootTable(List<ItemType> pool) {
        if (pool == null || pool.isEmpty()) {
            throw new IllegalArgumentException("La table de loot ne peut pas être vide.");
        }
        this.randomPool = List.copyOf(pool);
    }

    public LootResult roll(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Le générateur aléatoire doit être renseigné.");
        }
        ItemType chosen = randomPool.get(random.nextInt(randomPool.size()));
        return new LootResult(chosen.create(), 1);
    }
}
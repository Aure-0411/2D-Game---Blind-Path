package com.blindpath.domain.chest;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.blindpath.domain.inventory.BearTrap;
import com.blindpath.domain.inventory.Compass;
import com.blindpath.domain.inventory.Dynamite;
import com.blindpath.domain.inventory.Heart;
import com.blindpath.domain.inventory.Item;
import com.blindpath.domain.inventory.Booster;

public class LootTable {

    private static final List<Supplier<Item>> POOL = List.of(
            Heart::new,
            BearTrap::new,
            Dynamite::new,
            Compass::new,
            Booster::new);

    public LootResult roll(Random random) {
        Item item = POOL.get(random.nextInt(POOL.size())).get();
        return new LootResult(item, 1);
    }
}
package com.blindpath.domain.inventory;

import java.util.function.Supplier;

public enum ItemType {
    HEART(() -> new Heart(), true),
    BEAR_TRAP(() -> new BearTrap(), true),
    BOOSTER(() -> new Booster(), true),
    DYNAMITE(() -> new Dynamite(), true),
    COMPASS(() -> new Compass(), true);

    private final Supplier<Item> constructor;
    private final boolean randomDroppable;

    ItemType(Supplier<Item> constructor, boolean randomDroppable) {
        this.constructor = constructor;
        this.randomDroppable = randomDroppable;
    }

    public Item create() {
        return constructor.get();
    }

    public boolean isRandomDroppable() {
        return randomDroppable;
    }
}
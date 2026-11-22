package com.blindpath.domain.inventory;

import java.util.function.Supplier;

/**
 * Les types d'objets du jeu. Chaque type sait créer son objet,
 * et indique s'il peut sortir au hasard d'un coffre.
 */
public enum ItemType {
    /** Cœur : rend 1 cœur. */
    HEART(() -> new Heart(), true),
    /** Piège à ours : immobilise le monstre. */
    BEAR_TRAP(() -> new BearTrap(), true),
    /** Booster : renforce la lanterne. */
    BOOSTER(() -> new Booster(), true),
    /** Dynamite : explose comme un claquement et attire le monstre. */
    DYNAMITE(() -> new Dynamite(), true),
    /** Boussole : indique la sortie. */
    COMPASS(() -> new Compass(), true);

    private final Supplier<Item> constructor;
    private final boolean randomDroppable;

    ItemType(Supplier<Item> constructor, boolean randomDroppable) {
        this.constructor = constructor;
        this.randomDroppable = randomDroppable;
    }

    /** @return un nouvel objet de ce type */
    public Item create() {
        return constructor.get();
    }

    /** @return {@code true} si ce type peut sortir au hasard d'un coffre */
    public boolean isRandomDroppable() {
        return randomDroppable;
    }
}
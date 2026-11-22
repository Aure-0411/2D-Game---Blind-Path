package com.blindpath.domain.chest;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.blindpath.domain.inventory.ItemType;

/** Table de tirage du contenu des coffres : chaque objet de la liste a la même chance de sortir. */
public class LootTable {

    private final List<ItemType> randomPool;

    /** Crée une table avec tous les objets marqués comme tirables ({@link ItemType#isRandomDroppable()}). */
    public LootTable() {
        this(Arrays.stream(ItemType.values())
                .filter(ItemType::isRandomDroppable)
                .toList());
    }

    /**
     * Crée une table avec une liste d'objets choisie.
     *
     * @param pool les types d'objets tirables
     * @throws IllegalArgumentException si la liste est {@code null} ou vide
     */
    public LootTable(List<ItemType> pool) {
        if (pool == null || pool.isEmpty()) {
            throw new IllegalArgumentException("La table de loot ne peut pas être vide.");
        }
        this.randomPool = List.copyOf(pool);
    }

    /**
     * Tire un objet au hasard, en un exemplaire.
     *
     * @param random le générateur aléatoire
     * @return le contenu tiré
     * @throws IllegalArgumentException si le générateur est {@code null}
     */
    public LootResult roll(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Le générateur aléatoire doit être renseigné.");
        }
        ItemType chosen = randomPool.get(random.nextInt(randomPool.size()));
        return new LootResult(chosen.create(), 1);
    }
}
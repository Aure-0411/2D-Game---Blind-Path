package com.blindpath.domain.chest;

import com.blindpath.domain.inventory.*;

/**
 * Contenu d'un coffre : un objet et sa quantité.
 *
 * @param item l'objet obtenu
 * @param quantity le nombre d'exemplaires, strictement positif
 */
public record LootResult(Item item, int quantity) {
    /**
     * Vérifie le contenu.
     *
     * @throws IllegalArgumentException si l'objet est {@code null} ou si la quantité n'est pas positive
     */
    public LootResult {
        if (item == null) throw new IllegalArgumentException("L'objet ne peut pas être null");
        if (quantity <= 0) throw new IllegalArgumentException("La quantité doit être positive");
    }
}
package com.blindpath.domain.chest;

import com.blindpath.domain.inventory.*;

public record LootResult(Item item, int quantity) {
    public LootResult {
        if (item == null) throw new IllegalArgumentException("L'objet ne peut pas être null");
        if (quantity <= 0) throw new IllegalArgumentException("La quantité doit être positive");
    }
}
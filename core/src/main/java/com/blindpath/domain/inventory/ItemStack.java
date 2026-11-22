package com.blindpath.domain.inventory;

/** Pile d'objets d'un même type dans un emplacement d'inventaire (1 à 5 exemplaires). */
public class ItemStack {

    private final Item item;
    private int quantity;

    /**
     * @param item l'objet de la pile
     * @param quantity le nombre d'exemplaires, de 1 à 5
     * @throws IllegalArgumentException si l'objet est {@code null} ou la quantité hors limites
     */
    public ItemStack(Item item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("l'item chosi n'existe pas");
        }
        if (quantity > 5 || quantity < 1) {
            throw new IllegalArgumentException("Le nombre de d'item est trop grand pour etre stocké ou sa quantité n'est pas stockable");
        }

        this.item = item;
        this.quantity = quantity;
    }

    /** @return l'objet de la pile */
    public Item getItem() {
        return this.item;
    }

    /** @return le nombre d'exemplaires */
    public int getQuantity() {
        return this.quantity;
    }

    void add(int quantityToAdd) {
        if (quantityToAdd < 0) {
            throw new IllegalArgumentException("La quantité ajoutée ne peut pas être négative.");
        }

        int availableSpace = 5 - this.quantity;
        if (availableSpace <= quantityToAdd) {
            this.quantity = 5;
        }
        else {
            this.quantity += quantityToAdd;
        }

    }
    void rmv() {
        if (this.quantity == 0) {
            throw new IllegalArgumentException("Il n'y a pas d'objet à supprimer");
        }
        else {
            this.quantity -= 1;
        }

    }


}

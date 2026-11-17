package com.blindpath.domain.inventory;

public class ItemStack {

    private final Item item;
    private int quantity;

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

    public Item getItem() {
        return this.item;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void add(int quantityToAdd) {
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
       public void rmv() {
        if (this.quantity == 0) {
            throw new IllegalArgumentException("Il n'y a pas d'objet à supprimer");
        }
        else {
            this.quantity -= 1;
        }

    }


}

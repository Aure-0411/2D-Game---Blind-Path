package com.blindpath.domain.inventory;

import java.util.ArrayList;
import java.util.List;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;

public class Inventory {

    private int maxSlots = 5;
    private int maxStackSize = 5;
    private List<ItemStack> stacks;

    public Inventory() {
        this.stacks = new ArrayList<ItemStack>();
    }

    public void add(Item items, int quantity) {
        if (items == null) {
            throw new IllegalArgumentException("L'objet doit être renseigné.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité ajoutée ne peut pas être négative.");
        }
        if (quantity == 0) {
            return;
        }

        // Cherche d'abord une pile du même type, même si l'inventaire est plein.
        for (int i = 0; i < this.stacks.size(); i++) {
            ItemStack actuaList = this.stacks.get(i);
            if (actuaList.getItem().getClass() == items.getClass()) {
                actuaList.add(quantity);
                return;
            }
        }

        // Aucun type correspondant trouvé : crée une seule pile si une place reste.
        if (this.stacks.size() < maxSlots) {
            this.stacks.add(new ItemStack(items, Math.min(quantity, maxStackSize)));
        }
    }

    public boolean use(int slot, Gnome user, Level level) {
        if (user == null || level == null) {
            throw new IllegalArgumentException("le niveau ou le Gnome ne peuvent pas etres null");
        }

        if (slot < 0 || slot >= this.stacks.size()) {
            throw new IllegalArgumentException("Ce slot n'exite pas");
        }
        if (!this.stacks.get(slot).getItem().canUse(user, level)) {
            return false;
        } else {
            this.stacks.get(slot).getItem().use(user, level);
            this.stacks.get(slot).rmv();
            if (this.stacks.get(slot).getQuantity() == 0) {
                this.stacks.remove(slot);
            }
            return true;
        }

    }

    public List<ItemStack> getStacks() {
        return List.copyOf(this.stacks);
    }

    public void useHealthNeeded(Gnome user, Level level) {
        if (user == null || level == null) {
            throw new IllegalArgumentException("le niveau ou le Gnome ne peuvent pas etres null");
        }
        if (user.getLife() < 0 || user.getLife() > user.getMaxLife()) {
            throw new IllegalArgumentException("Le nombre de point de vie est pas normal");
        }
        if (user.getLife() == 0) {
            return;
        }
        if (user.getLife() < user.getMaxLife()) {
            for (int i = 0; i < this.stacks.size(); i++) {
                if (this.stacks.get(i).getItem().getClass() == Heart.class) {
                    use(i, user, level);
                    return;
                }
            }

        }
    }
}

package com.blindpath.domain.inventory;

import java.util.ArrayList;
import java.util.List;

import com.blindpath.domain.character.Character ;
import com.blindpath.domain.game.Level;

/**
 * Inventaire d'un personnage : 5 emplacements, chacun contenant une pile d'un seul type d'objet
 * (5 exemplaires au maximum).
 *
 * @param <T> le type du personnage propriétaire
 */
public class Inventory<T extends Character> {

    private int maxSlots = 5;
    private int maxStackSize = 5;
    private List<ItemStack> stacks;
    private final T owner;

    /**
     * Crée un inventaire vide.
     *
     * @param owner le personnage propriétaire
     */
    public Inventory(T owner) {
        this.stacks = new ArrayList<ItemStack>();
        this.owner = owner; 
    }

    /** @return le personnage propriétaire */
    public T getOwner() {
        return owner;
    }

    /**
     * Ajoute des objets. Ils rejoignent la pile du même type si elle existe, sinon une nouvelle pile
     * est créée s'il reste un emplacement libre. Ce qui dépasse 5 exemplaires est perdu.
     *
     * @param items l'objet à ajouter
     * @param quantity le nombre d'exemplaires
     * @throws IllegalArgumentException si l'objet est {@code null} ou la quantité négative
     */
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
            ItemStack actualStack = this.stacks.get(i);
            if (actualStack.getItem().getClass() == items.getClass()) {
                actualStack.add(quantity);
                return;
            }
        }

        // Aucun type correspondant trouvé : crée une seule pile si une place reste.
        if (this.stacks.size() < maxSlots) {
            this.stacks.add(new ItemStack(items, Math.min(quantity, maxStackSize)));
        }
    }

    /**
     * Utilise un objet de l'inventaire. S'il a pu servir, un exemplaire est retiré,
     * et la pile disparaît quand elle est vide.
     *
     * @param slot l'emplacement de l'objet, à partir de 0
     * @param user le personnage qui l'utilise
     * @param level le niveau en cours
     * @return {@code true} si l'objet a été utilisé
     * @throws IllegalArgumentException si le personnage ou le niveau est {@code null},
     *         ou si l'emplacement n'existe pas
     */
    public boolean use(int slot, T user, Level<?> level) {
        if (user == null || level == null) {
            throw new IllegalArgumentException("le niveau ou le personnage ne peuvent pas etres null");
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

    /** @return une copie non modifiable des piles de l'inventaire */
    public List<ItemStack> getStacks() {
        return List.copyOf(this.stacks);
    }

    /**
     * Soin automatique : si le personnage est blessé mais vivant, utilise un cœur de l'inventaire.
     *
     * @param user le personnage
     * @param level le niveau en cours
     * @throws IllegalArgumentException si le personnage ou le niveau est {@code null},
     *         ou si sa vie est hors limites
     */
    public void useHealthNeeded(T user, Level<?> level) {
        if (user == null || level == null) {
            throw new IllegalArgumentException("le niveau ou le personnage ne peuvent pas etres null");
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
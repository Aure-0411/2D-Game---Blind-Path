package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

/**
 * Cœur : rend un cœur (2 PV) au joueur, sans dépasser sa vie maximale.
 * Il est utilisé automatiquement quand le joueur est blessé (voir {@link Inventory#useHealthNeeded}).
 */
public class Heart extends Item {

    /** Crée un cœur. */
    public Heart() {
        // Initialise le nom demandé par le constructeur d'Item.
        super("Heart");
    }

    /**
     * Le cœur ne s'utilise que si le joueur est vivant et blessé : il ne ressuscite pas.
     *
     * @param user le joueur
     * @param level le niveau en cours
     * @return {@code true} si le cœur peut être utilisé
     */
    @Override
    public boolean canUse(Character user, Level<?> level) {
        // Réutilise le contrôle des paramètres défini dans Item.
        return super.canUse(user, level)
                && user.getLife() > 0
                && user.getLife() < user.getMaxLife();
    }

    /**
     * Rend 2 PV au joueur, plafonnés à sa vie maximale. Sans effet si {@link #canUse} renvoie {@code false}.
     *
     * @param user le joueur
     * @param level le niveau en cours
     */
    @Override
    public void use(Character user, Level<?> level) {
        // Protège aussi un appel direct : pas de résurrection ni de dépassement.
        if (!canUse(user, level)) {
            return;
        }
        user.setLife(Math.min(user.getMaxLife(), user.getLife() + 2));
        // Inventory.use se charge de consommer une unité de la pile.
    }
}
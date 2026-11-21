package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

public class Heart extends Item {

    public Heart() {
        // Initialise le nom demandé par le constructeur d'Item.
        super("Heart");
    }

    @Override
    public boolean canUse(Character user, Level<?> level) {
        // Réutilise le contrôle des paramètres défini dans Item.
        return super.canUse(user, level)
                && user.getLife() > 0
                && user.getLife() < user.getMaxLife();
    }

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
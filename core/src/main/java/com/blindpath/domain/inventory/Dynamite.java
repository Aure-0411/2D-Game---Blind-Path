package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.object.PlacedDynamite;

public class Dynamite extends Item {

    public Dynamite() {
        super("Dynamite");
    }

    @Override
    public boolean canUse(Character user, Level<?> level) {
        // Réutilise le contrôle des paramètres défini dans Item.
        return super.canUse(user, level)
                && !user.isDead();
    }

    @Override
    public void use(Character user, Level<?> level) {
        if (!canUse(user, level)) {
            return;
        }
        PlacedDynamite placedDynamite = new PlacedDynamite(user.getX(), user.getY(), level);
        level.addPlacedObject(placedDynamite);
    }
}
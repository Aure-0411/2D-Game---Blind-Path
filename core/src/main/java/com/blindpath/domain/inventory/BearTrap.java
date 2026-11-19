package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.object.PlacedBearTrap;

public class BearTrap extends Item {

    public BearTrap() {
        // Initialise le nom demandé par le constructeur d'Item.
        super("BearTrap");
    }

    @Override
    public boolean canUse(Gnome user, Level level) {
        // Réutilise le contrôle des paramètres défini dans Item.
        return super.canUse(user, level)
                && !user.isDead();
    }

    @Override
    public void use(Gnome user, Level level) {
        // Pose un piège à la position du gnome ; c'est le piège posé qui immobilisera le monstre.
        if (!canUse(user, level)) {
            return;
        }
        PlacedBearTrap placedBearTrap = new PlacedBearTrap(user.getX(), user.getY());
        level.addPlacedObject(placedBearTrap);
    }
}

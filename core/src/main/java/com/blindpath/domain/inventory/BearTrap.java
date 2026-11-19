package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.game.object;

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
        // BearTrap utilise la méthode immobililize(float duration) sur le monstre, de type void, et appelle la méthode stun(float duration), de type void.
    }
}

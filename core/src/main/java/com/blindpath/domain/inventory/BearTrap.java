package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.object.PlacedBearTrap;

/**
 * Piège à ours : une fois utilisé, il est posé à la position du joueur
 * et immobilise le monstre qui marche dessus (voir {@link PlacedBearTrap}).
 */
public class BearTrap extends Item {

    /** Crée un piège à ours. */
    public BearTrap() {
        // Initialise le nom demandé par le constructeur d'Item.
        super("BearTrap");
    }

    /**
     * Le piège ne peut être posé que si le joueur est vivant.
     *
     * @param user le joueur
     * @param level le niveau en cours
     * @return {@code true} si le piège peut être posé
     */
    @Override
    public boolean canUse(Character user, Level<?> level) {
        // Réutilise le contrôle des paramètres défini dans Item.
        return super.canUse(user, level)
                && !user.isDead();
    }

    /**
     * Pose le piège à la position du joueur. Sans effet si {@link #canUse} renvoie {@code false}.
     *
     * @param user le joueur
     * @param level le niveau dans lequel poser le piège
     */
    @Override
    public void use(Character user, Level<?> level) {
        // Pose un piège à la position du personage ; c'est le piège posé qui immobilisera le monstre.
        if (!canUse(user, level)) {
            return;
        }
        PlacedBearTrap placedBearTrap = new PlacedBearTrap(user.getX(), user.getY());
        level.addPlacedObject(placedBearTrap);
    }
}
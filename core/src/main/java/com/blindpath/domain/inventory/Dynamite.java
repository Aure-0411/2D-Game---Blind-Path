package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.object.PlacedDynamite;

/**
 * Dynamite : une fois utilisée, elle est posée à la position du joueur
 * et explose après un délai (voir {@link PlacedDynamite}).
 */
public class Dynamite extends Item {

    /** Crée une dynamite. */
    public Dynamite() {
        super("Dynamite");
    }

    /**
     * La dynamite ne peut être posée que si le joueur est vivant.
     *
     * @param user le joueur
     * @param level le niveau en cours
     * @return {@code true} si la dynamite peut être posée
     */
    @Override
    public boolean canUse(Character user, Level<?> level) {
        // Réutilise le contrôle des paramètres défini dans Item.
        return super.canUse(user, level)
                && !user.isDead();
    }

    /**
     * Pose la dynamite à la position du joueur. Sans effet si {@link #canUse} renvoie {@code false}.
     *
     * @param user le joueur
     * @param level le niveau dans lequel poser la dynamite
     */
    @Override
    public void use(Character user, Level<?> level) {
        if (!canUse(user, level)) {
            return;
        }
        PlacedDynamite placedDynamite = new PlacedDynamite(user.getX(), user.getY(), level);
        level.addPlacedObject(placedDynamite);
    }
}
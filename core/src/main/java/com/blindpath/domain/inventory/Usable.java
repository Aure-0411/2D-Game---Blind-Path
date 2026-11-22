package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

/** Ce qui peut être utilisé par un personnage pendant un niveau. */
public interface Usable {
    /**
     * @param user le personnage
     * @param level le niveau en cours
     * @return {@code true} si l'objet peut être utilisé maintenant
     */
    boolean canUse(Character user, Level<?> level);
    /**
     * Applique l'effet de l'objet.
     *
     * @param user le personnage
     * @param level le niveau en cours
     */
    void use(Character user, Level<?> level);
}
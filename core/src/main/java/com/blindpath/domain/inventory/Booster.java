package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

/** Booster : renforce la lanterne pour les {@link #BOOSTED_SNAPS} prochains claquements. */
public class Booster extends Item {
    /** Nombre de claquements renforcés par un Booster. */
    public static final int BOOSTED_SNAPS = 5;

    /** Crée un Booster. */
    public Booster() {
        super("Booster");
    }

    /**
     * Le Booster ne s'utilise que si le joueur est vivant et que la lanterne n'est pas déjà renforcée.
     *
     * @param user le joueur
     * @param level le niveau en cours
     * @return {@code true} si le Booster peut être utilisé
     */
    @Override
    public boolean canUse(Character user, Level<?> level) {
        return super.canUse(user, level)
            && !user.isDead()
            && !user.isLanternBoosted();
    }

    /**
     * Renforce la lanterne du joueur. Sans effet si {@link #canUse} renvoie {@code false}.
     *
     * @param user le joueur
     * @param level le niveau en cours
     */
    @Override
    public void use(Character user, Level<?> level) {
        if(!canUse(user, level)) {
            return;
        }
        user.boostLantern(BOOSTED_SNAPS);
    }
}
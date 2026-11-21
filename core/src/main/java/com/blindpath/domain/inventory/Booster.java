package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;

public class Booster extends Item {
    public static final int BOOSTED_SNAPS = 5;

    public Booster() {
        super("Booster");
    }

    @Override
    public boolean canUse(Gnome user, Level level) {
        return super.canUse(user, level)
            && !user.isDead()
            && !user.isLanternBoosted();
    }

    @Override
    public void use(Gnome user, Level level) {
        if(!canUse(user, level)) {
            return;
        }
        user.boostLantern(BOOSTED_SNAPS);
    }
}

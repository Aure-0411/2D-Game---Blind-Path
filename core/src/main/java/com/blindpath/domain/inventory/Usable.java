package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

public interface Usable {
    boolean canUse(Character user, Level<?> level);
    void use(Character user, Level<?> level);
}
package com.blindpath.domain.inventory;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;


public interface Usable {
    void use(Gnome user, Level level);
}

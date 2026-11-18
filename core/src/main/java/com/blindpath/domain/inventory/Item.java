package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;

public abstract class Item implements Usable {

    private final String name;

    public Item(String name) {
        if (name == null) {
            throw new IllegalArgumentException("L'objet doit avoir un nom");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("L'objet doit avoir un nom non vide visuellement");
        }

        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean canUse(Gnome user, Level level) {
        if (user == null || level == null) {
            throw new IllegalArgumentException("Le User et/ou kle LvL ne peuvent pas etres null");
        }
        return true;

    }

}

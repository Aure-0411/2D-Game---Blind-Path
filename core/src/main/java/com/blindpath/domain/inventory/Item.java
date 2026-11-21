package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

public abstract class Item implements Usable {

    private final String name;

    protected Item(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'objet doit être renseigné.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

        @Override
    public boolean canUse(Character user, Level<?> level) {
        if (user == null || level == null) {
            throw new IllegalArgumentException("Le joueur et le niveau doivent être renseignés.");
        }
        return true;
    }

}
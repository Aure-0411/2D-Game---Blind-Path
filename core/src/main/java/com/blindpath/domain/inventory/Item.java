package com.blindpath.domain.inventory;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;

/**
 * Objet que le joueur peut ramasser dans un coffre et utiliser.
 * Chaque sous-classe décide quand elle peut servir et ce qu'elle fait.
 */
public abstract class Item implements Usable {

    private final String name;

    /**
     * @param name le nom de l'objet
     * @throws IllegalArgumentException si le nom est vide
     */
    protected Item(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'objet doit être renseigné.");
        }
        this.name = name;
    }

    /** @return le nom de l'objet */
    public String getName() {
        return name;
    }

    /**
     * Vérification commune à tous les objets : le joueur et le niveau doivent exister.
     * Les sous-classes ajoutent leurs propres conditions.
     *
     * @param user le joueur
     * @param level le niveau en cours
     * @return {@code true}
     * @throws IllegalArgumentException si le joueur ou le niveau est {@code null}
     */
        @Override
    public boolean canUse(Character user, Level<?> level) {
        if (user == null || level == null) {
            throw new IllegalArgumentException("Le joueur et le niveau doivent être renseignés.");
        }
        return true;
    }

}
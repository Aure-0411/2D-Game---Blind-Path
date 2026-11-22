package com.blindpath.domain.character;

/** Le héros du jeu : le gnome, avec une hitbox d'une case et une vitesse de 2,75 cases par seconde. */
public class Gnome extends Character {

    /** Taille de la hitbox du gnome, en cases. */
    public static final float HITBOX_SIZE = 1f;

    /** Crée le gnome à l'origine. Sa position est fixée ensuite par l'entrée du niveau. */
    public Gnome() {
        super(2.75f, 0, 0, HITBOX_SIZE, HITBOX_SIZE);
    }
}
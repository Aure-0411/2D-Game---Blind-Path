package com.blindpath.presentation.renderer;

import com.blindpath.domain.character.Direction;

public class GnomeSprites {
    public static final String ATLAS_PATH = "gnome/gnome.atlas"; // Votre chemin

    public static String regionName(Direction d) {
        return switch (d) {
            case DOWN -> "walk_down";
            case UP -> "walk_up";
            case LEFT -> "walk_left";
            case RIGHT -> "walk_right";
            case DOWN_LEFT -> "walk_down_left";
            case DOWN_RIGHT -> "walk_down_right";
            case UP_LEFT -> "walk_up_left";
            case UP_RIGHT -> "walk_up_right";
            case NONE -> "walk_down"; // Sécurité
        };
    }
}
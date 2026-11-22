package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

/**
 * Les 5 types de monstres. Chaque type sait créer son monstre, ce qui permet
 * de choisir le monstre d'un niveau par son nom dans la carte.
 */
public enum MonsterType {
    /** Bondit sur le héros quand il est proche. */
    JUMPER(Jumper::new),
    /** Fuit vers la sortie quand le héros claque des doigts. */
    RUNAWAY(Runaway::new),
    /** Crie après plusieurs claquements et étourdit le héros. */
    SCREAMER(Screamer::new),
    /** Invisible sous le brouillard, sauf de près. */
    CHAMELEON(Chameleon::new),
    /** Inverse les commandes quand il est proche. */
    INVERTER(Inverter::new);

    @FunctionalInterface
    private interface Constructor {
        Monster create(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder);
    }

    private final Constructor constructor;

    MonsterType(Constructor constructor) {
        this.constructor = constructor;
    }

    /**
     * @param grid la grille du labyrinthe
     * @param start la case de départ
     * @param pathfinder le calcul de chemin
     * @return un nouveau monstre de ce type
     */
    public Monster create(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        return constructor.create(grid, start, pathfinder);
    }
}
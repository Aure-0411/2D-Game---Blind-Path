package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

/**
 * Jumper (palier facile) : il poursuit le héros, et quand il arrive tout près,
 * il marque un temps d'alerte puis bondit sur lui (voir {@link JumperStrategy}).
 */
public class Jumper extends Monster {

    private final JumperStrategy jumperStrategy;

    /**
     * Crée un Jumper avec sa stratégie de saut.
     *
     * @param grid la grille du labyrinthe
     * @param start la case de départ
     * @param pathfinder le calcul de chemin
     */
    public Jumper(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, start, new JumperStrategy(pathfinder));
    }

    /**
     * Crée un Jumper avec une stratégie de déplacement choisie (utile pour les tests).
     * Si ce n'est pas une {@link JumperStrategy}, {@link #getState()} renvoie {@code null}.
     *
     * @param grid la grille du labyrinthe
     * @param strategy la stratégie de déplacement
     * @param start la case de départ
     */
    public Jumper(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        super(grid, strategy, start);
        this.jumperStrategy = (strategy instanceof JumperStrategy js) ? js : null;
    }

    private Jumper(LabyrinthGrid grid, GridPosition start, JumperStrategy strategy) {
        super(grid, strategy, start);
        this.jumperStrategy = strategy;
    }

    /** @return l'état actuel du Jumper (poursuite ou alerte), ou {@code null} sans stratégie de saut */
    public JumperState getState() {
        return jumperStrategy != null ? jumperStrategy.getState() : null;
    }
}
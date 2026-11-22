package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.time.GameTimer;

/**
 * Inverter (palier extrême) : tant qu'il est à {@link #INVERT_RANGE} cases ou moins du héros,
 * les commandes du joueur sont inversées. L'effet dure encore
 * {@link #INVERT_AFTER_EXIT_DURATION} secondes après que le héros est sorti de sa portée.
 */
public class Inverter extends Monster {

    /** Nombre de cases parcourues à chaque déplacement. */
    public static final int CASE_PER_MOVE = 1;
    /** Portée de l'inversion des commandes, en cases (dans les 8 directions). */
    public static final int INVERT_RANGE = 3;
    /** Durée de l'inversion après la sortie de portée, en secondes. */
    public static final float INVERT_AFTER_EXIT_DURATION = 3f;

    private final GameTimer afterExitTimer = new GameTimer();
    private boolean wasInRange;

    /**
     * Crée un Inverter qui poursuit le héros.
     *
     * @param grid la grille du labyrinthe
     * @param start la case de départ
     * @param pathfinder le calcul de chemin
     */
    public Inverter(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, new ChaseStrategy(pathfinder), start);
    }

    /**
     * Crée un Inverter avec une stratégie de déplacement choisie (utile pour les tests).
     *
     * @param grid la grille du labyrinthe
     * @param strategy la stratégie de déplacement
     * @param start la case de départ
     */
    public Inverter(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        super(grid, strategy, start);
        setCasePerMove(CASE_PER_MOVE);
    }

    /** @return {@code true} si le héros est à {@link #INVERT_RANGE} cases ou moins */
    public boolean isInRange() {
        GridPosition target = getTarget();
        if (target == null) {
            return false;
        }
        int dc = Math.abs(target.getColumn() - getPosition().getColumn());
        int dr = Math.abs(target.getRow() - getPosition().getRow());
        return Math.max(dc, dr) <= INVERT_RANGE;
    }

    /**
     * @return {@code true} si les commandes du joueur doivent être inversées
     *         (héros à portée, ou sorti de portée depuis moins de 3 secondes)
     */
    public boolean invertsControls() {
        return isInRange() || afterExitTimer.isRunning();
    }

    /** @return {@code true} si l'inversion continue alors que le héros est déjà sorti de portée */
    public boolean isAfterExitActive() {
        return !isInRange() && afterExitTimer.isRunning();
    }

    /**
     * Fait avancer les minuteurs et démarre l'inversion « après sortie » quand le héros
     * vient de quitter la portée.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);
        boolean inRange = isInRange();

        if (inRange) {
            afterExitTimer.stop();
        } 
        
        else if (wasInRange) {
            afterExitTimer.start(INVERT_AFTER_EXIT_DURATION);
        } 
        
        else {
            afterExitTimer.update(deltaTime);
        }
        wasInRange = inRange;
    }
    
}
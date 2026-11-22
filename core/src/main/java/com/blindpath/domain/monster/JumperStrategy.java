package com.blindpath.domain.monster;

import java.util.List;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;

/**
 * Stratégie du Jumper (patron Strategy). Quand le héros est à portée de saut, le Jumper
 * s'arrête un pas (alerte), puis bondit directement sur la case du héros si elle est libre.
 * Sinon, il le poursuit comme {@link ChaseStrategy}.
 */
public class JumperStrategy implements MovementStrategy {

    /** Portée de saut par défaut, en cases (distance horizontale + verticale). */
    public static final int DEFAULT_JUMP_RANGE = 2;

    private final Pathfinder pathfinder;
    private final int jumpRange;
    private JumperState state = JumperState.CHASING;

    /**
     * @param pathfinder le calcul de chemin
     * @param jumpRange la portée du saut, en cases
     * @throws IllegalArgumentException si le pathfinder est {@code null} ou la portée inférieure à 1
     */
    public JumperStrategy(Pathfinder pathfinder, int jumpRange) {

        if (pathfinder == null) {
            throw new IllegalArgumentException("pathfinder must not be null");
        }

        if (jumpRange < 1) {
            throw new IllegalArgumentException("jumpRange must be >= 1");
        }

        this.pathfinder = pathfinder;
        this.jumpRange = jumpRange;
    }

    /**
     * Crée une stratégie avec la portée {@link #DEFAULT_JUMP_RANGE}.
     *
     * @param pathfinder le calcul de chemin
     */
    public JumperStrategy(Pathfinder pathfinder) {
        this(pathfinder, DEFAULT_JUMP_RANGE);
    }

    /** @return l'état actuel (poursuite ou alerte) */
    public JumperState getState() {
        return state;
    }

    /** @return la portée du saut, en cases */
    public int getJumpRange() {
        return jumpRange;
    }

    private boolean isInRange(GridPosition from, GridPosition target) {

        int dx = Math.abs(from.getColumn() - target.getColumn());
        int dy = Math.abs(from.getRow() - target.getRow());
        int distance = dx + dy;
        
        if (distance > 0 && distance <= jumpRange) {
            return true;
        }

        return false;
        
    }

    private GridPosition chase (LabyrinthGrid grid, GridPosition from, GridPosition target) {

        List<GridPosition> path = pathfinder.findPath(grid, from, target);

        if (path.isEmpty()) {
            return from;
        }

        return path.get(0);
    }

    /**
     * Calcule la prochaine case : en alerte, le Jumper bondit sur la cible si elle est à portée
     * et libre ; à portée, il passe en alerte et reste sur place ; sinon il poursuit.
     *
     * @param grid la grille du labyrinthe
     * @param from la case actuelle du monstre
     * @param target la case du héros
     * @return la prochaine case du monstre
     */
    @Override
    public GridPosition nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target) {

        if (state == JumperState.ALERT) {

            state = JumperState.CHASING;

            if (isInRange(from, target) && grid.isWalkable(target)) {
                return target;
            }

            return chase(grid, from, target);
        }

        if (isInRange(from, target)) {
            state = JumperState.ALERT;

            return from;
        }

        return chase(grid, from, target);
    }
    
}
package com.blindpath.domain.monster;

import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapListener;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.time.TickListener;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Collidable;
import com.blindpath.domain.time.GameTimer;

/**
 * Classe de base des monstres. Possèdent tous : grid position, target,
 * tick rhythm et snap reaction. Children (Jumper, Screamer, ...)
 * override les méthodes par ceux dans leur comportement.
 */
public abstract class Monster implements SnapListener, TickListener, Collidable {
    /** Nombre de ticks entre deux déplacements (30 ticks = 1 seconde). */
    public static final int DEFAULT_TICKS_PER_MOVE = 30;
    /** Nombre de cases parcourues par déplacement. */
    public static final int DEFAULT_CASE_PER_MOVE = 1;
    /** Nombre de cases parcourues vers un claquement. */
    public static final int DEFAULT_SNAP_DISTANCE = 1;
    /** Taille du monstre, en cases (2 x 2). */
    public static final int SIZE = 2;

    private final LabyrinthGrid grid;
    private final MovementStrategy strategy;
    private GridPosition position;
    private GridPosition target; // le héros, peut être null
    private int ticksPerMove = DEFAULT_TICKS_PER_MOVE;
    private int casePerMove = DEFAULT_CASE_PER_MOVE;
    private int snapDistance = DEFAULT_SNAP_DISTANCE;
    private boolean visible = false;
    private final GameTimer immobilization = new GameTimer();

    /**
     * @param grid la grille du labyrinthe
     * @param strategy la stratégie de déplacement
     * @param start la case de départ
     * @throws IllegalArgumentException si un paramètre est {@code null}
     */
    protected Monster(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start) {
        if (grid == null || strategy == null || start == null)
            throw new IllegalArgumentException("null argument");
        this.grid = grid;
        this.strategy = strategy;
        this.position = start;
    }

    /** @return la grille du labyrinthe */
    protected LabyrinthGrid getGrid() { return grid; }
    /** @return la case visée (le héros), ou {@code null} */
    protected GridPosition getTarget() { return target; }

    /** @return la case du coin bas gauche du monstre */
    public GridPosition getPosition() { return position; }
    /** @param t la case visée (le héros), ou {@code null} pour ne plus poursuivre */
    public void setTarget(GridPosition t) { this.target = t; }

    /** @return le nombre de ticks entre deux déplacements */
    public int getTicksPerMove() { return ticksPerMove; }
    /**
     * @param v le nombre de ticks entre deux déplacements
     * @throws IllegalArgumentException si la valeur n'est pas positive
     */
    public void setTicksPerMove(int v) {
        if (v <= 0) throw new IllegalArgumentException("ticksPerMove <= 0");
        ticksPerMove = v;
    }

    /** @return le nombre de cases par déplacement */
    public int getCasePerMove() { return casePerMove; }
    /**
     * @param v le nombre de cases par déplacement
     * @throws IllegalArgumentException si la valeur n'est pas positive
     */
    public void setCasePerMove(int v) {
        if (v <= 0) throw new IllegalArgumentException("casePerMove <= 0");
        casePerMove = v;
    }

    /** @return le nombre de cases parcourues vers un claquement */
    public int getSnapDistance() { return snapDistance; }
    /**
     * @param v le nombre de cases parcourues vers un claquement
     * @throws IllegalArgumentException si la valeur est négative
     */
    public void setSnapDistance(int v) {
        if (v < 0) throw new IllegalArgumentException("snapDistance < 0");
        snapDistance = v;
    }

    /** @return {@code true} si le monstre est affiché (invisible par défaut) */
    public boolean isVisible() {
        return visible;
    }

    /** @param visible {@code true} pour afficher le monstre */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Immobilise le monstre (piège à ours). Une immobilisation plus longue déjà en cours est conservée.
     *
     * @param duration durée, en secondes
     * @throws IllegalArgumentException si la durée est négative
     */
    public void immobilize(float duration) {
        startLongest(immobilization, duration);
    }

    private static void startLongest(GameTimer timer, float duration) {
        if (!(duration >= 0)) {
            throw new IllegalArgumentException("duration must be >= 0");
        }
        
        if (duration > timer.getRemaining()) {
            timer.start(duration);
        }

    }

    /** @return {@code true} tant que le monstre est immobilisé */
    public boolean isImmobilized() {
        return immobilization.isRunning();
    }

    /** @return {@code true} si le monstre peut bouger */
    public boolean canMove() {
        return !isImmobilized();
    }

    /**
     * Fait avancer les effets temporaires du monstre.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    public void update(float deltaTime) {
        immobilization.update(deltaTime);
    }

    /**
     * Avance vers une case, pas à pas, selon la stratégie de déplacement.
     *
     * @param goal la case visée
     * @param steps le nombre de pas
     */
    protected void moveTowards(GridPosition goal, int steps) {
        for (int i = 0; i < steps; i++)
            position = strategy.nextStep(grid, position, goal);
    }

    /**
     * Déplace le monstre vers le héros tous les {@link #getTicksPerMove()} ticks.
     *
     * @param tick le numéro du tick
     */
    @Override
    public void onTick(long tick) {
        if (!canMove()) return;
        if (target != null && tick % ticksPerMove == 0) moveTowards(target, casePerMove);
    }

    /**
     * Rapproche le monstre du claquement de {@link #getSnapDistance()} cases.
     * Les monstres réagissent au claquement, jamais à la lumière.
     *
     * @param event le claquement
     */
    @Override
    public void onSnap(SnapEvent event) {
        if (!canMove()) return;
        GridPosition origin = grid.anchorCovering(new GridPosition(
            (int) Math.floor(event.getSourceX()), (int) Math.floor(event.getSourceY())));
        moveTowards(origin, snapDistance);
    }

    // ---- Collidable ----
    @Override
    public boolean canCollide() { return true; }

    @Override
    public boolean collide(Collidable other) {
        return canCollide() && other.canCollide() && getBounds().intersects(other.getBounds());
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(position.getColumn(), position.getRow(), SIZE, SIZE);
    }
}
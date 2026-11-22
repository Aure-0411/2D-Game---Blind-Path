package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import java.util.Random;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.time.GameTimer;

/**
 * Screamer (palier difficile) : il poursuit le héros, et après un nombre aléatoire de claquements
 * (entre {@link #MIN_SNAPS} et {@link #MAX_SNAPS}), il crie et étourdit le héros
 * pendant {@link #STUN_DURATION} secondes.
 */
public class Screamer extends Monster {

    private final Random random;
    private int screamThreshold;
    private boolean screamed;
    private int snapCount;
    private long screamSequence;
    private final GameTimer screamAnimation = new GameTimer();

    /** Nombre minimal de claquements avant un cri. */
    public static final int MIN_SNAPS = 5;
    /** Nombre maximal de claquements avant un cri. */
    public static final int MAX_SNAPS = 8;
    /** Durée de l'étourdissement du héros, en secondes. */
    public static final float STUN_DURATION = 3f;
    /** Durée de l'animation du cri, en secondes. */
    public static final float SCREAM_ANIMATION_DURATION = 1f;

    /**
     * Crée un Screamer qui poursuit le héros.
     *
     * @param grid la grille du labyrinthe
     * @param start la case de départ
     * @param pathfinder le calcul de chemin
     */
    public Screamer(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, new ChaseStrategy(pathfinder), start, new Random());
    }

    /**
     * Crée un Screamer avec une stratégie et un générateur aléatoire choisis (utile pour les tests).
     *
     * @param grid la grille du labyrinthe
     * @param strategy la stratégie de déplacement
     * @param start la case de départ
     * @param random le générateur qui tire le nombre de claquements avant un cri
     * @throws IllegalArgumentException si le générateur est {@code null}
     */
    public Screamer(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start, Random random) {
        super(grid, strategy, start);
        if (random == null) {
            throw new IllegalArgumentException("random is null");
        }
        this.random = random;
        this.screamThreshold = drawThreshold();
    }

    private int drawThreshold() {
        return MIN_SNAPS + random.nextInt(MAX_SNAPS - MIN_SNAPS + 1);
    }

    /** @return {@code true} pendant l'animation du cri */
    public boolean isScreaming() {
        return screamAnimation.isRunning();
    }

    /**
     * Rapproche le Screamer du claquement, compte les claquements et crie quand le seuil est atteint.
     * Le seuil est ensuite tiré à nouveau.
     *
     * @param event le claquement
     */
    @Override
    public void onSnap(SnapEvent event) {

        super.onSnap(event);
        snapCount++;

        if (snapCount >= screamThreshold) {
            screamed = true;
            screamSequence++;
            screamAnimation.start(SCREAM_ANIMATION_DURATION);
            snapCount = 0;
            screamThreshold = drawThreshold();
        }
    }

    /**
     * Fait avancer les effets temporaires et l'animation du cri.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        screamAnimation.update(deltaTime);
    }

    /**
     * Indique si le Screamer vient de crier, et remet l'information à zéro.
     *
     * @return {@code true} une seule fois par cri
     */
    public boolean consumeScream() {
        boolean result = screamed;
        screamed = false;
        return result;
    }

    /**
     * Compteur de cris, observable par l'affichage, indépendant de {@link #consumeScream()}.
     *
     * @return le nombre de cris depuis le début du niveau
     */
    public long getScreamSequence() {
        return screamSequence;
    }

    /** @return le nombre de claquements depuis le dernier cri */
    public int getSnapCount() {
        return snapCount;
    }

    /** @return le nombre de claquements qui déclenchera le prochain cri */
    public int getScreamThreshold() {
        return screamThreshold;
    }

}


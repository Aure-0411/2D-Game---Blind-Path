package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.Pathfinder;
import java.util.Random;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.time.GameTimer;

public class Screamer extends Monster {

    private final Random random;
    private int screamThreshold;
    private boolean screamed;
    private int snapCount;
    private long screamSequence;
    private final GameTimer screamAnimation = new GameTimer();

    public static final int MIN_SNAPS = 5;
    public static final int MAX_SNAPS = 8;
    public static final float STUN_DURATION = 2f;
    public static final float SCREAM_ANIMATION_DURATION = 0.6f;

    public Screamer(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) {
        this(grid, new ChaseStrategy(pathfinder), start, new Random());
    }

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

    public boolean isScreaming() {
        return screamAnimation.isRunning();
    }

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

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        screamAnimation.update(deltaTime);
    }

    public boolean consumeScream() {
        boolean result = screamed;
        screamed = false;
        return result;
    }

    /** Compteur observable par la présentation, indépendant de consumeScream(). */
    public long getScreamSequence() {
        return screamSequence;
    }

    public int getSnapCount() {
        return snapCount;
    }

    public int getScreamThreshold() {
        return screamThreshold;
    }

}


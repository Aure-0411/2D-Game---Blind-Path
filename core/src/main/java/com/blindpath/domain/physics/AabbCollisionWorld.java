package com.blindpath.domain.physics;

import java.util.ArrayList;
import java.util.List;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Bounds;

/**
 * Implémentation Java pure (rectangles alignés sur les axes) : testable sans natives,
 * utilisable en CI. Résolution axe par axe : le personnage glisse le long des murs.
 */
public class AabbCollisionWorld implements CollisionWorld {

    private static final float EPSILON = 1e-6f;
    private static final int BISECTION_STEPS = 12;

    private final List<Bounds> statics = new ArrayList<>();

    @Override
    public void addStatic(Bounds bounds) {
        if (bounds == null) {
            throw new IllegalArgumentException("Les bounds doivent être renseignés.");
        }
        statics.add(bounds);
    }

    @Override
    public void addDynamic(Character character) {
        // Rien à enregistrer : la position du personnage suffit.
    }

    @Override
    public boolean move(Character c, float dirX, float dirY, float deltaTime) {
        float startX = c.getX();
        float startY = c.getY();
        float dx = dirX * c.getSpeed() * deltaTime;
        float dy = dirY * c.getSpeed() * deltaTime;

        // Axe X puis axe Y : si l'un est bloqué, l'autre continue (glissement).
        float x = startX + dx * reach(c, startX, startY, dx, 0f);
        float y = startY + dy * reach(c, x, startY, 0f, dy);
        c.setPosition(x, y);

        return Math.abs(x - startX) > EPSILON || Math.abs(y - startY) > EPSILON;
    }

    /** Fraction (0..1) du déplacement (dx, dy) réalisable avant de toucher un mur. */
    private float reach(Character c, float x, float y, float dx, float dy) {
        if (dx == 0f && dy == 0f || isFree(c, x + dx, y + dy)) {
            return 1f;
        }
        float lo = 0f;
        float hi = 1f;
        for (int i = 0; i < BISECTION_STEPS; i++) {
            float mid = (lo + hi) / 2f;
            if (isFree(c, x + dx * mid, y + dy * mid)) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    private boolean isFree(Character c, float x, float y) {
        Bounds candidate = new Bounds(x, y, c.getWidth(), c.getHeight());
        for (Bounds wall : statics) {
            if (candidate.intersects(wall)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void dispose() {
        statics.clear();
    }
}

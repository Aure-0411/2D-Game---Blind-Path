package com.blindpath.domain.labyrinth;

import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.time.GameTimer;

/** Ronces réagissant au clap, sans bloquer les déplacements. */
public final class Bramble {

    public static final float SNAP_RANGE = 3f;
    public static final float DURATION = 0.5f;
    public static final int DAMAGE = 2; // Deux demi-cœurs.

    private final float x;
    private final float y;
    private final OpeningOrientation orientation;
    private final GameTimer timer = new GameTimer();

    public Bramble(float x, float y, OpeningOrientation orientation) {
        if (!Float.isFinite(x) || !Float.isFinite(y)) {
            throw new IllegalArgumentException("La position des ronces doit être finie.");
        }
        if (orientation == null) {
            throw new IllegalArgumentException("L'orientation des ronces doit être renseignée.");
        }
        this.x = x;
        this.y = y;
        this.orientation = orientation;
    }

    public OpeningOrientation getOrientation() {
        return orientation;
    }

    public Bounds getBounds() {
        // Une copie évite d'exposer la géométrie interne aux appelants.
        return new Bounds(x, y,
                orientation == OpeningOrientation.HORIZONTAL ? 2f : 1f,
                orientation == OpeningOrientation.HORIZONTAL ? 1f : 2f);
    }

    public boolean isActive() {
        return timer.isRunning();
    }

    public void activate() {
        if (!isActive()) timer.start(DURATION);
    }

    public void deactivate() {
        timer.stop();
    }

    public void update(float delta) {
        timer.update(delta);
    }

    public void onSnap(SnapEvent event) {
        if (event == null || event.getSource() != SnapSource.HERO) return;
        Bounds ground = getBounds();
        // Distance du point du clap au point le plus proche des deux cases au sol.
        float closestX = Math.max(x, Math.min(event.getSourceX(), x + ground.getWidth()));
        float closestY = Math.max(y, Math.min(event.getSourceY(), y + ground.getHeight()));
        float dx = event.getSourceX() - closestX;
        float dy = event.getSourceY() - closestY;
        if (dx * dx + dy * dy <= SNAP_RANGE * SNAP_RANGE) activate();
    }

    /** Changement instantané : taille maximale pendant 0,5 s, puis repos. */
    public float getExtension() {
        return isActive() ? 1f : 0f;
    }

    public Bounds getDamageBounds() {
        Bounds ground = getBounds();
        float extension = getExtension();
        return orientation == OpeningOrientation.HORIZONTAL
                ? new Bounds(x, y - extension, ground.getWidth(), 1f + 2f * extension)
                : new Bounds(x - extension, y, 1f + 2f * extension, ground.getHeight());
    }

    public boolean damages(Bounds target) {
        return isActive() && getDamageBounds().intersects(target);
    }
}

package com.blindpath.domain.labyrinth;

import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.time.GameTimer;

/**
 * Ronces : un claquement du héros à moins de {@link #SNAP_RANGE} cases les fait jaillir
 * pendant {@link #DURATION} seconde. Pendant ce temps, elles blessent ce qui les touche.
 * Elles ne bloquent jamais les déplacements.
 */
/** Ronces réagissant au clap, sans bloquer les déplacements. */
public final class Bramble {

    /** Distance maximale entre le claquement et les ronces pour les déclencher, en cases. */
    public static final float SNAP_RANGE = 3f;
    /** Durée pendant laquelle les ronces restent sorties, en secondes. */
    public static final float DURATION = 0.5f;
    /** Dégâts des ronces : 2 PV, soit 1 cœur. */
    public static final int DAMAGE = 2; // Deux demi-cœurs.

    private final float x;
    private final float y;
    private final OpeningOrientation orientation;
    private final GameTimer timer = new GameTimer();

    /**
     * @param x position horizontale du coin bas gauche
     * @param y position verticale du coin bas gauche
     * @param orientation sens des deux cases de ronces
     * @throws IllegalArgumentException si la position n'est pas finie ou l'orientation {@code null}
     */
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

    /** @return le sens des deux cases de ronces */
    public OpeningOrientation getOrientation() {
        return orientation;
    }

    /** @return les deux cases au sol occupées par les ronces au repos */
    public Bounds getBounds() {
        // Une copie évite d'exposer la géométrie interne aux appelants.
        return new Bounds(x, y,
                orientation == OpeningOrientation.HORIZONTAL ? 2f : 1f,
                orientation == OpeningOrientation.HORIZONTAL ? 1f : 2f);
    }

    /** @return {@code true} pendant que les ronces sont sorties */
    public boolean isActive() {
        return timer.isRunning();
    }

    /** Fait sortir les ronces. Sans effet si elles sont déjà sorties. */
    public void activate() {
        if (!isActive()) timer.start(DURATION);
    }

    /** Rentre les ronces immédiatement. */
    public void deactivate() {
        timer.stop();
    }

    /**
     * Fait avancer le minuteur des ronces.
     *
     * @param delta temps écoulé depuis la dernière image, en secondes
     */
    public void update(float delta) {
        timer.update(delta);
    }

    /**
     * Déclenche les ronces si le héros claque des doigts assez près.
     * Les autres sources de claquement sont ignorées.
     *
     * @param event le claquement ({@code null} est ignoré)
     */
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

    /**
     * Taille de la sortie des ronces. Le changement est instantané :
     * taille maximale pendant 0,5 s, puis retour au repos.
     *
     * @return 1 si les ronces sont sorties, 0 sinon
     */
    public float getExtension() {
        return isActive() ? 1f : 0f;
    }

    /**
     * @return la zone qui blesse : les deux cases au sol, élargies d'une case de chaque côté quand
     *         les ronces sont sorties
     */
    public Bounds getDamageBounds() {
        Bounds ground = getBounds();
        float extension = getExtension();
        return orientation == OpeningOrientation.HORIZONTAL
                ? new Bounds(x, y - extension, ground.getWidth(), 1f + 2f * extension)
                : new Bounds(x - extension, y, 1f + 2f * extension, ground.getHeight());
    }

    /**
     * @param target la hitbox à tester
     * @return {@code true} si les ronces sont sorties et touchent la hitbox
     */
    public boolean damages(Bounds target) {
        return isActive() && getDamageBounds().intersects(target);
    }
}

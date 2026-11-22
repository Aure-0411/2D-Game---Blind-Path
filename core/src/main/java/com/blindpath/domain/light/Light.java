package com.blindpath.domain.light;

import com.blindpath.domain.time.GameTimer;

/**
 * Lanterne magique du héros. Un claquement de doigts révèle le labyrinthe autour du héros
 * pendant {@link #DEFAULT_REVEAL_DURATION} seconde, puis la lanterne doit recharger.
 * <p>
 * Le rayon révélé grandit puis diminue pendant la révélation. En dehors, seul un petit cercle
 * autour du héros reste visible. La lumière est purement visuelle : les monstres réagissent
 * au claquement, pas à la lumière.
 */
public class Light implements Revealable {

    /** Rayon de révélation par défaut, en cases. */
    public static float DEFAULT_RADIUS = 4.5f;
    /** Durée de la révélation par défaut, en secondes. */
    public static float DEFAULT_REVEAL_DURATION = 1f;
    /** Temps de recharge entre deux claquements, en secondes. */
    public static final float DEFAULT_COOLDOWN = 1.5f;
    /** Multiplicateur du rayon quand la lanterne est renforcée par un Booster. */
    public static final float BOOST_MULTIPLIER = 1.5f;

    private final float baseRadius;
    private final float revealDuration;
    private final float cooldown;
    private int bonusPercent;
    private boolean boosted = false;
    private final GameTimer revealTimer = new GameTimer();
    private final GameTimer cooldownTimer = new GameTimer();
    private float originX;
    private float originY;
    private float snapX;
    private float snapY;
    private float heroCenterX;
    private float heroCenterY;

    /**
     * @param baseRadius rayon de révélation, en cases
     * @param revealDuration durée de la révélation, en secondes
     * @param cooldown temps de recharge, en secondes
     * @throws IllegalArgumentException si un paramètre n'est pas positif
     */
    public Light(float baseRadius, float revealDuration, float cooldown) {

        if (baseRadius <= 0 || revealDuration <= 0 || cooldown <= 0) {
            throw new IllegalArgumentException("Light parameters must be positive");
        }

        this.baseRadius = baseRadius;
        this.revealDuration = revealDuration;
        this.cooldown = cooldown;
    }

    /** Crée une lanterne avec les valeurs par défaut. */
    public Light() {
        this(DEFAULT_RADIUS, DEFAULT_REVEAL_DURATION, DEFAULT_COOLDOWN);
    }

    /**
     * Indique où se trouve le héros, pour placer le prochain claquement.
     *
     * @param x position horizontale
     * @param y position verticale
     */
    public void setOrigin(float x, float y) {
        originX = x;
        originY = y;
    }

    /**
     * Claque des doigts : démarre la révélation et la recharge, sauf si la lanterne recharge encore.
     *
     * @return {@code true} si le claquement a eu lieu
     */
    @Override
    public boolean snap() {
        if (cooldownTimer.isRunning()) {
            return false;
        }

        snapX = originX;
        snapY = originY;

        revealTimer.start(revealDuration);
        cooldownTimer.start(cooldown);

        return true;
    }

    /**
     * Fait avancer la révélation et la recharge.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    public void update(float deltaTime) {
        revealTimer.update(deltaTime);
        cooldownTimer.update(deltaTime);
    }

    /** @return {@code true} pendant la révélation */
    public boolean isRevealing() {
        return revealTimer.isRunning();
    }

    /** @return {@code true} pendant la recharge */
    public boolean isOnCooldown() {
        return cooldownTimer.isRunning();
    }

    /** @return le rayon maximal de révélation, bonus et boost compris */
    public float getRevealRadius() {
        float radius = baseRadius * (100 + bonusPercent) / 100f;
        if (boosted) {
            radius = radius * BOOST_MULTIPLIER;
        }
        return radius;
    }

    /** @param boosted {@code true} pour multiplier le rayon par {@link #BOOST_MULTIPLIER} */
    public void setBoosted(boolean boosted) {
        this.boosted = boosted;
    }

    /** @return le bonus de rayon cumulé, en pourcentage */
    public int getBonusPercent() {
        return bonusPercent;
    }

    /** @return la position horizontale du dernier claquement */
    public float getSnapX() {
        return snapX;
    }

    /** @return la position verticale du dernier claquement */
    public float getSnapY() {
        return snapY;
    }

    /**
     * Augmente le rayon de révélation.
     *
     * @param percent le bonus à ajouter, en pourcentage
     * @throws IllegalArgumentException si le bonus est négatif
     */
    public void addBonusPercent(int percent) {

        if(percent < 0) {
            throw new IllegalArgumentException("Bonus must be >= 0");
        }
        bonusPercent += percent;
    }

    /** @return l'avancement de la révélation, de 0 à 1 (0 en dehors d'une révélation) */
    public float getRevealProgress() {
        if (!isRevealing()) {
            return 0f;
        }
        return revealTimer.getProgress();
    }

    /**
     * Rayon révélé à cet instant : il grandit jusqu'au rayon maximal à mi-parcours, puis diminue.
     *
     * @return le rayon actuel, 0 en dehors d'une révélation
     */
    public float getCurrentRadius() {
        if (!isRevealing()) {
            return 0f;
        }
        float progress = revealTimer.getProgress();
        return getRevealRadius() * (1f - Math.abs(2f * progress - 1f));
    }

    /**
     * La visibilité permanente autour du héros, en dehors des claquements.
     *
     * @return le rayon toujours visible, en cases
     */
    public float getPermanentVisibilityRadius() {
        return 0.8f;
    }

    /**
     * Met à jour le centre du héros, utilisé pour la visibilité permanente.
     *
     * @param centerX position horizontale du centre
     * @param centerY position verticale du centre
     */
    public void updateHeroPosition(float centerX, float centerY) {
        this.heroCenterX = centerX;
        this.heroCenterY = centerY;
    }

    /**
     * Teste si un point du monde (et non un indice de case) est dans la zone toujours visible. Bord inclus.
     *
     * @param worldX position horizontale du point
     * @param worldY position verticale du point
     * @return {@code true} si le point est visible
     */
    public boolean isVisible(float worldX, float worldY) {
        float dx = worldX - heroCenterX;
        float dy = worldY - heroCenterY;
        float radius = getPermanentVisibilityRadius();
        return dx * dx + dy * dy <= radius * radius;
    }

    /** @return la position horizontale du centre du héros */
    public float getHeroCenterX() {
        return heroCenterX;
    }

    /** @return la position verticale du centre du héros */
    public float getHeroCenterY() {
        return heroCenterY;
    }
    /** @return l'avancement de la recharge, de 0 à 1 */
    public float getCooldownProgress(){
        return cooldownTimer.getProgress();
    }
}

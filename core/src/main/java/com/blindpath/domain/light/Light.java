package com.blindpath.domain.light;

import com.blindpath.domain.time.GameTimer;

public class Light implements Revealable {

    public static final float DEFAULT_RADIUS = 3f;
    public static final float DEFAULT_REVEAL_DURATION = 1f;
    public static final float DEFAULT_COOLDOWN = 2f;

    private final float baseRadius;
    private final float revealDuration;
    private final float cooldown;
    private int bonusPercent;
    private final GameTimer revealTimer = new GameTimer();
    private final GameTimer cooldownTimer = new GameTimer();
    private float originX;
    private float originY;
    private float snapX;
    private float snapY;

    public Light(float baseRadius, float revealDuration, float cooldown) {

        if (baseRadius <= 0 || revealDuration <= 0 || cooldown <= 0) {
            throw new IllegalArgumentException("Light parameters must be positive");
        }

        this.baseRadius = baseRadius;
        this.revealDuration = revealDuration;
        this.cooldown = cooldown;
    }

    public Light() {
        this(DEFAULT_RADIUS, DEFAULT_REVEAL_DURATION, DEFAULT_COOLDOWN);
    }

    public void setOrigin(float x, float y) {
        originX = x;
        originY = y;
    }

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

    public void update(float deltaTime) {
        revealTimer.update(deltaTime);
        cooldownTimer.update(deltaTime);
    }

    public boolean isRevealing() {
        return revealTimer.isRunning();
    }

    public boolean isOnCooldown() {
        return cooldownTimer.isRunning();
    }

    public float getRevealRadius() {
        return baseRadius * (100 + bonusPercent) / 100f;
    }

    public int getBonusPercent() {
        return bonusPercent;
    }

    public float getSnapX() {
        return snapX;
    }

    public float getSnapY() {
        return snapY;
    }

    public void addBonusPercent(int percent) {

        if(percent < 0) {
            throw new IllegalArgumentException("Bonus must be >= 0");
        }
        bonusPercent += percent;
    }

    public float getRevealProgress() {
        if (!isRevealing()) {
            return 0f;
        }
        return revealTimer.getProgress();
    }

    public float getCurrentRadius() {
        if (!isRevealing()) {
            return 0f;
        }
        float progress = revealTimer.getProgress();
        return getRevealRadius() * (1f - Math.abs(2f * progress - 1f));
    }

}
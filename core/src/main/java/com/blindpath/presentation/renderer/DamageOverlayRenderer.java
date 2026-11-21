package com.blindpath.presentation.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Contour rouge affiché en plein écran quand le personnage prend un coup non fatal.
 * L'image (monsters/damage_overlay.png) est transparente au centre et s'efface
 * progressivement pendant DURATION secondes.
 */
public class DamageOverlayRenderer implements Disposable {

    public static final float DURATION = 0.5f;
    private static final float WIDTH = 1920f;
    private static final float HEIGHT = 1080f;

    private final Texture texture;
    private final FitViewport viewport = new FitViewport(WIDTH, HEIGHT);
    private float timeLeft = 0f;

    public DamageOverlayRenderer() {
        texture = new Texture("monsters/damage_overlay.png");
    }

    public void trigger() {
        timeLeft = DURATION;
    }

    public boolean isActive() {
        return timeLeft > 0;
    }

    public void update(float delta) {
        timeLeft = Math.max(0f, timeLeft - delta);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void render(SpriteBatch batch) {
        if (!isActive()) {
            return;
        }

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        // Opacité de 1 (au moment du coup) à 0 (fin) : l'image s'efface.
        batch.setColor(1f, 1f, 1f, timeLeft / DURATION);
        batch.draw(texture, 0, 0, WIDTH, HEIGHT);
        // La couleur du batch reste active : on la remet à blanc pour la suite.
        batch.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
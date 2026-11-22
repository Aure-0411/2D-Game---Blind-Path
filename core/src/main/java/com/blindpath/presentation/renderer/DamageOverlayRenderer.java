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

    /** Durée de l'effet, en secondes. */
    public static final float DURATION = 0.5f;
    private static final float WIDTH = 1920f;
    private static final float HEIGHT = 1080f;

    private final Texture texture;
    private final FitViewport viewport = new FitViewport(WIDTH, HEIGHT);
    private float timeLeft = 0f;

    /** Charge l'image du contour rouge. */
    public DamageOverlayRenderer() {
        texture = new Texture("monsters/damage_overlay.png");
    }

    /** Démarre l'effet, à pleine opacité. */
    public void trigger() {
        timeLeft = DURATION;
    }

    /** @return {@code true} tant que l'effet est visible */
    public boolean isActive() {
        return timeLeft > 0;
    }

    /**
     * Fait avancer l'effacement.
     *
     * @param delta temps écoulé depuis la dernière image, en secondes
     */
    public void update(float delta) {
        timeLeft = Math.max(0f, timeLeft - delta);
    }

    /**
     * Adapte l'effet à la nouvelle taille de la fenêtre.
     *
     * @param width largeur, en pixels
     * @param height hauteur, en pixels
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Dessine le contour, de plus en plus transparent jusqu'à la fin de l'effet.
     *
     * @param batch le batch de dessin, fermé (la méthode l'ouvre et le ferme)
     */
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

    /** Libère l'image. */
    @Override
    public void dispose() {
        texture.dispose();
    }
}
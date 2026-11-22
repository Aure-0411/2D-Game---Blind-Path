package com.blindpath.presentation.renderer;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Image de jumpscare affichée en plein écran quand un monstre attrape le héros. */
public class JumpscareRenderer implements Disposable {

    /** Durée minimale d'affichage, en secondes. */
    public static final float DURATION = 0.8f;
    /** Largeur de référence de l'image, en pixels. */
    public static final float WIDTH = 1920f;
    /** Hauteur de référence de l'image, en pixels. */
    public static final float HEIGHT = 1080f;

    private final Texture texture;
    private final FitViewport viewport = new FitViewport(WIDTH, HEIGHT);
    private float timeLeft = 0f;

    /**
     * Charge l'image {@code monsters/<nom>/<nom>_jumpscare.png}.
     *
     * @param name le nom du monstre
     */
    public JumpscareRenderer(String name) {
        texture = new Texture("monsters/" + name + "/" + name + "_jumpscare.png");
    }

    /** Démarre l'affichage. */
    public void trigger() {
        timeLeft = DURATION;
    }

    /** @return {@code true} tant que l'image doit être affichée */
    public boolean isActive() {
        return timeLeft > 0;
    }

    /**
     * Fait avancer le temps d'affichage.
     *
     * @param delta temps écoulé depuis la dernière image, en secondes
     */
    public void update(float delta) {
        timeLeft = Math.max(0f, timeLeft - delta);
    }

    /**
     * Adapte l'image à la nouvelle taille de la fenêtre.
     *
     * @param width largeur, en pixels
     * @param height hauteur, en pixels
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Dessine l'image si elle est active.
     *
     * @param batch le batch de dessin, fermé (la méthode l'ouvre et le ferme)
     */
    public void render(SpriteBatch batch) {
        render(batch, false);
    }

    /**
     * Dessine l'image si elle est active, ou tant que le son de mort n'est pas terminé.
     *
     * @param batch le batch de dessin, fermé (la méthode l'ouvre et le ferme)
     * @param holdForAudio {@code true} pour garder l'image pendant le son
     */
    public void render(SpriteBatch batch, boolean holdForAudio) {

        if (!isActive() && !holdForAudio) {
            return;
        }

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(texture, 0, 0, WIDTH, HEIGHT);
        batch.end();

    }

    /** Libère l'image. */
    @Override 
    public void dispose() {
        texture.dispose();
    }

}

package com.blindpath.presentation.renderer;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class JumpscareRenderer implements Disposable {

    public static final float DURATION = 0.8f;
    public static final float WIDTH = 1920f;
    public static final float HEIGHT = 1080f;

    private final Texture texture;
    private final FitViewport viewport = new FitViewport(WIDTH, HEIGHT);
    private float timeLeft = 0f;

    public JumpscareRenderer(String name) {
        texture = new Texture("monsters/" + name + "/" + name + "_jumpscare.png");
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
        render(batch, false);
    }

    /** Maintient l'image tant que le son de mort n'est pas terminé. */
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

    @Override 
    public void dispose() {
        texture.dispose();
    }

}

package com.blindpath.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blindpath.Main;

/** Joue l'animatique au lancement de l'application, puis ouvre le menu principal. */
public class IntroScreen implements Screen {
    private static final String DIRECTORY = "animations/intro/";

    private final Main main;
    private final Texture[] frames;
    private final float[] durations;
    private final SpriteBatch batch;
    private final FitViewport viewport;
    private int frameIndex;
    private float frameTime;
    private boolean disposed;

    public IntroScreen(Main main) {
        this.main = main;
        // Le GIF est conservé dans assets. Les PNG et leurs durées en sont extraits
        // à l'avance : aucun décodage GIF ni chargement de texture à chaque image.
        JsonValue animation = new JsonReader().parse(
                Gdx.files.internal(DIRECTORY + "animation.json")).get("frames");
        frames = new Texture[animation.size];
        durations = new float[animation.size];
        try {
            for (int i = 0; i < frames.length; i++) {
                JsonValue frame = animation.get(i);
                frames[i] = new Texture(DIRECTORY + frame.getString("file"));
                frames[i].setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                durations[i] = frame.getFloat("duration");
            }
        } catch (RuntimeException exception) {
            for (Texture frame : frames) {
                if (frame != null) frame.dispose();
            }
            throw exception;
        }
        batch = new SpriteBatch();
        // Conserve les proportions du GIF, avec des bandes noires si nécessaire.
        viewport = new FitViewport(frames[0].getWidth(), frames[0].getHeight());
    }

    @Override
    public void show() {
        frameIndex = 0;
        frameTime = 0f;
        Gdx.input.setInputProcessor(null);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        frameTime += delta;
        // Respecte la durée propre à chaque image, même si une frame du jeu est lente.
        while (frameIndex < frames.length && frameTime >= durations[frameIndex]) {
            frameTime -= durations[frameIndex];
            frameIndex++;
        }
        if (frameIndex == frames.length) {
            main.setScreen(new MainMenuScreen(main));
            dispose();
            return;
        }

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(frames[frameIndex], 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        batch.dispose();
        for (Texture frame : frames) frame.dispose();
    }
}

package com.blindpath.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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

    /**
     * Charge les images de l'animatique et leurs durées ({@code animations/intro/animation.json}).
     *
     * @param main l'application, pour ouvrir le menu à la fin
     */
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

    /** Repart de la première image de l'animatique. */
    @Override
    public void show() {
        frameIndex = 0;
        frameTime = 0f;
        Gdx.input.setInputProcessor(null);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Affiche l'animatique en respectant la durée de chaque image. À la fin, ou sur Échap,
     * ouvre le menu principal.
     *
     * @param delta temps écoulé depuis la dernière image, en secondes
     */
    @Override
    public void render(float delta) {
        frameTime += delta;
        // Respecte la durée propre à chaque image, même si une frame du jeu est lente.
        while (frameIndex < frames.length && frameTime >= durations[frameIndex]) {
            frameTime -= durations[frameIndex];
            frameIndex++;
        }
        // Échap termine l'introduction comme si l'animation était arrivée à sa fin.
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || frameIndex == frames.length) {
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

    /**
     * @param width largeur de la fenêtre, en pixels
     * @param height hauteur de la fenêtre, en pixels
     */
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

    /** Libère les images (une seule fois, même si la méthode est rappelée). */
    @Override
    public void dispose() {
        if (disposed) return;
        disposed = true;
        batch.dispose();
        for (Texture frame : frames) frame.dispose();
    }
}

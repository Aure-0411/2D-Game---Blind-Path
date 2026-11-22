package com.blindpath.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blindpath.Main;

/** Message de fin de démo et compteur de morts, suivi d'un retour automatique à l'accueil. */
public class EndScreen implements Screen {
    private static final float DISPLAY_DURATION = 6f;

    private final Main main;
    private final Stage stage;
    private final BitmapFont font;
    private final BitmapFont subFont;
    private final int deathCount;
    private float elapsed;

    /**
     * Prépare le message de fin sans mort enregistrée.
     *
     * @param main l'application, pour revenir au menu
     */
    public EndScreen(Main main) {
        this(main, 0);
    }

    /**
     * Prépare le message de fin avec le nombre de morts de la partie.
     *
     * @param main l'application, pour revenir au menu
     * @param deathCount le nombre de morts survenues pendant la partie
     * @throws IllegalArgumentException si {@code deathCount} est négatif
     */
    public EndScreen(Main main, int deathCount) {
        if (deathCount < 0) {
            throw new IllegalArgumentException("Le nombre de morts ne peut pas être négatif.");
        }
        this.main = main;
        this.deathCount = deathCount;
        // Une taille virtuelle fixe garde le message lisible au redimensionnement.
        stage = new Stage(new FitViewport(960, 540));
        font = new BitmapFont();
        font.getData().setScale(3f);

        subFont = new BitmapFont();
        subFont.getData().setScale(2f);

        Label message = new Label("À bientôt pour la version complète ;)",
                new Label.LabelStyle(font, Color.WHITE));
        message.setAlignment(Align.center);
        message.setWrap(true);

        String deathText = deathCount <= 1
                ? "Nombre de morts : " + deathCount
                : "Nombre de morts : " + deathCount;
        Label deathLabel = new Label(deathText, new Label.LabelStyle(subFont, Color.LIGHT_GRAY));
        deathLabel.setAlignment(Align.center);

        Table table = new Table();
        table.setFillParent(true);
        table.add(message).growX().pad(40).row();
        table.add(deathLabel).padTop(10);
        stage.addActor(table);
    }

    /** @return le nombre de morts affiché sur cet écran */
    public int getDeathCount() {
        return deathCount;
    }

    /** Remet le compte à rebours à zéro et donne le clavier à l'écran. */
    @Override
    public void show() {
        elapsed = 0f;
        Gdx.input.setInputProcessor(stage);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    /**
     * Affiche le message, puis revient au menu principal au bout de 6 secondes.
     *
     * @param delta temps écoulé depuis la dernière image, en secondes
     */
    @Override
    public void render(float delta) {
        // Accumuler delta laisse la fenêtre réactive, contrairement à un sleep.
        elapsed += delta;
        if (elapsed >= DISPLAY_DURATION) {
            main.setScreen(new MainMenuScreen(main));
            dispose();
            return; // Ne plus dessiner une scène dont les ressources sont libérées.
        }

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    /**
     * @param width largeur de la fenêtre, en pixels
     * @param height hauteur de la fenêtre, en pixels
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    /** Rend le clavier quand l'écran est quitté. */
    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    /** Libère la scène et les polices. */
    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        subFont.dispose();
    }
}
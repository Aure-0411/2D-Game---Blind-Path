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

/** Message de fin de démo, suivi d'un retour automatique à l'accueil. */
public class EndScreen implements Screen {
    private static final float DISPLAY_DURATION = 6f;

    private final Main main;
    private final Stage stage;
    private final BitmapFont font;
    private float elapsed;

    public EndScreen(Main main) {
        this.main = main;
        // Une taille virtuelle fixe garde le message lisible au redimensionnement.
        stage = new Stage(new FitViewport(960, 540));
        font = new BitmapFont();
        font.getData().setScale(3f);

        Label message = new Label("À bientôt pour la version complète ;)",
                new Label.LabelStyle(font, Color.WHITE));
        message.setAlignment(Align.center);
        message.setWrap(true);

        Table table = new Table();
        table.setFillParent(true);
        table.add(message).growX().pad(40);
        stage.addActor(table);
    }

    @Override
    public void show() {
        elapsed = 0f;
        Gdx.input.setInputProcessor(stage);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

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

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}

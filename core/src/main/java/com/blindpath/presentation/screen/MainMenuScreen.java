package com.blindpath.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.blindpath.Main;
import com.blindpath.domain.character.Gnome;

/**
 * Menu principal : un bouton « Start » qui lance une partie avec le gnome,
 * et un bouton « Quit » qui ferme le jeu.
 */
public class MainMenuScreen implements Screen {

    private Stage stage;
    private Skin skin;

    /**
     * Construit le menu et ses boutons.
     *
     * @param main l'application, pour changer d'écran
     * @throws IllegalArgumentException si l'application est {@code null}
     */
    public MainMenuScreen(Main main) {
        if (main == null) {
            throw new IllegalArgumentException("L'application principale doit être renseignée.");
        }
        
        this.stage = new Stage(new ScreenViewport());

        this.skin = new Skin();
        skin.add("default-font", new BitmapFont());

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.GRAY;
        buttonStyle.font = skin.getFont("default-font");
        buttonStyle.font.getData().setScale(5f);
        skin.add("default", buttonStyle);

        Table table = new Table();
        table.setFillParent(true);

        TextButton startButton = new TextButton("Start", skin);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.setScreen(new GameScreen<>(main, new Gnome()));
                MainMenuScreen.this.dispose();
            }
        });

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        table.add(startButton).width(600).height(180).padBottom(40);
        table.row();
        table.add(quitButton).width(600).height(180);

        stage.addActor(table);

    }

    /** Donne le clavier et la souris au menu. */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

    }

    /** @param delta temps écoulé depuis la dernière image, en secondes */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
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
    public void pause() {
        // Aucune logique à suspendre pour ce premier affichage.
    }

    @Override
    public void resume() {
        // Aucune logique à reprendre pour le moment.
    }

    /** Rend le clavier quand le menu est quitté. */
    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    /** Libère la scène et le skin. */
    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }

        if (skin != null) {
            skin.dispose();
            skin = null;
        }
    }
}

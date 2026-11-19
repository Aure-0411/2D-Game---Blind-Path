package com.blindpath.presentation.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Hud {

    private final Stage stage;
    private final ProgressBar cooldownBar;
    private final Texture whiteTexture;
    private final Texture heartFullTexture;
    private final Texture heartHalfTexture;
    private final Texture heartEmptyTexture;
    private final TextureRegionDrawable heartFullDrawable;
    private final TextureRegionDrawable heartHalfDrawable;
    private final TextureRegionDrawable heartEmptyDrawable;

    private final Image[] hearts;

    public Hud() {
        this.stage = new Stage(new ScreenViewport());

        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE);
        pixel.fill();

        this.whiteTexture = new Texture(pixel);
        pixel.dispose();

        TextureRegionDrawable white = new TextureRegionDrawable(
                new TextureRegion(whiteTexture)
        );

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = white.tint(Color.valueOf("#5b5b5b"));
        style.background.setMinWidth(30);

        style.knobBefore = white.tint(Color.valueOf("#e68208"));
        style.knobBefore.setMinWidth(20);

        this.cooldownBar = new ProgressBar(0f, 1f, 0.01f, true, style);
        cooldownBar.setValue(1f);

        Table root = new Table();
        root.setFillParent(true);
        root.top().left();
        root.pad(30);
        root.padTop(250);

        // Le fond de ce conteneur reste visible autour de la jauge : bordure de 3 px.
        Table barFrame = new Table();
        barFrame.setBackground(white.tint(Color.valueOf("#F2F2F2")));
        barFrame.pad(3);
        barFrame.add(cooldownBar).width(20).height(120);

        root.add(barFrame);
        stage.addActor(root);
        root.row();

        heartFullTexture = new Texture("ui/hearts/heart_full.png");
        heartHalfTexture = new Texture("ui/hearts/heart_half.png");
        heartEmptyTexture = new Texture("ui/hearts/heart_empty.png");
        heartFullDrawable = new TextureRegionDrawable(new TextureRegion(heartFullTexture));
        heartHalfDrawable = new TextureRegionDrawable(new TextureRegion(heartHalfTexture));
        heartEmptyDrawable = new TextureRegionDrawable(new TextureRegion(heartEmptyTexture));
        hearts = new Image[3];

        for (int i = 0; i < hearts.length; i++) {
            hearts[i] = new Image(heartFullTexture);
            root.add(hearts[i])
                    .size(48)
                    .padTop(i == 0 ? 16 : 8);
            root.row();
        }

    }

    public void update(float cooldownProgress, int life) {
        cooldownBar.setValue(cooldownProgress);
        for (int i = 0; i < hearts.length; i++) {

            int heartLife = Math.max(0, Math.min(2, life - 2 * (hearts.length - 1 - i)));
            if (heartLife == 2) {
                hearts[i].setDrawable(heartFullDrawable);
            }
            if (heartLife == 1) {
                hearts[i].setDrawable(heartHalfDrawable);
            }
            if (heartLife == 0) {
                hearts[i].setDrawable(heartEmptyDrawable);
            }
        }
    }

    public void render(float delta) {
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        whiteTexture.dispose();
        heartFullTexture.dispose();
        heartHalfTexture.dispose();
        heartEmptyTexture.dispose();
    }

}

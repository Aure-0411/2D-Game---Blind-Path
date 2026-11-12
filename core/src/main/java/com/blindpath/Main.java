package com.blindpath;

import com.badlogic.gdx.Game;
import com.blindpath.presentation.screen.GameScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }

    // @Override
    // public void render() {
    //     ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
    //     batch.begin();
    //     batch.draw(image, 140, 210);
    //     batch.end();
    // }
    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        super.dispose();
    }
}

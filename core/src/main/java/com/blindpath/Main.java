package com.blindpath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.blindpath.presentation.screen.IntroScreen;
import com.blindpath.presentation.screen.MainMenuScreen;
import com.blindpath.presentation.audio.MenuAudio;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends Game {
    private MenuAudio menuAudio;

    @Override
    public void create() {
        menuAudio = new MenuAudio();
        setScreen(new IntroScreen(this));
    }

    @Override
    public void setScreen(Screen next) {
        boolean menuActive = next instanceof IntroScreen || next instanceof MainMenuScreen;
        if (menuAudio != null && !menuActive) menuAudio.setActive(false);
        super.setScreen(next);
        if (menuAudio != null && menuActive) menuAudio.setActive(true);
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
        if (menuAudio != null) {
            menuAudio.dispose();
            menuAudio = null;
        }
    }
}

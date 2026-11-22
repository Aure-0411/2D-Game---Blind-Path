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

    /** Crée la musique du menu et affiche l'écran d'introduction au lancement du jeu. */
    @Override
    public void create() {
        menuAudio = new MenuAudio();
        setScreen(new IntroScreen(this));
    }

    /**
     * Change d'écran et coupe ou relance la musique du menu selon l'écran affiché.
     * La musique ne joue que sur l'écran d'introduction et le menu principal.
     *
     * @param next le nouvel écran à afficher
     */
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
    /** Libère l'écran courant puis la musique du menu à la fermeture du jeu. */
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

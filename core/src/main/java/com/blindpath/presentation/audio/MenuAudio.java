package com.blindpath.presentation.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

/** Une même musique pour l'intro et le menu, conservée entre les deux écrans. */
public final class MenuAudio implements Disposable {
    private static final float VOLUME = 0.4f;
    private final Music music;

    public MenuAudio() {
        this(Gdx.audio.newMusic(Gdx.files.internal("audio/MUSIQUE_START.mp3")));
    }

    MenuAudio(Music music) {
        this.music = music;
        music.setLooping(true);
        music.setVolume(VOLUME);
    }

    public void setActive(boolean active) {
        if (active) {
            if (!music.isPlaying()) music.play();
        } else {
            music.stop();
        }
    }

    @Override
    public void dispose() {
        music.stop();
        music.dispose();
    }
}

package com.blindpath.presentation.audio;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.audio.Music;

class MenuAudioTest {
    private static class RecordingMusic {
        boolean playing;
        boolean looping;
        boolean disposed;
        int starts;
        float volume;
        final Music music = (Music) Proxy.newProxyInstance(Music.class.getClassLoader(),
                new Class<?>[] {Music.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "setLooping" -> looping = (boolean) args[0];
                        case "setVolume" -> volume = (float) args[0];
                        case "isPlaying" -> { return playing; }
                        case "play" -> { playing = true; starts++; }
                        case "stop" -> playing = false;
                        case "dispose" -> disposed = true;
                        default -> throw new UnsupportedOperationException(method.getName());
                    }
                    return null;
                });
    }

    @Test
    void introToMenuKeepsSameLoopThenGameplayStopsIt() {
        RecordingMusic recording = new RecordingMusic();
        MenuAudio audio = new MenuAudio(recording.music);
        assertTrue(recording.looping);
        assertEquals(0.4f, recording.volume);
        audio.setActive(true); // Intro.
        audio.setActive(true); // Menu : continue au même endroit.
        assertEquals(1, recording.starts);
        audio.setActive(false); // Partie.
        assertFalse(recording.playing);
        audio.setActive(true); // Retour au menu.
        assertEquals(2, recording.starts);
        audio.dispose();
        assertFalse(recording.playing);
        assertTrue(recording.disposed);
    }
}

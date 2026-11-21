package com.blindpath.presentation.audio;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import com.badlogic.gdx.audio.Sound;
import com.blindpath.domain.character.Character;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.*;
import com.blindpath.domain.monster.*;
import com.blindpath.domain.pathfinding.*;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;

class MonsterAudioTest {
    @Test
    void audioAcceptsGenericCharacterWithoutCastingToGnome() {
        RecordingSound recording = new RecordingSound();
        MonsterAudio audio = new MonsterAudio(recording.sound);
        Character hero = new Character(3f, 10f, 10f, 0.8f, 0.8f) {};
        audio.update(monster(MonsterType.JUMPER), hero, true);
        assertEquals(1, recording.loops);
        audio.dispose();
    }

    @ParameterizedTest
    @CsvSource({
        "JUMPER, audio/JUMPER_JUMPSCARE.mp3",
        "RUNAWAY, audio/RUNAWAY_SCREAMER.mp3",
        "SCREAMER, audio/SCREAMER_JUMPSCARE.wav",
        "CHAMELEON, audio/CHAMELEON_JUMPSCARE.mp3",
        "INVERTER, audio/INVERTER_JUMPSCARE.mp3"
    })
    void jumpscareMatchesMonster(MonsterType type, String expected) {
        String path = MonsterAudio.jumpscarePath(monster(type));
        assertEquals(expected, path);
        if (path != null) {
            Path assets = Files.isDirectory(Path.of("assets")) ? Path.of("assets") : Path.of("../assets");
            assertTrue(Files.isRegularFile(assets.resolve(path)), "Jumpscare manquant : " + path);
        }
    }

    private static LabyrinthGrid grid() {
        return new LabyrinthGrid(new Labyrinth(30, 30,
                new Entrance(0, 0, OpeningOrientation.VERTICAL),
                new Exit(29, 0, OpeningOrientation.VERTICAL), List.of()));
    }

    private static Monster monster(MonsterType type) {
        Monster monster = type.create(grid(), new GridPosition(0, 0),
                (grid, start, target) -> List.of(new GridPosition(start.getColumn() + 1, start.getRow())));
        monster.setTarget(new GridPosition(20, 20));
        return monster;
    }

    private static class RecordingSound {
        int plays;
        int loops;
        int stops;
        int disposals;
        float volume;
        final Sound sound = (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(),
                new Class<?>[] {Sound.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "play" -> { plays++; volume = (float) args[0]; return 42L; }
                        case "loop" -> { loops++; volume = (float) args[0]; return 42L; }
                        case "setVolume" -> volume = (float) args[1];
                        case "stop" -> stops++;
                        case "dispose" -> disposals++;
                        default -> throw new UnsupportedOperationException(method.getName());
                    }
                    return null;
                });
    }

    @Test
    void screamerPlaysOncePerCryEvenAfterLevelConsumedEventOrDuringActiveAnimation() {
        Screamer screamer = new Screamer(grid(), (grid, current, goal) -> current,
                new GridPosition(0, 0), new java.util.Random(0));
        RecordingSound movement = new RecordingSound();
        RecordingSound scream = new RecordingSound();
        MonsterAudio audio = new MonsterAudio(Map.of(Screamer.class, movement.sound), scream.sound);
        Gnome hero = new Gnome();
        SnapEvent snap = new SnapEvent(10, 10, SnapSource.HERO, 0);
        audio.update(screamer, hero, true);
        int threshold = screamer.getScreamThreshold();
        for (int i = 0; i < threshold - 1; i++) screamer.onSnap(snap);
        audio.update(screamer, hero, true);
        assertEquals(0, scream.plays);
        screamer.onSnap(snap);
        assertTrue(screamer.consumeScream(), "Level garde son événement pour étourdir le gnome");
        audio.update(screamer, hero, true);
        assertEquals(1, scream.plays);
        assertEquals(1f, scream.volume);
        audio.update(screamer, hero, true);
        assertEquals(1, scream.plays, "pas de répétition à chaque image");
        threshold = screamer.getScreamThreshold();
        for (int i = 0; i < threshold; i++) screamer.onSnap(snap);
        assertTrue(screamer.isScreaming());
        audio.update(screamer, hero, true);
        assertEquals(2, scream.plays, "un nouveau cri est audible même si l'animation précédente était active");
        assertEquals(1, scream.stops, "la nouvelle lecture ne se superpose pas à la précédente");
        audio.update(screamer, hero, false);
        audio.update(screamer, hero, true);
        assertEquals(2, scream.plays, "la reprise ne rejoue pas le dernier cri");
        audio.dispose();
        assertEquals(1, scream.disposals);
        assertEquals(1, movement.disposals);
    }

    @ParameterizedTest
    @EnumSource(MonsterType.class)
    void eachMonsterLoopsOnceRegardlessOfMovementAndOnlyVolumeChanges(MonsterType type) {
        Monster monster = monster(type);
        RecordingSound recording = new RecordingSound();
        MonsterAudio audio = new MonsterAudio(Map.of(monster.getClass(), recording.sound));
        Gnome hero = new Gnome();
        hero.setPosition(25, 25);
        audio.update(monster, hero, false);
        assertEquals(0, recording.loops);
        audio.update(monster, hero, true);
        assertEquals(1, recording.loops);
        assertEquals(0.05f, recording.volume);
        monster.onTick(60);
        audio.update(monster, hero, true);
        hero.setPosition(monster.getPosition().getColumn(), monster.getPosition().getRow());
        audio.update(monster, hero, true);
        assertEquals(0.8f, recording.volume);
        monster.immobilize(10f);
        for (int i = 0; i < 120; i++) audio.update(monster, hero, true);
        assertEquals(1, recording.loops, "la même boucle continue pendant la marche et les arrêts");
        assertEquals(0, recording.stops);
        audio.dispose();
        assertEquals(1, recording.stops);
        assertEquals(1, recording.disposals);
    }

    @ParameterizedTest
    @CsvSource({"20, 0.05", "10.001, 0.05", "10, 0.3", "7.001, 0.3",
                "7, 0.5", "5.001, 0.5", "5, 0.5", "4.999, 0.8", "0, 0.8"})
    void volumeMatchesCurrentThresholds(float distance, float expected) {
        assertEquals(expected, MonsterAudio.volumeForDistance(distance), 0.0001f);
    }

    @Test
    void pauseStopsLoopAndResumeStartsOnlyOneNewLoop() {
        RecordingSound recording = new RecordingSound();
        MonsterAudio audio = new MonsterAudio(recording.sound);
        Monster monster = monster(MonsterType.JUMPER);
        Gnome hero = new Gnome();
        audio.update(monster, hero, true);
        audio.update(monster, hero, false);
        audio.update(monster, hero, false);
        assertEquals(1, recording.stops);
        audio.update(monster, hero, true);
        audio.update(monster, hero, true);
        assertEquals(2, recording.loops);
        audio.dispose();
    }

    @Test
    void changingMonsterStopsPreviousLoopAndSelectsNewSound() {
        RecordingSound jumper = new RecordingSound();
        RecordingSound screamer = new RecordingSound();
        MonsterAudio audio = new MonsterAudio(Map.of(Jumper.class, jumper.sound, Screamer.class, screamer.sound));
        Gnome hero = new Gnome();
        audio.update(monster(MonsterType.JUMPER), hero, true);
        audio.update(monster(MonsterType.SCREAMER), hero, true);
        assertEquals(1, jumper.loops);
        assertEquals(1, jumper.stops);
        assertEquals(1, screamer.loops);
        audio.dispose();
        assertEquals(1, jumper.disposals);
        assertEquals(1, screamer.disposals);
    }

    @Test
    void everyConfiguredMonsterHasAnExistingSupportedAudioFile() {
        var paths = MonsterAudio.movementPaths();
        assertEquals(5, paths.size());
        Path assets = Files.isDirectory(Path.of("assets")) ? Path.of("assets") : Path.of("../assets");
        for (String path : paths.values()) {
            assertTrue(path.endsWith(".wav") || path.endsWith(".mp3"));
            assertTrue(Files.isRegularFile(assets.resolve(path)), "Son manquant : " + path);
        }
    }
}

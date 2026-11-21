package com.blindpath.presentation.audio;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.monster.Chameleon;
import com.blindpath.domain.monster.Inverter;
import com.blindpath.domain.monster.Jumper;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.monster.Runaway;
import com.blindpath.domain.monster.Screamer;

/** Sons des monstres ; aucune dépendance audio n'est ajoutée au domaine. */
public final class MonsterAudio implements Disposable {
    private static final float SCREAM_VOLUME = 1f;
    private final Map<Class<? extends Monster>, Sound> movementSounds;
    private final Sound screamerScream;
    private Sound currentMovement;
    private Monster trackedMonster;
    private long movementId = -1;
    private long screamId = -1;
    private long previousScreamSequence;

    public MonsterAudio() {
        movementSounds = loadMovementSounds();
        try {
            screamerScream = Gdx.audio.newSound(Gdx.files.internal("audio/SCREAMER_SCREAM.mp3"));
        } catch (RuntimeException exception) {
            movementSounds.values().forEach(Sound::dispose);
            throw exception;
        }
    }

    // Permet de vérifier les déclenchements sans ouvrir de périphérique audio.
    MonsterAudio(Sound jumperMovement) {
        this(Map.of(Jumper.class, jumperMovement));
    }

    MonsterAudio(Map<Class<? extends Monster>, Sound> movementSounds) {
        this(movementSounds, null);
    }

    MonsterAudio(Map<Class<? extends Monster>, Sound> movementSounds, Sound screamerScream) {
        this.movementSounds = Map.copyOf(movementSounds);
        this.screamerScream = screamerScream;
    }

    static Map<Class<? extends Monster>, String> movementPaths() {
        return Map.of(
                Jumper.class, "audio/JUMPER_MOVE.mp3",
                Runaway.class, "audio/RUNAWAY_MOVE.mp3",
                Screamer.class, "audio/SCREAMER_MOVE.mp3",
                Chameleon.class, "audio/CHAMELEON_MOVE.mp3",
                Inverter.class, "audio/INVERTER_MOVE.mp3");
    }

    /** Chaque monstre utilise son propre jumpscare. */
    public static String jumpscarePath(Monster monster) {
        if (monster instanceof Jumper) return "audio/JUMPER_JUMPSCARE.mp3";
        if (monster instanceof Runaway) return "audio/RUNAWAY_SCREAMER.mp3";
        if (monster instanceof Screamer) return "audio/SCREAMER_JUMPSCARE.wav";
        if (monster instanceof Chameleon) return "audio/CHAMELEON_JUMPSCARE.mp3";
        if (monster instanceof Inverter) return "audio/INVERTER_JUMPSCARE.mp3";
        return null;
    }

    private static Map<Class<? extends Monster>, Sound> loadMovementSounds() {
        Map<Class<? extends Monster>, Sound> sounds = new LinkedHashMap<>();
        try {
            movementPaths().forEach((type, path) -> sounds.put(type,
                    Gdx.audio.newSound(Gdx.files.internal(path))));
            return sounds;
        } catch (RuntimeException exception) {
            sounds.values().forEach(Sound::dispose);
            throw exception;
        }
    }

    private Sound soundFor(Monster monster) {
        for (var entry : movementSounds.entrySet()) {
            if (entry.getKey().isInstance(monster)) return entry.getValue();
        }
        return null;
    }

    static float volumeForDistance(float distance) {
        if (distance > 10f) return 0.05f;
        if (distance > 7f) return 0.3f;
        if (distance >= 5f) return 0.5f;
        return 0.8f;
    }

    public void update(Monster monster, Character hero, boolean enabled) {
        if (trackedMonster != monster) {
            stop();
            trackedMonster = monster;
            currentMovement = soundFor(monster);
            previousScreamSequence = monster instanceof Screamer screamer ? screamer.getScreamSequence() : 0;
        }
        long screamSequence = monster instanceof Screamer screamer ? screamer.getScreamSequence() : 0;
        if (enabled && screamerScream != null && screamSequence != previousScreamSequence) {
            stopScream();
            screamId = screamerScream.play(SCREAM_VOLUME);
        }
        previousScreamSequence = screamSequence;
        if (!enabled) stopScream();
        if (!enabled || currentMovement == null) {
            stopCurrentSound();
            return;
        }

        Bounds monsterBounds = monster.getBounds();
        Bounds heroBounds = hero.getBounds();
        float dx = monsterBounds.getCenterX() - heroBounds.getCenterX();
        float dy = monsterBounds.getCenterY() - heroBounds.getCenterY();
        float volume = volumeForDistance((float) Math.sqrt(dx * dx + dy * dy));

        // Une seule boucle continue ; les déplacements ne redémarrent jamais le fichier.
        if (movementId == -1) {
            movementId = currentMovement.loop(volume);
        } else {
            currentMovement.setVolume(movementId, volume);
        }
    }

    private void stopCurrentSound() {
        if (movementId != -1 && currentMovement != null) currentMovement.stop(movementId);
        movementId = -1;
    }

    public void stop() {
        stopCurrentSound();
        stopScream();
        trackedMonster = null;
        currentMovement = null;
        previousScreamSequence = 0;
    }

    private void stopScream() {
        if (screamerScream != null && screamId != -1) screamerScream.stop(screamId);
        screamId = -1;
    }

    @Override
    public void dispose() {
        stop();
        Set<Sound> unique = Collections.newSetFromMap(new IdentityHashMap<>());
        unique.addAll(movementSounds.values());
        if (screamerScream != null) unique.add(screamerScream);
        unique.forEach(Sound::dispose);
    }
}

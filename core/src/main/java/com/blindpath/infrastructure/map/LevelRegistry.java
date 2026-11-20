package com.blindpath.infrastructure.map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.blindpath.domain.game.LevelCatalog;
import com.blindpath.domain.game.LevelConfig;
import com.blindpath.domain.monster.MonsterType;

public class LevelRegistry implements LevelCatalog {

    private final List<LevelConfig> levels = new ArrayList<>();
    private final int mediumStart;
    private final int hardStart;
    private final int extremeStart;

    public LevelRegistry() {
        this(readIndexFile());
    }

    public LevelRegistry(String indexContent) {
        if (indexContent == null) {
            throw new IllegalArgumentException("Le contenu de l'index doit être renseigné.");
        }

        List<String> easy = new ArrayList<>();
        List<String> medium = new ArrayList<>();
        List<String> hard = new ArrayList<>();
        List<String> extreme = new ArrayList<>();

        for (String line : indexContent.split("\\r?\\n")) {
            String path = line.trim();
            if (path.isEmpty()) continue;

            if (path.startsWith("maps/1_easy/")) easy.add(path);
            else if (path.startsWith("maps/2_medium/")) medium.add(path);
            else if (path.startsWith("maps/3_hard/")) hard.add(path);
            else if (path.startsWith("maps/4_extreme/")) extreme.add(path);
        }

        this.mediumStart = easy.size() + 1;
        this.hardStart = mediumStart + medium.size();
        this.extremeStart = hardStart + hard.size();

        int jumperLimit = (int) Math.ceil(easy.size() / 2.0);
        for (int i = 0; i < easy.size(); i++) {
            MonsterType type = (i < jumperLimit) ? MonsterType.RUNAWAY : MonsterType.RUNAWAY;
            levels.add(new LevelConfig(easy.get(i), type));
        }
        for (String path : medium) levels.add(new LevelConfig(path, MonsterType.SCREAMER));
        for (String path : hard) levels.add(new LevelConfig(path, MonsterType.CHAMELEON));
        for (String path : extreme) levels.add(new LevelConfig(path, MonsterType.INVERTER));
    }

    private static String readIndexFile() {
        FileHandle indexFile = Gdx.files.internal("maps/index.txt");
        if (!indexFile.exists()) {
            throw new IllegalStateException("Fichier maps/index.txt introuvable. Lance la tâche Gradle generateMapIndex.");
        }
        return indexFile.readString("UTF-8");
    }

    @Override
    public LevelConfig getLevel(int levelNumber) {
        int index = levelNumber - 1;
        if (index < 0 || index >= levels.size()) {
            throw new IllegalArgumentException("Niveau inconnu : " + levelNumber + " (total : " + levels.size() + ")");
        }
        return levels.get(index);
    }

    @Override
    public int totalLevels() {
        return levels.size();
    }

    @Override
    public int tierStartLevel(int levelNumber) {
        if (levelNumber < 1 || levelNumber > levels.size()) {
            throw new IllegalArgumentException("Niveau inconnu : " + levelNumber);
        }
        if (levelNumber < mediumStart) return 1;
        if (levelNumber < hardStart) return mediumStart;
        if (levelNumber < extremeStart) return hardStart;
        return extremeStart;
    }
}
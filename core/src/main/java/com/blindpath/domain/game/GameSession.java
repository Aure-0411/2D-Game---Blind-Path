package com.blindpath.domain.game;

import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.chest.Chest;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class GameSession {

    private final LevelFactory factory;
    private final Gnome gnome;
    private final Map<Integer, List<Chest>> chestsByLevel = new HashMap<>();
    private int currentLevelNumber = 1;
    private int retryCount;
    private Level level;
    private boolean won;

    public GameSession(LevelFactory factory, Gnome gnome) {
        if (factory == null || gnome == null) {
            throw new IllegalArgumentException("La factory et le gnome doivent être renseignés.");
        }
        this.factory = factory;
        this.gnome = gnome;
    }

    public void startLevel(int number) {
        currentLevelNumber = number;
        won = false;
        List<Chest> chests = chestsByLevel.computeIfAbsent(number, factory::createChests);
        level = factory.create(number, gnome, chests);
    }

    public void nextLevel() {
        if (currentLevelNumber >= factory.totalLevels()) {
            won = true;
            return;
        }
        startLevel(currentLevelNumber + 1);
    }

    public void retry() {
        retryCount++;
        gnome.resetForRetry();
        startLevel(currentLevelNumber);
    }

    public Level getLevel() { return level; }
    public int getCurrentLevelNumber() { return currentLevelNumber; }
    public int getRetryCount() { return retryCount; }
    public boolean isWon() { return won; }

    // pause, resume, update, checkpoint... inchangés
}
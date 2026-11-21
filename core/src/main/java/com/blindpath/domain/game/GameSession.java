package com.blindpath.domain.game;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.chest.Chest;

public class GameSession<T extends Character> {

    private final LevelFactory factory;
    private final T hero;
    private final Map<Integer, List<Chest>> chestsByLevel = new HashMap<>();
    private int currentLevelNumber = 1;
    private int retryCount;
    private Level<T> level;
    private boolean won;

    public GameSession(LevelFactory factory, T hero) {
        if (factory == null || hero == null) {
            throw new IllegalArgumentException("La factory et le joueur doivent être renseignés.");
        }
        this.factory = factory;
        this.hero = hero;
    }

    public void startLevel(int number) {
        currentLevelNumber = number;
        won = false;
        List<Chest> chests = chestsByLevel.computeIfAbsent(number, factory::createChests);
        level = factory.create(number, hero, chests);
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
        hero.resetForRetry();
        int checkpointLevel = factory.tierStartFor(currentLevelNumber);
        startLevel(checkpointLevel);
    }


    public T getHero() { return hero; }
    public Level<T> getLevel() { return level; }
    public int getCurrentLevelNumber() { return currentLevelNumber; }
    public int getRetryCount() { return retryCount; }
    public boolean isWon() { return won; }
}
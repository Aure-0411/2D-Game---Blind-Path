package com.blindpath.domain.game;

public interface LevelCatalog {
    LevelConfig getLevel(int levelNumber);
    int totalLevels();
    int tierStartLevel(int levelNumber);
}
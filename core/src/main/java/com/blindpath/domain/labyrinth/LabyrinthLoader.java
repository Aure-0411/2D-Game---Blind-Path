package com.blindpath.domain.labyrinth;

import java.util.List;

import com.blindpath.domain.monster.MonsterType;
import com.blindpath.domain.pathfinding.GridPosition;

public interface LabyrinthLoader {

    record ChestSpawn(float x, float y, Border orientation) {}

    Labyrinth load(String path);

    default List<ChestSpawn> loadChestSpawns(String path) {
        return List.of(new ChestSpawn(1f, 1f, Border.BOTTOM));
    }

    default MonsterType loadMonsterType(String path) {
        return null; // null = utilise le MonsterType du palier par défaut
    }

    default GridPosition loadMonsterSpawn(String path) {
        return new GridPosition(5, 5);
    }
}
package com.blindpath.domain.game;

import com.blindpath.domain.monster.MonsterType;

public record LevelConfig(
        String mapPath,
        MonsterType monsterType) {
}
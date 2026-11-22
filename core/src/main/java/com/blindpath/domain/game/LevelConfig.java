package com.blindpath.domain.game;

import com.blindpath.domain.monster.MonsterType;

/**
 * Configuration d'un niveau.
 *
 * @param mapPath chemin de la carte Tiled (.tmx)
 * @param monsterType le monstre par défaut du niveau
 */
public record LevelConfig(
        String mapPath,
        MonsterType monsterType) {
}
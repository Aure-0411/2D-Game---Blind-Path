package com.blindpath.infrastructure.map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.blindpath.domain.monster.MonsterType;

class LevelRegistryTest {

    @Test
    void easyTier_splitsFiftyFiftyBetweenJumperAndRunaway_withOddCountRoundedUp() {
        String index = String.join("\n",
                "maps/1_easy/01.tmx",
                "maps/1_easy/02.tmx",
                "maps/1_easy/03.tmx", // 3 cartes -> ceil(3/2) = 2 Jumper, 1 Runaway
                "maps/2_medium/04.tmx",
                "maps/3_hard/05.tmx",
                "maps/4_extreme/06.tmx");

        LevelRegistry registry = new LevelRegistry(index);

        assertAll(
                () -> assertEquals(6, registry.totalLevels()),
                () -> assertEquals(MonsterType.JUMPER, registry.getLevel(1).monsterType()),
                () -> assertEquals(MonsterType.JUMPER, registry.getLevel(2).monsterType()),
                () -> assertEquals(MonsterType.RUNAWAY, registry.getLevel(3).monsterType()),
                () -> assertEquals(MonsterType.SCREAMER, registry.getLevel(4).monsterType()),
                () -> assertEquals(MonsterType.CHAMELEON, registry.getLevel(5).monsterType()),
                () -> assertEquals(MonsterType.INVERTER, registry.getLevel(6).monsterType()));
    }

    @Test
    void outOfBoundsLevel_throwsException() {
        LevelRegistry registry = new LevelRegistry("maps/1_easy/01.tmx");

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> registry.getLevel(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> registry.getLevel(2)));
    }
}
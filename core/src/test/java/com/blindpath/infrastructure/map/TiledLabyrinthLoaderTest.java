package com.blindpath.infrastructure.map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.blindpath.domain.inventory.ItemType;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.LabyrinthLoader.ChestSpawn;
import com.blindpath.domain.monster.MonsterType;
import com.blindpath.domain.pathfinding.GridPosition;

class TiledLabyrinthLoaderTest {

    private final TiledLabyrinthLoader loader = new TiledLabyrinthLoader();

    @Test
    void loadChestSpawns_readsGridCoordinatesAndOrientation() {
        TiledMap map = new TiledMap();
        MapLayer chestLayer = new MapLayer();
        chestLayer.setName("Coffre");

        // 1x2 cases (250x500 px) orienté RIGHT en (1, 14) -> (250, 3500)
        RectangleMapObject chestObj = new RectangleMapObject(250f, 3500f, 250f, 500f);
        chestObj.getProperties().put("Orientation", "RIGHT");
        chestLayer.getObjects().add(chestObj);
        map.getLayers().add(chestLayer);

        List<ChestSpawn> spawns = loader.loadChestSpawns(map);

        assertAll(
                () -> assertEquals(1, spawns.size()),
                () -> assertEquals(1f, spawns.get(0).x()),
                () -> assertEquals(14f, spawns.get(0).y()),
                () -> assertEquals(Border.RIGHT, spawns.get(0).orientation()));
        map.dispose();
    }

    @Test
    void loadChestSpawns_rejectsInvalidDimensionsForOrientation() {
        TiledMap map = new TiledMap();
        MapLayer chestLayer = new MapLayer();
        chestLayer.setName("Coffre");

        // 1x1 case (250x250 px) alors que RIGHT exige 1x2 cases
        RectangleMapObject badChest = new RectangleMapObject(250f, 250f, 250f, 250f);
        badChest.getProperties().put("Orientation", "RIGHT");
        chestLayer.getObjects().add(badChest);
        map.getLayers().add(chestLayer);

        assertThrows(IllegalArgumentException.class, () -> loader.loadChestSpawns(map));
        map.dispose();
    }

    @Test
    void loadMonsterSpawn_centersTwoByTwoAnchorAndInvertsTiledYAxis() {
        TiledMap map = new TiledMap();
        map.getProperties().put("height", 30);

        MapLayer monsterLayer = new MapLayer();
        monsterLayer.setName("monstre_start");

        // Point Tiled au centre de la zone 2x2 en haut à droite : x=7000 (col 28), y=7000 (row 28)
        // Coin bas-gauche attendu : (28 - 1, 28 - 1) = (27, 27)
        PointMapObject spawnPoint = new PointMapObject(7000f, 7000f);
        monsterLayer.getObjects().add(spawnPoint);
        map.getLayers().add(monsterLayer);

        GridPosition pos = loader.loadMonsterSpawn(map);

        assertEquals(new GridPosition(27, 27), pos);
        map.dispose();
    }

    @Test
    void loadMonsterType_readsCustomPropertyOrReturnsNullWhenAbsent() {
        TiledMap mapWithType = new TiledMap();
        MapLayer layerWithType = new MapLayer();
        layerWithType.setName("monstre_start");
        PointMapObject objWithType = new PointMapObject(7000f, 500f);
        objWithType.getProperties().put("monsterType", "jumper");
        layerWithType.getObjects().add(objWithType);
        mapWithType.getLayers().add(layerWithType);

        TiledMap mapWithoutType = new TiledMap();
        MapLayer layerWithoutType = new MapLayer();
        layerWithoutType.setName("monstre_start");
        layerWithoutType.getObjects().add(new PointMapObject(7000f, 500f));
        mapWithoutType.getLayers().add(layerWithoutType);

        assertAll(
                () -> assertEquals(MonsterType.JUMPER, loader.loadMonsterType(mapWithType)),
                () -> assertNull(loader.loadMonsterType(mapWithoutType)));

        mapWithType.dispose();
        mapWithoutType.dispose();
    }

    @Test
    void loadChestSpawns_readsOptionalLootPropertyAndRejectsUnknownItem() {
        TiledMap validMap = new TiledMap();
        MapLayer validLayer = new MapLayer();
        validLayer.setName("Coffre");
        RectangleMapObject customChest = new RectangleMapObject(250f, 3500f, 250f, 500f);
        customChest.getProperties().put("Orientation", "RIGHT");
        customChest.getProperties().put("loot", "dynamite");
        validLayer.getObjects().add(customChest);
        validMap.getLayers().add(validLayer);

        TiledMap invalidMap = new TiledMap();
        MapLayer invalidLayer = new MapLayer();
        invalidLayer.setName("Coffre");
        RectangleMapObject badChest = new RectangleMapObject(0f, 0f, 500f, 250f);
        badChest.getProperties().put("Orientation", "BOTTOM");
        badChest.getProperties().put("loot", "EXCALIBUR");
        invalidLayer.getObjects().add(badChest);
        invalidMap.getLayers().add(invalidLayer);

        List<ChestSpawn> spawns = loader.loadChestSpawns(validMap);

        assertAll(
                () -> assertEquals(1, spawns.size()),
                () -> assertEquals(ItemType.DYNAMITE, spawns.get(0).fixedLoot()),
                () -> assertThrows(IllegalArgumentException.class, () -> loader.loadChestSpawns(invalidMap)));

        validMap.dispose();
        invalidMap.dispose();
    }
}
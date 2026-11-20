package com.blindpath.infrastructure.map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.blindpath.domain.inventory.ItemType;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.labyrinth.LabyrinthLoader.ChestSpawn;
import com.blindpath.domain.labyrinth.LabyrinthLoader.LabyrinthData;
import com.blindpath.domain.monster.MonsterType;
import com.blindpath.domain.pathfinding.GridPosition;

class TiledLabyrinthLoaderTest {

    private final TiledLabyrinthLoader loader = new TiledLabyrinthLoader();

    @Test
    void loadLeversResolvesPivotAndLeverByObjectIdOrResolvedObject() {
        TiledMap map = new TiledMap();
        try {
            assertTrue(loader.loadLevers(map).isEmpty());
            MapLayer layer = new MapLayer();
            layer.setName("movableWall");
            RectangleMapObject wall = new RectangleMapObject(1500, 5250, 250, 500);
            wall.setName("movableWall");
            wall.getProperties().put("orientation", "VERTICAL");
            wall.getProperties().put("rotation", 90);
            wall.getProperties().put("pivot", 313);
            wall.getProperties().put("levier", 312);
            PointMapObject pivot = new PointMapObject(1750, 5750);
            pivot.getProperties().put("id", 313);
            RectangleMapObject lever = new RectangleMapObject(3000, 5500, 250, 250);
            lever.getProperties().put("id", 312);
            layer.getObjects().add(wall);
            layer.getObjects().add(pivot);
            layer.getObjects().add(lever);
            map.getLayers().add(layer);
            var loaded = loader.loadLevers(map);
            assertEquals(1, loaded.size());
            assertEquals(12f, loaded.get(0).getBounds().getX());
            var rotatingWall = loaded.get(0).getWall();
            assertEquals(7f, rotatingWall.getPivotX());
            assertEquals(23f, rotatingWall.getPivotY());
            rotatingWall.rotate();
            assertEquals(7f, rotatingWall.getBounds().getX());
            assertEquals(22f, rotatingWall.getBounds().getY());

            wall.getProperties().put("pivot", pivot);
            wall.getProperties().put("levier", lever);
            assertEquals(1, loader.loadLevers(map).size());
            wall.getProperties().put("rotation", 45);
            assertThrows(IllegalArgumentException.class, () -> loader.loadLevers(map));
            wall.getProperties().put("rotation", -90);
            wall.getProperties().put("pivot", 999);
            assertThrows(IllegalArgumentException.class, () -> loader.loadLevers(map));
        } finally {
            map.dispose();
        }
    }

    @Test
    void loadBramblesReadsOptionalLayerAndBothOrientationsWithoutMakingWalls() {
        TiledMap map = new TiledMap();
        try {
            assertTrue(loader.loadBrambles(map).isEmpty());
            MapLayer layer = new MapLayer();
            layer.setName("ronces");
            RectangleMapObject horizontal = new RectangleMapObject(1000, 1000, 500, 250);
            horizontal.getProperties().put("orientation", "HORIZONTAL");
            RectangleMapObject vertical = new RectangleMapObject(2000, 1500, 250, 500);
            vertical.getProperties().put("orientation", "Vertical");
            layer.getObjects().add(horizontal);
            layer.getObjects().add(vertical);
            map.getLayers().add(layer);
            var brambles = loader.loadBrambles(map);
            assertEquals(2, brambles.size());
            assertEquals(4f, brambles.get(0).getBounds().getX());
            assertEquals(2f, brambles.get(0).getBounds().getWidth());
            assertEquals(OpeningOrientation.VERTICAL, brambles.get(1).getOrientation());
            assertEquals(6f, brambles.get(1).getBounds().getY());
            assertFalse(brambles.get(0).isActive());
            vertical.getProperties().put("orientation", "VERCTICAL");
            assertThrows(IllegalArgumentException.class, () -> loader.loadBrambles(map));
        } finally {
            map.dispose();
        }
    }

    @Test
    void loadExitReadsOrientationForAnInteriorOpening() {
        TiledMap map = new TiledMap();
        MapLayer layer = new MapLayer();
        layer.setName("Exit");
        RectangleMapObject exit = new RectangleMapObject(1000f, 1000f, 500f, 250f);
        exit.setName("Exit");
        exit.getProperties().put("orientation", "HORIZONTAL");
        layer.getObjects().add(exit);
        map.getLayers().add(layer);
        try {
            assertEquals(OpeningOrientation.HORIZONTAL, loader.loadExit(map).getOrientation());
            assertEquals(4, loader.loadExit(map).getX());
            assertEquals(4, loader.loadExit(map).getY());
            exit.getProperties().put("orientation", "TOP");
            assertThrows(IllegalArgumentException.class, () -> loader.loadExit(map));
            exit.getProperties().put("orientation", "VERTICAL");
            assertThrows(IllegalArgumentException.class, () -> loader.loadExit(map));
        } finally {
            map.dispose();
        }
    }

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

        @Test
    void loadAll_extractsLabyrinthChestsMonsterSpawnAndTypeInSinglePass() {
        TiledMap map = new TiledMap();
        map.getProperties().put("width", 10);
        map.getProperties().put("height", 10);

        MapLayer collisionsLayer = new MapLayer();
        collisionsLayer.setName("Collisions");
        map.getLayers().add(collisionsLayer);

        MapLayer entranceLayer = new MapLayer();
        entranceLayer.setName("Entrance");
        RectangleMapObject entrance = new RectangleMapObject(0f, 1000f, 250f, 500f);
        entrance.setName("Entrance");
        entrance.getProperties().put("orientation", "VERTICAL");
        entranceLayer.getObjects().add(entrance);
        map.getLayers().add(entranceLayer);

        MapLayer exitLayer = new MapLayer();
        exitLayer.setName("Exit");
        RectangleMapObject exit = new RectangleMapObject(2250f, 1000f, 250f, 500f);
        exit.setName("Exit");
        exit.getProperties().put("orientation", "VERTICAL");
        exitLayer.getObjects().add(exit);
        map.getLayers().add(exitLayer);

        MapLayer monsterLayer = new MapLayer();
        monsterLayer.setName("monstre_start");
        PointMapObject monsterObj = new PointMapObject(1500f, 1500f);
        monsterObj.getProperties().put("monsterType", "SCREAMER");
        monsterLayer.getObjects().add(monsterObj);
        map.getLayers().add(monsterLayer);

        LabyrinthData data = loader.loadAll(map);

        assertAll(
                () -> assertEquals(10, data.labyrinth().getWidth()),
                () -> assertEquals(new GridPosition(5, 5), data.monsterSpawn()),
                () -> assertEquals(MonsterType.SCREAMER, data.monsterType()),
                () -> assertTrue(data.chestSpawns().isEmpty()));
        map.dispose();
    }
}

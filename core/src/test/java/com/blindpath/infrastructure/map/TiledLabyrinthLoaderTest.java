package com.blindpath.infrastructure.map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.LabyrinthLoader.ChestSpawn;
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
    void loadMonsterSpawn_readsPointMapObjectOnGrid() {
        TiledMap map = new TiledMap();
        MapLayer monsterLayer = new MapLayer();
        monsterLayer.setName("monstre_start");

        // Point à (28, 28) -> (7000px, 7000px)
        PointMapObject spawnPoint = new PointMapObject(7000f, 7000f);
        monsterLayer.getObjects().add(spawnPoint);
        map.getLayers().add(monsterLayer);

        GridPosition pos = loader.loadMonsterSpawn(map);

        assertEquals(new GridPosition(28, 28), pos);
        map.dispose();
    }
}
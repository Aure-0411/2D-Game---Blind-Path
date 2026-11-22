# UML : Chargement des cartes

Package `com.blindpath.infrastructure.map`. Lecture des cartes Tiled et catalogue des niveaux, derrière les interfaces du domaine `LabyrinthLoader` et `LevelCatalog`.

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.infrastructure.map =====
    class LevelRegistry {
        -List~LevelConfig~ levels
        -int mediumStart
        -int hardStart
        -int extremeStart
        +LevelRegistry()
        +LevelRegistry(String indexContent)
        -readIndexFile()$ String
        +getLevel(int levelNumber) LevelConfig
        +totalLevels() int
        +tierStartLevel(int levelNumber) int
    }
    class TiledLabyrinthLoader {
        -float UNIT_SCALE = 1f / 250f$
        -TiledMapStore store
        +TiledLabyrinthLoader()
        +TiledLabyrinthLoader(TiledMapStore store)
        -read(String mapPath, Function~TiledMap,T~ reader) T
        -parseLootType(MapObject object)$ ItemType
        +load(String mapPath) Labyrinth
        +load(TiledMap map) Labyrinth
        +loadLevers(TiledMap map) List~Lever~
        -referencedObject(TiledMap map, MapObject source, String property) MapObject
        +loadBrambles(TiledMap map) List~Bramble~
        +loadAll(String mapPath) LabyrinthData
        +loadAll(TiledMap map) LabyrinthData
        +loadObstacles(TiledMap map) List~Obstacle~
        +loadEntrance(TiledMap map) Entrance
        +loadExit(TiledMap map) Exit
        +loadChestSpawns(String mapPath) List~ChestSpawn~
        +loadChestSpawns(TiledMap map) List~ChestSpawn~
        +loadMonsterType(String mapPath) MonsterType
        +loadMonsterType(TiledMap map) MonsterType
        +loadMonsterSpawn(String mapPath) GridPosition
        +loadMonsterSpawn(TiledMap map) GridPosition
        -loadOpening(TiledMap map, String name) RectangleMapObject
        -readOrientation(MapObject object) OpeningOrientation
        -toGridCoordinate(float pixels) int
        -validateOpeningSize(Rectangle rectangle, Border border) void
        -validateOpeningSize(Rectangle rectangle, boolean horizontal, String orientation) void
    }
    class TiledMapStore {
        -AssetManager assets
        -Set~String~ requested
        +TiledMapStore()
        +preload(String path) void
        +update() void
        +get(String path) TiledMap
        +retainOnly(Set~String~ keep) void
        +dispose() void
    }
    %% ===== Classes d'autres packages (detail dans leur propre fichier) =====
    class LabyrinthLoader {
        <<interface>>
    }
    class LevelCatalog {
        <<interface>>
    }
    class LevelConfig {
        <<record>>
    }
    %% ===== Relations =====
    LevelCatalog <|.. LevelRegistry
    LevelRegistry --> "*" LevelConfig : levels
    LabyrinthLoader <|.. TiledLabyrinthLoader
    TiledLabyrinthLoader --> "1" TiledMapStore : store
    style LabyrinthLoader fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style LevelCatalog fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style LevelConfig fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
```

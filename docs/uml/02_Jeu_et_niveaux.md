# UML : Jeu et niveaux

Package `com.blindpath.domain.game`. `GameSession` enchaîne les niveaux, `Level` assemble le héros, le labyrinthe, le monstre, les coffres et les objets posés. `LevelFactory` construit un niveau à partir de sa `LevelConfig` (patron **Fabrique**).

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.domain.game =====
    class GameSession~T~ {
        -LevelFactory factory
        -T hero
        -Map~Integer,List‹Chest›~ chestsByLevel
        -int currentLevelNumber
        -int retryCount
        -Level~T~ level
        -boolean won
        +GameSession(LevelFactory factory, T hero)
        +startLevel(int number) void
        +nextLevel() void
        +retry() void
        +getHero() T
        +getLevel() Level~T~
        +getCurrentLevelNumber() int
        +getRetryCount() int
        +isWon() boolean
    }
    class Level~T~ {
        -T hero
        -Labyrinth labyrinth
        -CollisionWorld collisions
        -Monster monster
        -Pathfinder pathfinder
        -List~Chest~ chests
        -List~PlacedObject~ placedObjects
        +Level(T hero, Labyrinth labyrinth, CollisionWorld collisions, Monster monster, Pathfinder pathfinder, List~Chest~ chests)
        +getHero() T
        +getLabyrinth() Labyrinth
        +getMonster() Monster
        +setMonster(Monster monster) void
        +getChests() List~Chest~
        +getPathfinder() Pathfinder
        +getPlacedObjects() List~PlacedObject~
        +update(float deltaTime) void
        +tryMove(Character character, float dx, float dy, float deltaTime) boolean
        +emitSnap(SnapEvent event) void
        +addPlacedObject(PlacedObject object) void
        -updateRotatingWalls() void
        +areControlsInverted() boolean
        +isCompleted() boolean
    }
    class LevelCatalog {
        <<interface>>
        +getLevel(int levelNumber)* LevelConfig
        +totalLevels()* int
        +tierStartLevel(int levelNumber)* int
    }
    class LevelConfig {
        <<record>>
        -String mapPath
        -MonsterType monsterType
    }
    class LevelFactory {
        +float CELL = 1f$
        -LevelCatalog registry
        -LabyrinthLoader loader
        -LootTable lootTable
        -Random random
        -Supplier~CollisionWorld~ collisionWorldFactory
        +LevelFactory(LevelCatalog registry, LabyrinthLoader loader, LootTable lootTable, Random random)
        +LevelFactory(LevelCatalog registry, LabyrinthLoader loader, LootTable lootTable, Random random, Supplier~CollisionWorld~ collisionWorldFactory)
        +configFor(int number) LevelConfig
        +totalLevels() int
        +createChests(int number) List~Chest~
        +create(int number, T hero, List~Chest~ chests) Level~T~
        +tierStartFor(int number) int
    }
    %% ===== Classes d'autres packages (detail dans leur propre fichier) =====
    class Character {
        <<abstract>>
    }
    class Chest
    class CollisionWorld {
        <<interface>>
    }
    class Labyrinth
    class LabyrinthLoader {
        <<interface>>
    }
    class LootTable
    class Monster {
        <<abstract>>
    }
    class MonsterType {
        <<enumeration>>
    }
    class Pathfinder {
        <<interface>>
    }
    class PlacedObject {
        <<abstract>>
    }
    %% ===== Relations =====
    GameSession --> "1" LevelFactory : factory
    GameSession --> "1" Character : hero
    GameSession --> "*" Chest : chestsByLevel
    GameSession --> "1" Level : level
    Level --> "1" Character : hero
    Level --> "1" Labyrinth : labyrinth
    Level --> "1" CollisionWorld : collisions
    Level --> "1" Monster : monster
    Level --> "1" Pathfinder : pathfinder
    Level --> "*" Chest : chests
    Level --> "*" PlacedObject : placedObjects
    LevelConfig --> "1" MonsterType : monsterType
    LevelFactory --> "1" LevelCatalog : registry
    LevelFactory --> "1" LabyrinthLoader : loader
    LevelFactory --> "1" LootTable : lootTable
    LevelFactory --> "1" CollisionWorld : collisionWorldFactory
    LevelCatalog ..> LevelConfig
    LevelFactory ..> LevelConfig
    LevelFactory ..> Chest
    LevelFactory ..> Level
    style Character fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Chest fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style CollisionWorld fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Labyrinth fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style LabyrinthLoader fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style LootTable fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Monster fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style MonsterType fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Pathfinder fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style PlacedObject fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
```

# UML : Labyrinthe et éléments de carte

Package `com.blindpath.domain.labyrinth`. Le labyrinthe, ses obstacles (murs, ronces, mur pivotant + levier), l'entrée et la sortie, et le contrat `Collidable`.

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.domain.labyrinth =====
    class Border {
        <<enumeration>>
        TOP
        BOTTOM
        LEFT
        RIGHT
    }
    class Bounds {
        -float x
        -float y
        -float width
        -float height
        +Bounds(float x, float y, float width, float height)
        +getX() float
        +getY() float
        +getWidth() float
        +getHeight() float
        +getCenterX() float
        +getCenterY() float
        +intersects(Bounds other) boolean
    }
    class Bramble {
        +float SNAP_RANGE = 3f$
        +float DURATION = 0.5f$
        +int DAMAGE = 2$
        -float x
        -float y
        -OpeningOrientation orientation
        -GameTimer timer
        +Bramble(float x, float y, OpeningOrientation orientation)
        +getOrientation() OpeningOrientation
        +getBounds() Bounds
        +isActive() boolean
        +activate() void
        +deactivate() void
        +update(float delta) void
        +onSnap(SnapEvent event) void
        +getExtension() float
        +getDamageBounds() Bounds
        +damages(Bounds target) boolean
    }
    class Collidable {
        <<interface>>
        +canCollide()* boolean
        +collide(Collidable other)* boolean
        +getBounds()* Bounds
    }
    class Entrance {
        -int x
        -int y
        -int height
        -int width
        -OpeningOrientation orientation
        +Entrance(int x, int y, OpeningOrientation orientation)
        +getX() int
        +getY() int
        +getHeight() int
        +getWidth() int
        +getOrientation() OpeningOrientation
    }
    class Exit {
        -int height
        -int width
        -int x
        -int y
        -Bounds bounds
        -OpeningOrientation orientation
        +Exit(int x, int y, OpeningOrientation orientation)
        +getX() int
        +getY() int
        +getHeight() int
        +getWidth() int
        +getOrientation() OpeningOrientation
        +getBounds() Bounds
        +isReached(Character character) boolean
    }
    class Labyrinth {
        -int width
        -int height
        -List~Obstacle~ obstacles
        -List~Obstacle~ boundaryWalls
        -Entrance entrance
        -Exit exit
        -List~Bramble~ brambles
        -List~Lever~ levers
        -List~RotatingWall~ rotatingWalls
        +Labyrinth(int width, int height, Entrance entrance, Exit exit, List~Obstacle~ obstacles)
        +Labyrinth(int width, int height, Entrance entrance, Exit exit, List~Obstacle~ obstacles, List~Bramble~ brambles)
        +Labyrinth(int width, int height, Entrance entrance, Exit exit, List~Obstacle~ obstacles, List~Bramble~ brambles, List~Lever~ levers)
        +getBoundaryWalls() List~Obstacle~
        -validateOpening(int x, int y, int openingWidth, int openingHeight) void
        +getWidth() int
        +getHeight() int
        +getObstacles() List~Obstacle~
        +getBrambles() List~Bramble~
        +getLevers() List~Lever~
        +getRotatingWalls() List~RotatingWall~
        +getEntrance() Entrance
        +getExit() Exit
    }
    class LabyrinthLoader {
        <<interface>>
        +load(String path)* Labyrinth
        +loadChestSpawns(String path) List~ChestSpawn~
        +loadMonsterType(String path) MonsterType
        +loadMonsterSpawn(String path) GridPosition
        +loadAll(String path) LabyrinthData
    }
    class LabyrinthLoader_ChestSpawn["LabyrinthLoader.ChestSpawn"] {
        <<record>>
        -float x
        -float y
        -Border orientation
        -ItemType fixedLoot
        +ChestSpawn(float x, float y, Border orientation)
    }
    class LabyrinthLoader_LabyrinthData["LabyrinthLoader.LabyrinthData"] {
        <<record>>
        -Labyrinth labyrinth
        -List~ChestSpawn~ chestSpawns
        -GridPosition monsterSpawn
        -MonsterType monsterType
    }
    class Lever {
        +float SNAP_RANGE = 2f$
        -float x
        -float y
        -float width
        -float height
        -RotatingWall wall
        +Lever(float x, float y, float width, float height, RotatingWall wall)
        +getWall() RotatingWall
        +isActivated() boolean
        +getBounds() Bounds
        +onSnap(SnapEvent event, Bounds[] occupants) boolean
    }
    class Obstacle {
        <<abstract>>
        -float x
        -float y
        -float height
        -float width
        -boolean collide
        #Obstacle(float x, float y, float width, float height)
        +setCollide(boolean state) void
        +getBounds() Bounds
        +collide(Collidable other) boolean
        +canCollide() boolean
    }
    class OpeningOrientation {
        <<enumeration>>
        HORIZONTAL
        VERTICAL
    }
    class RotatingWall {
        -Bounds initialBounds
        -OpeningOrientation initialOrientation
        -float pivotX
        -float pivotY
        -int turn
        -boolean rotated
        -boolean waitingForClearance
        +RotatingWall(float x, float y, OpeningOrientation orientation, float pivotX, float pivotY, int turn)
        +getInitialBounds() Bounds
        +getPivotX() float
        +getPivotY() float
        +getTurn() int
        +isRotated() boolean
        +getInitialOrientation() OpeningOrientation
        +getBounds() Bounds
        +rotate(Bounds[] occupants) boolean
        +updateClearance(Bounds[] occupants) void
        -overlapsAny(Bounds[] occupants) boolean
    }
    class Wall {
        -boolean blocksMonster
        +Wall(float x, float y, float width, float height)
        +blocksMonster() boolean
    }
    %% ===== Classes d'autres packages (detail dans leur propre fichier) =====
    class ItemType {
        <<enumeration>>
    }
    class MonsterType {
        <<enumeration>>
    }
    %% ===== Relations =====
    Bramble --> "1" OpeningOrientation : orientation
    Entrance --> "1" OpeningOrientation : orientation
    Exit --> "1" Bounds : bounds
    Exit --> "1" OpeningOrientation : orientation
    Labyrinth --> "*" Obstacle : obstacles
    Labyrinth --> "1" Entrance : entrance
    Labyrinth --> "1" Exit : exit
    Labyrinth --> "*" Bramble : brambles
    Labyrinth --> "*" Lever : levers
    Labyrinth --> "*" RotatingWall : rotatingWalls
    LabyrinthLoader *-- LabyrinthLoader_ChestSpawn : imbriqué
    LabyrinthLoader_ChestSpawn --> "1" Border : orientation
    LabyrinthLoader_ChestSpawn --> "1" ItemType : fixedLoot
    LabyrinthLoader *-- LabyrinthLoader_LabyrinthData : imbriqué
    LabyrinthLoader_LabyrinthData --> "1" Labyrinth : labyrinth
    LabyrinthLoader_LabyrinthData --> "*" LabyrinthLoader_ChestSpawn : chestSpawns
    LabyrinthLoader_LabyrinthData --> "1" MonsterType : monsterType
    Lever --> "1" RotatingWall : wall
    Collidable <|.. Obstacle
    Wall <|-- RotatingWall
    RotatingWall --> "1" Bounds : initialBounds
    RotatingWall --> "1" OpeningOrientation : initialOrientation
    Obstacle <|-- Wall
    LabyrinthLoader ..> Labyrinth
    LabyrinthLoader ..> MonsterType
    style ItemType fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style MonsterType fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
```

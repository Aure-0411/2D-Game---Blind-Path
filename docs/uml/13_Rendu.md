# UML : Rendu (renderers)

Package `com.blindpath.presentation.renderer`. Une classe de rendu par élément du jeu. Seuls les membres publics et protégés sont affichés.

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction LR
    %% ===== com.blindpath.presentation.renderer =====
    class BrambleRenderer {
        +render(SpriteBatch batch, List~Bramble~ brambles) void
        +dispose() void
    }
    class CharacterRenderer~T~ {
        +CharacterRenderer(String ATLAS_PATH, Function~Direction,String~ regionNameResolver, Direction defaultDirection, float frameDuration)
        +render(SpriteBatch batch, T character, float stateTime, boolean isMoving) void
        +dispose() void
    }
    class ChestRenderer {
        +ChestRenderer()
        +render(SpriteBatch batch, List~Chest~ chests) void
        +dispose() void
    }
    class CollisionDebugRenderer {
        +render(Camera camera, Level~?~ level) void
        +dispose() void
    }
    class CompassArrowRenderer {
        +render(OrthographicCamera camera, Character hero, Exit exit) void
        +dispose() void
    }
    class DamageOverlayRenderer {
        +float DURATION = 0.5f$
        +DamageOverlayRenderer()
        +trigger() void
        +isActive() boolean
        +update(float delta) void
        +resize(int width, int height) void
        +render(SpriteBatch batch) void
        +dispose() void
    }
    class FogRenderer {
        +FogRenderer()
        +render(OrthographicCamera camera, float centerX, float centerY, float radius, boolean revealing, float snapX, float snapY, float snapProgress, float snapRadius, float delta) void
        +dispose() void
    }
    class GnomeSprites {
        +String ATLAS_PATH = gnome/gnome.atlas$
        +regionName(Direction d)$ String
    }
    class JumpscareRenderer {
        +float DURATION = 0.8f$
        +float WIDTH = 1920f$
        +float HEIGHT = 1080f$
        +JumpscareRenderer(String name)
        +trigger() void
        +isActive() boolean
        +update(float delta) void
        +resize(int width, int height) void
        +render(SpriteBatch batch) void
        +render(SpriteBatch batch, boolean holdForAudio) void
        +dispose() void
    }
    class LabyrinthRenderer {
        +LabyrinthRenderer(TiledMap map)
        +render(OrthographicCamera camera) void
        +dispose() void
    }
    class LeverWallRenderer {
        +render(SpriteBatch batch, List~RotatingWall~ walls, List~Lever~ levers) void
        +dispose() void
    }
    class MonsterRenderer {
        +MonsterRenderer(String name)
        +render(SpriteBatch batch, Monster monster, float stateTime) void
        +dispose() void
    }
    class PlacedObjectRenderer {
        +PlacedObjectRenderer()
        +render(SpriteBatch batch, List~PlacedObject~ placedObjects) void
        +dispose() void
    }
    %% ===== Classes d'autres packages (detail dans leur propre fichier) =====
    class Bramble
    class Character {
        <<abstract>>
    }
    class Chest
    class Direction {
        <<enumeration>>
    }
    class Exit
    class Level~T~
    class Lever
    class Monster {
        <<abstract>>
    }
    class PlacedObject {
        <<abstract>>
    }
    class RotatingWall
    %% ===== Relations =====
    CharacterRenderer --> "*" Direction : animations
    BrambleRenderer ..> Bramble
    CharacterRenderer ..> Character
    ChestRenderer ..> Chest
    CollisionDebugRenderer ..> Level
    CompassArrowRenderer ..> Character
    CompassArrowRenderer ..> Exit
    GnomeSprites ..> Direction
    LeverWallRenderer ..> RotatingWall
    LeverWallRenderer ..> Lever
    MonsterRenderer ..> Monster
    PlacedObjectRenderer ..> PlacedObject
    style Bramble fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Character fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Chest fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Direction fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Exit fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Level fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Lever fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Monster fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style PlacedObject fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style RotatingWall fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
```

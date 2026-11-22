# UML : Physique et collisions

Packages `com.blindpath.domain.physics` (contrat + version simple AABB) et `com.blindpath.infrastructure.physics` (version Box2D).

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.domain.physics =====
    namespace domain_physics {
        class AabbCollisionWorld {
            -float EPSILON = 1e-6f$
            -int BISECTION_STEPS = 12$
            -List~Bounds~ statics
            -List~Obstacle~ changingObstacles
            +addChangingObstacle(Obstacle obstacle) void
            +syncChangingObstacles() void
            +addStatic(Bounds bounds) void
            +addDynamic(Character character) void
            +move(Character c, float dirX, float dirY, float deltaTime) boolean
            -reach(Character c, float x, float y, float dx, float dy) float
            -isFree(Character c, float x, float y) boolean
            +dispose() void
        }
        class CollisionWorld {
            <<interface>>
            +addStatic(Bounds bounds)* void
            +addChangingObstacle(Obstacle obstacle)* void
            +syncChangingObstacles()* void
            +addDynamic(Character character)* void
            +move(Character character, float directionX, float directionY, float deltaTime)* boolean
            +dispose()* void
        }
    }
    %% ===== com.blindpath.infrastructure.physics =====
    namespace infrastructure_physics {
        class Box2DCollisionWorld {
            -float MAX_STEP = 1f / 30f$
            -int VELOCITY_ITERATIONS = 8$
            -int POSITION_ITERATIONS = 3$
            -float MOVE_EPSILON = 1e-6f$
            -World world
            -Map~Character,Body~ bodies
            -Map~Obstacle,ChangingBody~ changingBodies
            +addStatic(Bounds bounds) void
            -createStaticBody(Bounds bounds) Body
            +addChangingObstacle(Obstacle obstacle) void
            +syncChangingObstacles() void
            +addDynamic(Character character) void
            +move(Character character, float dirX, float dirY, float deltaTime) boolean
            +dispose() void
            -centerX(Character c)$ float
            -centerY(Character c)$ float
            -chamferedBox(float hw, float hh)$ float[]
        }
    }
    %% ===== Classes d'autres packages (detail dans leur propre fichier) =====
    class Character {
        <<abstract>>
    }
    class Obstacle {
        <<abstract>>
    }
    %% ===== Relations =====
    CollisionWorld <|.. AabbCollisionWorld
    AabbCollisionWorld --> "*" Obstacle : changingObstacles
    CollisionWorld <|.. Box2DCollisionWorld
    Box2DCollisionWorld --> "*" Character : bodies
    Box2DCollisionWorld --> "*" Obstacle : changingBodies
    style Character fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Obstacle fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
```

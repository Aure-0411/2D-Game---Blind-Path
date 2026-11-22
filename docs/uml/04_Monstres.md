# UML : Monstres

Package `com.blindpath.domain.monster`. Diagramme écrit à partir du code de `dev`.

Patrons de conception visibles ici :
- **Héritage** : `Monster` (abstraite) et ses 5 monstres.
- **Strategy** : `MovementStrategy`, implémentée par `ChaseStrategy` et `JumperStrategy`.
- **Fabrique** : l'enum `MonsterType` et sa méthode `create`.

```mermaid
classDiagram
    direction TB

    %% ===== Contrats venant d'autres packages =====
    class SnapListener {
        <<interface>>
        +onSnap(SnapEvent event) void
    }
    class TickListener {
        <<interface>>
        +onTick(long tick) void
    }
    class Collidable {
        <<interface>>
        +canCollide() boolean
        +collide(Collidable other) boolean
        +getBounds() Bounds
    }

    %% ===== Classe mere =====
    class Monster {
        <<abstract>>
        +int DEFAULT_TICKS_PER_MOVE = 30$
        +int DEFAULT_CASE_PER_MOVE = 1$
        +int DEFAULT_SNAP_DISTANCE = 1$
        +int SIZE = 2$
        -LabyrinthGrid grid
        -MovementStrategy strategy
        -GridPosition position
        -GridPosition target
        -int ticksPerMove
        -int casePerMove
        -int snapDistance
        -boolean visible
        -GameTimer immobilization
        #Monster(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start)
        +getPosition() GridPosition
        +setTarget(GridPosition target) void
        +getTicksPerMove() int
        +setTicksPerMove(int v) void
        +getCasePerMove() int
        +setCasePerMove(int v) void
        +getSnapDistance() int
        +setSnapDistance(int v) void
        +isVisible() boolean
        +setVisible(boolean visible) void
        +immobilize(float duration) void
        +isImmobilized() boolean
        +canMove() boolean
        +update(float deltaTime) void
        #moveTowards(GridPosition goal, int steps) void
        +onTick(long tick) void
        +onSnap(SnapEvent event) void
        +canCollide() boolean
        +collide(Collidable other) boolean
        +getBounds() Bounds
    }

    %% ===== Les 5 monstres =====
    class Jumper {
        -JumperStrategy jumperStrategy
        +getState() JumperState
    }
    class Runaway {
        +int TICKS_PER_MOVE = 60$
        +int CASE_PER_MOVE = 2$
        +int EXIT_SNAP_DISTANCE = 3$
        -GridPosition exit
        +getExit() GridPosition
        +onSnap(SnapEvent event) void
    }
    class Screamer {
        +int MIN_SNAPS = 5$
        +int MAX_SNAPS = 8$
        +float STUN_DURATION = 3$
        +float SCREAM_ANIMATION_DURATION = 1$
        -Random random
        -int screamThreshold
        -int snapCount
        -boolean screamed
        -long screamSequence
        -GameTimer screamAnimation
        +onSnap(SnapEvent event) void
        +update(float deltaTime) void
        +isScreaming() boolean
        +consumeScream() boolean
        +getScreamSequence() long
        +getSnapCount() int
        +getScreamThreshold() int
    }
    class Chameleon {
        +int CASE_PER_MOVE = 1$
        +int REVEAL_RANGE = 3$
        +isRevealed() boolean
    }
    class Inverter {
        +int CASE_PER_MOVE = 1$
        +int INVERT_RANGE = 3$
        +float INVERT_AFTER_EXIT_DURATION = 3$
        -GameTimer afterExitTimer
        -boolean wasInRange
        +isInRange() boolean
        +invertsControls() boolean
        +isAfterExitActive() boolean
        +update(float deltaTime) void
    }

    %% ===== Patron Strategy (deplacement) =====
    class MovementStrategy {
        <<interface>>
        +nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target) GridPosition
    }
    class ChaseStrategy {
        -Pathfinder pathfinder
        +nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target) GridPosition
    }
    class JumperStrategy {
        +int DEFAULT_JUMP_RANGE = 2$
        -Pathfinder pathfinder
        -int jumpRange
        -JumperState state
        +getState() JumperState
        +getJumpRange() int
        +nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target) GridPosition
    }
    class JumperState {
        <<enumeration>>
        CHASING
        ALERT
    }

    %% ===== Fabrique =====
    class MonsterType {
        <<enumeration>>
        JUMPER
        RUNAWAY
        SCREAMER
        CHAMELEON
        INVERTER
        +create(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) Monster
    }

    %% ===== Relations =====
    SnapListener <|.. Monster
    TickListener <|.. Monster
    Collidable <|.. Monster

    Monster <|-- Jumper
    Monster <|-- Runaway
    Monster <|-- Screamer
    Monster <|-- Chameleon
    Monster <|-- Inverter

    MovementStrategy <|.. ChaseStrategy
    MovementStrategy <|.. JumperStrategy
    Monster o-- "1" MovementStrategy : strategy
    Jumper --> "1" JumperStrategy
    JumperStrategy --> JumperState

    MonsterType ..> Monster : create()
```

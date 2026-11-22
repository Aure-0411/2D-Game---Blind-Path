# UML : Monstres

Package `com.blindpath.domain.monster`.

Patrons de conception visibles ici :
- **Héritage** : `Monster` (abstraite) et ses 5 monstres.
- **Strategy** : `MovementStrategy`, implémentée par `ChaseStrategy` et `JumperStrategy`.
- **Fabrique** : l'enum `MonsterType` et sa méthode `create`.
- **Observer** : `Monster` écoute le claquement (`SnapListener`) et l'horloge (`TickListener`).

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.domain.monster =====
    class Chameleon {
        +int CASE_PER_MOVE = 1$
        +int REVEAL_RANGE = 3$
        +Chameleon(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder)
        +Chameleon(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start)
        +isRevealed() boolean
    }
    class ChaseStrategy {
        -Pathfinder pathfinder
        +ChaseStrategy(Pathfinder pathfinder)
        +nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target) GridPosition
    }
    class Inverter {
        +int CASE_PER_MOVE = 1$
        +int INVERT_RANGE = 3$
        +float INVERT_AFTER_EXIT_DURATION = 3f$
        -GameTimer afterExitTimer
        -boolean wasInRange
        +Inverter(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder)
        +Inverter(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start)
        +isInRange() boolean
        +invertsControls() boolean
        +isAfterExitActive() boolean
        +update(float deltaTime) void
    }
    class Jumper {
        -JumperStrategy jumperStrategy
        +Jumper(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder)
        +Jumper(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start)
        +getState() JumperState
    }
    class JumperState {
        <<enumeration>>
        CHASING
        ALERT
    }
    class JumperStrategy {
        +int DEFAULT_JUMP_RANGE = 2$
        -Pathfinder pathfinder
        -int jumpRange
        -JumperState state
        +JumperStrategy(Pathfinder pathfinder, int jumpRange)
        +JumperStrategy(Pathfinder pathfinder)
        +getState() JumperState
        +getJumpRange() int
        -isInRange(GridPosition from, GridPosition target) boolean
        -chase(LabyrinthGrid grid, GridPosition from, GridPosition target) GridPosition
        +nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target) GridPosition
    }
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
        #getGrid() LabyrinthGrid
        #getTarget() GridPosition
        +getPosition() GridPosition
        +setTarget(GridPosition t) void
        +getTicksPerMove() int
        +setTicksPerMove(int v) void
        +getCasePerMove() int
        +setCasePerMove(int v) void
        +getSnapDistance() int
        +setSnapDistance(int v) void
        +isVisible() boolean
        +setVisible(boolean visible) void
        +immobilize(float duration) void
        -startLongest(GameTimer timer, float duration)$ void
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
    class MonsterType {
        <<enumeration>>
        JUMPER
        RUNAWAY
        SCREAMER
        CHAMELEON
        INVERTER
        +create(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder) Monster
    }
    class MovementStrategy {
        <<interface>>
        +nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target)* GridPosition
    }
    class Runaway {
        +int TICKS_PER_MOVE = 2 * DEFAULT_TICKS_PER_MOVE$
        +int CASE_PER_MOVE = 2$
        +int EXIT_SNAP_DISTANCE = 3$
        -GridPosition exit
        +Runaway(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder)
        +getExit() GridPosition
        +onSnap(SnapEvent event) void
    }
    class Screamer {
        -Random random
        -int screamThreshold
        -boolean screamed
        -int snapCount
        -long screamSequence
        -GameTimer screamAnimation
        +int MIN_SNAPS = 5$
        +int MAX_SNAPS = 8$
        +float STUN_DURATION = 3f$
        +float SCREAM_ANIMATION_DURATION = 1f$
        +Screamer(LabyrinthGrid grid, GridPosition start, Pathfinder pathfinder)
        +Screamer(LabyrinthGrid grid, MovementStrategy strategy, GridPosition start, Random random)
        -drawThreshold() int
        +isScreaming() boolean
        +onSnap(SnapEvent event) void
        +update(float deltaTime) void
        +consumeScream() boolean
        +getScreamSequence() long
        +getSnapCount() int
        +getScreamThreshold() int
    }
    %% ===== Classes d'autres packages (detail dans leur propre fichier) =====
    class Collidable {
        <<interface>>
    }
    class LabyrinthGrid
    class Pathfinder {
        <<interface>>
    }
    class SnapListener {
        <<interface>>
    }
    class TickListener {
        <<interface>>
    }
    %% ===== Relations =====
    Monster <|-- Chameleon
    MovementStrategy <|.. ChaseStrategy
    ChaseStrategy --> "1" Pathfinder : pathfinder
    Monster <|-- Inverter
    Monster <|-- Jumper
    Jumper --> "1" JumperStrategy : jumperStrategy
    MovementStrategy <|.. JumperStrategy
    JumperStrategy --> "1" Pathfinder : pathfinder
    JumperStrategy --> "1" JumperState : state
    SnapListener <|.. Monster
    TickListener <|.. Monster
    Collidable <|.. Monster
    Monster --> "1" MovementStrategy : strategy
    Monster <|-- Runaway
    Monster <|-- Screamer
    Jumper ..> JumperState
    Monster ..> LabyrinthGrid
    MonsterType ..> Monster
    style Collidable fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style LabyrinthGrid fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Pathfinder fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style SnapListener fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style TickListener fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
```

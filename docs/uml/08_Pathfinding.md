# UML : Pathfinding

Packages `com.blindpath.domain.pathfinding` (contrat, sans LibGDX) et `com.blindpath.infrastructure.pathfinding` (implémentation avec gdx-ai). Le domaine ne dépend que de l'interface `Pathfinder`.

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.domain.pathfinding =====
    namespace domain_pathfinding {
        class GridPosition {
            -int column
            -int row
            +GridPosition(int column, int row)
            +getColumn() int
            +getRow() int
            +equals(Object other) boolean
            +hashCode() int
            +toString() String
        }
        class LabyrinthGrid {
            -int[][] DIRECTIONS$
            -float WALL_MARGIN = 0.05f$
            -Labyrinth labyrinth
            -int agentSize
            +LabyrinthGrid(Labyrinth labyrinth)
            +LabyrinthGrid(Labyrinth labyrinth, int agentSize)
            +getExitCell() GridPosition
            +getAgentSize() int
            +getWidth() int
            +getHeight() int
            +isInside(GridPosition position) boolean
            +isWalkable(GridPosition position) boolean
            +getNeighbors(GridPosition position) List~GridPosition~
            +anchorCovering(GridPosition cell) GridPosition
        }
        class Pathfinder {
            <<interface>>
            +findPath(LabyrinthGrid grid, GridPosition start, GridPosition target)* List~GridPosition~
        }
    }
    %% ===== com.blindpath.infrastructure.pathfinding =====
    namespace infrastructure_pathfinding {
        class GdxAiPathfinder {
            -GridDistanceHeuristic heuristic
            +findPath(LabyrinthGrid grid, GridPosition start, GridPosition target) List~GridPosition~
        }
        class GridDistanceHeuristic {
            +estimate(GridPosition node, GridPosition endNode) float
        }
        class GridGraph {
            -LabyrinthGrid grid
            -GridPosition[] nodes
            +GridGraph(LabyrinthGrid grid)
            +getIndex(GridPosition node) int
            +getNodeCount() int
            +getNode(GridPosition position) GridPosition
            +getConnections(GridPosition fromNode) Array~Connection‹GridPosition›~
        }
    }
    %% ===== Classes d'autres packages (detail dans leur propre fichier) =====
    class Labyrinth
    %% ===== Relations =====
    LabyrinthGrid --> "1" Labyrinth : labyrinth
    Pathfinder <|.. GdxAiPathfinder
    GdxAiPathfinder --> "1" GridDistanceHeuristic : heuristic
    GridGraph --> "1" LabyrinthGrid : grid
    GridGraph --> "*" GridPosition : nodes
    style Labyrinth fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
```

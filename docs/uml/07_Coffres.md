# UML : Coffres et butin

Package `com.blindpath.domain.chest`. Le coffre s'ouvre au claquement et donne un butin tiré dans la `LootTable`.

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.domain.chest =====
    class Chest {
        +float SHORT_SIDE = 1f$
        +float LONG_SIDE = 2f$
        +float DEFAULT_ACTIVATION_MARGIN = 1f$
        -float x
        -float y
        -float width
        -float height
        -Border orientation
        -float activationRadiusX
        -float activationRadiusY
        -LootResult loot
        -boolean opened
        -Character opener
        +Chest(float x, float y, Border orientation, LootResult loot)
        +getX() float
        +getY() float
        +getWidth() float
        +getHeight() float
        +getOrientation() Border
        +getActivationRadiusX() float
        +getActivationRadiusY() float
        +setOpener(Character character) void
        +open(Character character) boolean
        +isOpened() boolean
        +isInRange(float px, float py) boolean
        +onSnap(SnapEvent event) void
        +canCollide() boolean
        +collide(Collidable other) boolean
        +getBounds() Bounds
    }
    class LootResult {
        <<record>>
        -Item item
        -int quantity
    }
    class LootTable {
        -List~ItemType~ randomPool
        +LootTable()
        +LootTable(List~ItemType~ pool)
        +roll(Random random) LootResult
    }
    %% ===== Classes d'autres packages (detail dans leur propre fichier) =====
    class Border {
        <<enumeration>>
    }
    class Character {
        <<abstract>>
    }
    class Collidable {
        <<interface>>
    }
    class Item {
        <<abstract>>
    }
    class ItemType {
        <<enumeration>>
    }
    class SnapListener {
        <<interface>>
    }
    %% ===== Relations =====
    SnapListener <|.. Chest
    Collidable <|.. Chest
    Chest --> "1" Border : orientation
    Chest --> "1" LootResult : loot
    Chest --> "1" Character : opener
    LootResult --> "1" Item : item
    LootTable --> "*" ItemType : randomPool
    LootTable ..> LootResult
    style Border fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Character fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Collidable fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Item fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style ItemType fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style SnapListener fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
```

# UML : Personnage (héros)

Package `com.blindpath.domain.character`. Le gnome est le héros jouable ; `Character` porte la vie (en PV, 1 cœur = 2 PV), le stun et le déplacement en 8 directions.

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.domain.character =====
    class Character {
        <<abstract>>
        +int MONSTER_DAMAGE = 2$
        +float IMMUNITY_DURATION = 2f$
        -Inventory~Character~ inventory
        -CompassEffect compassEffect
        -int boostedSnapsLeft
        -int life
        -int maxLife
        -float speed
        -float x
        -float y
        -float width
        -float height
        -Direction facing
        -boolean invincible
        -float immunityTimer
        -float stunTimer
        +Character(float speed, float x, float y, float width, float height)
        +getMaxLife() int
        +getLife() int
        +setLife(int life) void
        +getSpeed() float
        +setSpeed(float speed) void
        +getX() float
        +getY() float
        +getWidth() float
        +getHeight() float
        +getInventory() Inventory~Character~
        +setPosition(float x, float y) void
        +getFacing() Direction
        +setFacing(Direction facing) void
        +move(float directionX, float directionY, float deltaTime) void
        +canCollide() boolean
        +collide(Collidable other) boolean
        +getBounds() Bounds
        +takeMonsterHit() void
        +takeDamage(int amount) void
        +isImmune() boolean
        +updateImmunity(float deltaTime) void
        +stun(float duration) void
        +isStunned() boolean
        +updateStun(float deltaTime) void
        +toggleInvincible() void
        +isInvincible() boolean
        +isDead() boolean
        +getCompassEffect() CompassEffect
        +activateCompass(float durationSeconds) void
        +isCompassActive() boolean
        +updateCompass(float deltaTime) void
        +resetForRetry() void
        +boostLantern(int snaps) void
        +isLanternBoosted() boolean
        +consumeBoostedSnap() boolean
    }
    class Direction {
        <<enumeration>>
        NONE
        UP
        UP_RIGHT
        RIGHT
        DOWN_RIGHT
        DOWN
        DOWN_LEFT
        LEFT
        UP_LEFT
        -int dx
        -int dy
        -float normalizedX
        -float normalizedY
        +getDx() int
        +getDy() int
        +getNormalizedX() float
        +getNormalizedY() float
        +fromKeys(boolean up, boolean down, boolean left, boolean right)$ Direction
    }
    class Gnome {
        +float HITBOX_SIZE = 1f$
        +Gnome()
    }
    class Movable {
        <<interface>>
        +move(float directionX, float directionY, float deltaTime)* void
    }
    %% ===== Classes d'autres packages (detail dans leur propre fichier) =====
    class Collidable {
        <<interface>>
    }
    class CompassEffect
    class Inventory~T~
    %% ===== Relations =====
    Movable <|.. Character
    Collidable <|.. Character
    Character --> "1" Inventory : inventory
    Character --> "1" CompassEffect : compassEffect
    Character --> "1" Direction : facing
    Character <|-- Gnome
    style Collidable fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style CompassEffect fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
    style Inventory fill:#eeeeee,stroke:#999999,stroke-dasharray:4 3
```

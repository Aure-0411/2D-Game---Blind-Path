# UML : Lumière et claquement de doigts

Package `com.blindpath.domain.light`. La lanterne (`Light`) et le claquement (`SnapEvent`), diffusé aux `SnapListener` (patron **Observer**).

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.domain.light =====
    class Light {
        +float DEFAULT_RADIUS = 4.5f$
        +float DEFAULT_REVEAL_DURATION = 1f$
        +float DEFAULT_COOLDOWN = 1.5f$
        +float BOOST_MULTIPLIER = 1.5f$
        -float baseRadius
        -float revealDuration
        -float cooldown
        -int bonusPercent
        -boolean boosted
        -GameTimer revealTimer
        -GameTimer cooldownTimer
        -float originX
        -float originY
        -float snapX
        -float snapY
        -float heroCenterX
        -float heroCenterY
        +Light(float baseRadius, float revealDuration, float cooldown)
        +Light()
        +setOrigin(float x, float y) void
        +snap() boolean
        +update(float deltaTime) void
        +isRevealing() boolean
        +isOnCooldown() boolean
        +getRevealRadius() float
        +setBoosted(boolean boosted) void
        +getBonusPercent() int
        +getSnapX() float
        +getSnapY() float
        +addBonusPercent(int percent) void
        +getRevealProgress() float
        +getCurrentRadius() float
        +getPermanentVisibilityRadius() float
        +updateHeroPosition(float centerX, float centerY) void
        +isVisible(float worldX, float worldY) boolean
        +getHeroCenterX() float
        +getHeroCenterY() float
        +getCooldownProgress() float
    }
    class Revealable {
        <<interface>>
        +snap()* boolean
    }
    class SnapEvent {
        -float sourceX
        -float sourceY
        -SnapSource source
        -long groupId
        +SnapEvent(float sourceX, float sourceY, SnapSource source, long groupId)
        +getSourceX() float
        +getSourceY() float
        +getSource() SnapSource
        +getGroupId() long
    }
    class SnapListener {
        <<interface>>
        +onSnap(SnapEvent event)* void
    }
    class SnapSource {
        <<enumeration>>
        HERO
        DYNAMITE
        DASH
        DOUBLE_SNAP
    }
    %% ===== Relations =====
    Revealable <|.. Light
    SnapEvent --> "1" SnapSource : source
```

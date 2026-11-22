# UML : Temps : horloge et minuteurs

Package `com.blindpath.domain.time`. `GameClock` émet 30 ticks par seconde vers les `TickListener` (patron **Observer**) ; `GameTimer` sert aux durées (stun, immobilisation, cri…).

Les classes grisées en pointillés viennent d'autres packages : leur détail est dans leur propre fichier.

```mermaid
classDiagram
    direction TB
    %% ===== com.blindpath.domain.time =====
    class GameClock {
        +int TICKS_PER_SECOND = 30$
        +float TICK_DURATION = 1f / TICKS_PER_SECOND$
        +float MAX_DELTA_TIME = 0.25f$
        -List~TickListener~ listeners
        -long tickCount
        -long accumulatedMicroTicks
        +update(float deltaTime) int
        +addListener(TickListener listener) void
        +removeListener(TickListener listener) void
        +getTickCount() long
        +secondsToTicks(float seconds)$ int
    }
    class GameTimer {
        -long durationMicros
        -long remainingMicros
        +start(float duration) void
        +stop() void
        +update(float deltaTime) boolean
        +isRunning() boolean
        +getRemaining() float
        +getProgress() float
    }
    class TickListener {
        <<interface>>
        +onTick(long tick)* void
    }
    class TimeUnits {
        ~long MICROS_PER_SECOND = 1000000L$
        ~toMicros(float seconds)$ long
        ~toSeconds(long micros)$ float
    }
    %% ===== Relations =====
    GameClock --> "*" TickListener : listeners
```

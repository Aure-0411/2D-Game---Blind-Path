package com.blindpath.domain.time;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
 
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
class GameClockTest {
 
    private static final float FRAME = 1f / 60; // one frame at 60 fps
 
    private GameClock clock;
    private List<Long> received;
 
    @BeforeEach
    void setUp() {
        clock = new GameClock();
        received = new ArrayList<>();
        clock.addListener(received::add);
    }
 
    private void runFrames(int frames) {
        for (int i = 0; i < frames; i++) {
            clock.update(FRAME);
        }
    }
 
    @Test
    void oneSecondAtSixtyFpsGivesTwentyTicks() {
        runFrames(60);
        assertEquals(20, clock.getTickCount(), "60 frames of 1/60 s should give exactly 20 ticks");
        assertEquals(20, received.size(), "the listener should receive every tick");
    }
 
    @Test
    void tenSecondsGiveTwoHundredTicks() {
        runFrames(600);
        assertEquals(200, clock.getTickCount(), "no drift over 10 s");
    }
 
    @Test
    void noTickBeforeAFullTick() {
        assertEquals(0, clock.update(0.049f), "no tick before 0.05 s");
        assertEquals(1, clock.update(0.001f), "one tick once 0.05 s is reached");
    }
 
    @Test
    void ticksAreNumberedFromOne() {
        clock.update(0.15f);
        assertEquals(List.of(1L, 2L, 3L), received, "ticks 1, 2 and 3 in order");
    }
 
    @Test
    void longFrameIsClamped() {
        assertEquals(5, clock.update(10f), "a frame counts for 0.25 s at most, i.e. 5 ticks");
    }
 
    @Test
    void removedListenerIsNoLongerNotified() {
        List<Long> other = new ArrayList<>();
        TickListener listener = other::add;
        clock.addListener(listener);
        clock.update(GameClock.TICK_DURATION);
        clock.removeListener(listener);
        clock.update(GameClock.TICK_DURATION);
        assertEquals(List.of(1L), other, "only the tick before removal is received");
    }
 
    @Test
    void listenerCanUnsubscribeDuringATick() {
        List<Long> once = new ArrayList<>();
        clock.addListener(new TickListener() {
            @Override
            public void onTick(long tick) {
                once.add(tick);
                clock.removeListener(this);
            }
        });
        clock.update(0.1f);
        assertEquals(List.of(1L), once, "a listener removed during a tick gets no more ticks");
    }
 
    @Test
    void sameListenerAddedTwiceIsNotifiedOnce() {
        List<Long> calls = new ArrayList<>();
        TickListener listener = calls::add;
        clock.addListener(listener);
        clock.addListener(listener);
        clock.update(GameClock.TICK_DURATION);
        assertEquals(1, calls.size(), "no duplicate notification");
    }
 
    @Test
    void secondsToTicksUsesTwentyTicksPerSecond() {
        assertEquals(20, GameClock.secondsToTicks(1f), "1 s = 20 ticks");
        assertEquals(10, GameClock.secondsToTicks(0.5f), "0.5 s = 10 ticks");
    }
 
    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> clock.update(-0.1f));
        assertThrows(IllegalArgumentException.class, () -> clock.addListener(null));
    }
}
 
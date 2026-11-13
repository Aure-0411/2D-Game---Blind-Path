package com.blindpath.domain.time;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
 
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
/**
 * Tests of {@link GameClock}. Expected values are computed from
 * {@link GameClock#TICKS_PER_SECOND}, so the tests stay valid if the tick rate changes.
 */
class GameClockTest {
 
    private static final int TPS = GameClock.TICKS_PER_SECOND;
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
    void oneSecondAtSixtyFpsGivesTicksPerSecond() {
        runFrames(60);
        assertEquals(TPS, clock.getTickCount(), "60 frames of 1/60 s should give exactly " + TPS + " ticks");
        assertEquals(TPS, received.size(), "the listener should receive every tick");
    }
 
    @Test
    void tenSecondsGiveNoDrift() {
        runFrames(600);
        assertEquals(10L * TPS, clock.getTickCount(), "no drift over 10 s");
    }
 
    @Test
    void oneHourGivesNoDrift() {
        for (int i = 0; i < 3600 * 4; i++) {
            clock.update(0.25f);
        }
        assertEquals(3600L * TPS, clock.getTickCount(), "no drift over 1 hour, even if 1 s / TPS is not a whole number of microseconds");
    }
 
    @Test
    void noTickBeforeAFullTick() {
        assertEquals(0, clock.update(GameClock.TICK_DURATION * 0.9f), "no tick before one tick duration");
        assertEquals(1, clock.update(GameClock.TICK_DURATION * 0.2f), "one tick once a full tick duration is reached");
    }
 
    @Test
    void ticksAreNumberedFromOne() {
        clock.update(GameClock.TICK_DURATION * 3.5f);
        assertEquals(List.of(1L, 2L, 3L), received, "ticks 1, 2 and 3 in order");
    }
 
    @Test
    void longFrameIsClamped() {
        int maxTicks = (int) (GameClock.MAX_DELTA_TIME * TPS);
        assertEquals(maxTicks, clock.update(10f), "a frame counts for MAX_DELTA_TIME at most, i.e. " + maxTicks + " ticks");
    }
 
    @Test
    void removedListenerIsNoLongerNotified() {
        List<Long> other = new ArrayList<>();
        TickListener listener = other::add;
        clock.addListener(listener);
        clock.update(GameClock.TICK_DURATION * 1.5f);
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
        clock.update(GameClock.TICK_DURATION * 2.5f);
        assertEquals(List.of(1L), once, "a listener removed during a tick gets no more ticks");
    }
 
    @Test
    void sameListenerAddedTwiceIsNotifiedOnce() {
        List<Long> calls = new ArrayList<>();
        TickListener listener = calls::add;
        clock.addListener(listener);
        clock.addListener(listener);
        clock.update(GameClock.TICK_DURATION * 1.5f);
        assertEquals(1, calls.size(), "no duplicate notification");
    }
 
    @Test
    void secondsToTicksUsesTicksPerSecond() {
        assertEquals(TPS, GameClock.secondsToTicks(1f), "1 s = " + TPS + " ticks");
        assertEquals(2 * TPS, GameClock.secondsToTicks(2f), "2 s = " + 2 * TPS + " ticks");
        assertEquals(TPS / 2, GameClock.secondsToTicks(0.5f), "0.5 s = " + TPS / 2 + " ticks");
    }
 
    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> clock.update(-0.1f), "negative time is rejected");
        assertThrows(IllegalArgumentException.class, () -> clock.addListener(null), "null listener is rejected");
    }
}
 
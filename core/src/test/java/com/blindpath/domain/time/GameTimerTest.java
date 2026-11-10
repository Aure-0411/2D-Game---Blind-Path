package com.blindpath.domain.time;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
 
import org.junit.jupiter.api.Test;
 
class GameTimerTest {
 
    private static final float FRAME = 1f / 60;
 
    @Test
    void newTimerIsNotRunning() {
        GameTimer timer = new GameTimer();
        assertFalse(timer.isRunning(), "a new timer is stopped (a cooldown is ready)");
        assertEquals(0f, timer.getRemaining(), "nothing remains");
    }
 
    @Test
    void immunityLastsExactlyTwoSeconds() {
        GameTimer immunity = new GameTimer();
        immunity.start(2f);
        for (int i = 0; i < 119; i++) {
            immunity.update(FRAME);
        }
        assertTrue(immunity.isRunning(), "still immune after 119 frames");
        assertTrue(immunity.update(FRAME), "immunity ends on the 120th frame (2 s)");
        assertFalse(immunity.isRunning(), "not immune any more");
    }
 
    @Test
    void updateReturnsTrueOnlyOnExpiry() {
        GameTimer timer = new GameTimer();
        timer.start(0.1f);
        assertFalse(timer.update(0.05f), "not finished halfway");
        assertTrue(timer.update(0.05f), "finished now");
        assertFalse(timer.update(0.05f), "already finished");
    }
 
    @Test
    void remainingNeverGoesBelowZero() {
        GameTimer timer = new GameTimer();
        timer.start(1f);
        timer.update(5f);
        assertEquals(0f, timer.getRemaining(), "remaining time stops at zero");
    }
 
    @Test
    void restartResetsTheCountdown() {
        GameTimer stun = new GameTimer();
        stun.start(1f);
        stun.update(0.75f);
        stun.start(3f);
        assertEquals(3f, stun.getRemaining(), 1e-6, "a new start replaces the old countdown");
    }
 
    @Test
    void progressGoesFromZeroToOne() {
        GameTimer cooldown = new GameTimer();
        cooldown.start(2f);
        assertEquals(0f, cooldown.getProgress(), 1e-6, "0 when just started");
        cooldown.update(0.5f);
        assertEquals(0.25f, cooldown.getProgress(), 1e-6, "a quarter done after 0.5 s of 2 s");
        cooldown.update(1.5f);
        assertEquals(1f, cooldown.getProgress(), 1e-6, "1 when finished");
    }
 
    @Test
    void stopEndsTheCountdown() {
        GameTimer timer = new GameTimer();
        timer.start(5f);
        timer.stop();
        assertFalse(timer.isRunning(), "stopped timer is not running");
    }
 
    @Test
    void negativeValuesAreRejected() {
        GameTimer timer = new GameTimer();
        assertThrows(IllegalArgumentException.class, () -> timer.start(-1f));
        assertThrows(IllegalArgumentException.class, () -> timer.update(-0.1f));
    }
}
 
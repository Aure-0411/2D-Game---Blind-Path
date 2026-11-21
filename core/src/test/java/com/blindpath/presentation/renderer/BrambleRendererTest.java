package com.blindpath.presentation.renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.blindpath.domain.labyrinth.Bramble;
import com.blindpath.domain.labyrinth.OpeningOrientation;

class BrambleRendererTest {
    @Test
    void horizontalImageExtendsAboveAndBelowGroundArea() {
        Bramble bramble = new Bramble(4, 4, OpeningOrientation.HORIZONTAL);
        var rest = BrambleRenderer.computeDrawSpec(bramble);
        assertEquals(4f, rest.x());
        assertEquals(4f, rest.y());
        assertEquals(2f, rest.width());
        assertEquals(1f, rest.height());
        bramble.activate();
        var active = BrambleRenderer.computeDrawSpec(bramble);
        assertEquals(4f, active.x());
        assertEquals(3f, active.y());
        assertEquals(3f, active.height());
        assertEquals(0f, active.rotation());
        bramble.update(0.5f);
        assertEquals(rest, BrambleRenderer.computeDrawSpec(bramble));
    }

    @Test
    void verticalImageRotatesAroundGroundCenter() {
        Bramble bramble = new Bramble(4, 4, OpeningOrientation.VERTICAL);
        bramble.activate();
        var active = BrambleRenderer.computeDrawSpec(bramble);
        assertEquals(90f, active.rotation());
        assertEquals(4.5f, active.x() + active.width() / 2f);
        assertEquals(5f, active.y() + active.height() / 2f);
        assertEquals(2f, active.width());
        assertEquals(3f, active.height());
    }
}

package com.blindpath.presentation.renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.labyrinth.RotatingWall;

class LeverWallRendererTest {
    @Test
    void imageUsesSameCenterAndAngleAsRotatedWall() {
        RotatingWall wall = new RotatingWall(6, 21, OpeningOrientation.VERTICAL, 7, 23, 90);
        var initial = LeverWallRenderer.computeDrawSpec(wall);
        assertEquals(90f, initial.rotation());
        assertEquals(6.5f, initial.x() + 1f);
        assertEquals(22f, initial.y() + 0.5f);
        wall.rotate();
        var rotated = LeverWallRenderer.computeDrawSpec(wall);
        assertEquals(180f, rotated.rotation());
        assertEquals(wall.getBounds().getCenterX(), rotated.x() + 1f);
        assertEquals(wall.getBounds().getCenterY(), rotated.y() + 0.5f);
    }
}

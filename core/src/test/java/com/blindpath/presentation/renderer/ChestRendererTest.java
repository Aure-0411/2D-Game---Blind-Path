package com.blindpath.presentation.renderer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.blindpath.domain.chest.Chest;
import com.blindpath.domain.chest.LootResult;
import com.blindpath.domain.inventory.Heart;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.presentation.renderer.ChestRenderer.DrawSpec;

class ChestRendererTest {

    @Test
    void horizontalChest_keepsTwoByOneDimensionsAndRotates() {
        Chest bottom = new Chest(2f, 3f, Border.BOTTOM, new LootResult(new Heart(), 1));
        Chest top = new Chest(2f, 3f, Border.TOP, new LootResult(new Heart(), 1));

        DrawSpec bottomSpec = ChestRenderer.computeDrawSpec(bottom);
        DrawSpec topSpec = ChestRenderer.computeDrawSpec(top);

        assertAll(
                () -> assertEquals(0f, bottomSpec.rotation()),
                () -> assertEquals(2f, bottomSpec.width()),
                () -> assertEquals(1f, bottomSpec.height()),
                () -> assertEquals(2f, bottomSpec.x()),
                () -> assertEquals(3f, bottomSpec.y()),
                () -> assertEquals(180f, topSpec.rotation()));
    }

    @Test
    void verticalChest_swapsDimensionsBeforeRotationAndCentersOnBounds() {
        // Coffre RIGHT en (1, 14) : hitbox 1x2 -> avant rotation 90°, drawW = 2f, drawH = 1f centré en (0.5f, 14.5f)
        Chest right = new Chest(1f, 14f, Border.RIGHT, new LootResult(new Heart(), 1));
        Chest left = new Chest(1f, 14f, Border.LEFT, new LootResult(new Heart(), 1));

        DrawSpec rightSpec = ChestRenderer.computeDrawSpec(right);
        DrawSpec leftSpec = ChestRenderer.computeDrawSpec(left);

        assertAll(
                () -> assertEquals(90f, rightSpec.rotation()),
                () -> assertEquals(2f, rightSpec.width()),
                () -> assertEquals(1f, rightSpec.height()),
                () -> assertEquals(0.5f, rightSpec.x()),
                () -> assertEquals(14.5f, rightSpec.y()),
                () -> assertEquals(270f, leftSpec.rotation()));
    }
}
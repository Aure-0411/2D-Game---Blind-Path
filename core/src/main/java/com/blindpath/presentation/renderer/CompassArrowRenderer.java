package com.blindpath.presentation.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Exit;

public class CompassArrowRenderer implements Disposable {

    private static final float ORBIT_RADIUS = 1.1f;
    private static final float ARROW_LENGTH = 0.45f;
    private static final float ARROW_HALF_WIDTH = 0.22f;

    private final ShapeRenderer shapes = new ShapeRenderer();

    public void render(OrthographicCamera camera, Character hero, Exit exit) {
        if (hero == null || exit == null || !hero.isCompassActive()) {
            return;
        }

        float angleDeg = hero.getCompassEffect().computeAngleDegrees(hero, exit);
        float angleRad = (float) Math.toRadians(angleDeg);

        float centerX = hero.getX() + hero.getWidth() / 2f;
        float centerY = hero.getY() + hero.getHeight() / 2f;

        float tipX = centerX + (float) Math.cos(angleRad) * (ORBIT_RADIUS + ARROW_LENGTH);
        float tipY = centerY + (float) Math.sin(angleRad) * (ORBIT_RADIUS + ARROW_LENGTH);

        float baseX = centerX + (float) Math.cos(angleRad) * ORBIT_RADIUS;
        float baseY = centerY + (float) Math.sin(angleRad) * ORBIT_RADIUS;

        float perpX = (float) -Math.sin(angleRad) * ARROW_HALF_WIDTH;
        float perpY = (float) Math.cos(angleRad) * ARROW_HALF_WIDTH;

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.GOLD);
        shapes.triangle(
                tipX, tipY,
                baseX + perpX, baseY + perpY,
                baseX - perpX, baseY - perpY
        );
        shapes.end();
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }
}
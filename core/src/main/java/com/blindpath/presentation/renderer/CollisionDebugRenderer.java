package com.blindpath.presentation.renderer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.game.Level;

/** Affiche les hitbox (murs en rouge, gnome en vert) pour vérifier les collisions. Touche F3. */
public class CollisionDebugRenderer implements Disposable {

    private final ShapeRenderer shapes = new ShapeRenderer();

    public void render(Camera camera, Level level) {
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Line);

        shapes.setColor(Color.RED);
        for (Obstacle obstacle : level.getLabyrinth().getObstacles()) {
            draw(obstacle.getBounds());
        }
        shapes.setColor(Color.ORANGE);
        for (Obstacle wall : level.getLabyrinth().getBoundaryWalls()) {
            draw(wall.getBounds());
        }
        shapes.setColor(Color.GREEN);
        draw(level.getGnome().getBounds());

        shapes.end();
    }

    private void draw(Bounds b) {
        shapes.rect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }
}
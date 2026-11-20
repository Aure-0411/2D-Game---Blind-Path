package com.blindpath.presentation.renderer;

import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Lever;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.labyrinth.RotatingWall;

public final class LeverWallRenderer implements Disposable {
    private static final String DIRECTORY = "maps/tilesets/assets/murs_speciaux/";
    private final Texture wallTexture = new Texture(DIRECTORY + "mur_engrenages.png");
    private final Texture restingLever = new Texture(DIRECTORY + "levier_desactive.png");
    private final Texture activeLever = new Texture(DIRECTORY + "levier_active.png");

    record DrawSpec(float x, float y, float rotation) {}

    static DrawSpec computeDrawSpec(RotatingWall wall) {
        Bounds bounds = wall.getBounds();
        float angle = wall.getInitialOrientation() == OpeningOrientation.VERTICAL ? 90f : 0f;
        if (wall.isRotated()) angle += wall.getTurn();
        return new DrawSpec(bounds.getCenterX() - 1f, bounds.getCenterY() - 0.5f, angle);
    }

    public void render(SpriteBatch batch, List<RotatingWall> walls, List<Lever> levers) {
        for (RotatingWall wall : walls) {
            DrawSpec spec = computeDrawSpec(wall);
            batch.draw(wallTexture, spec.x(), spec.y(), 1f, 0.5f, 2f, 1f,
                    1f, 1f, spec.rotation(), 0, 0, wallTexture.getWidth(), wallTexture.getHeight(), false, false);
        }
        for (Lever lever : levers) {
            Bounds bounds = lever.getBounds();
            batch.draw(lever.isActivated() ? activeLever : restingLever,
                    bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        }
    }

    @Override
    public void dispose() {
        wallTexture.dispose();
        restingLever.dispose();
        activeLever.dispose();
    }
}

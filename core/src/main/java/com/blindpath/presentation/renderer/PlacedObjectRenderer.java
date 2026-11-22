package com.blindpath.presentation.renderer;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.object.PlacedBearTrap;
import com.blindpath.domain.object.PlacedDynamite;
import com.blindpath.domain.object.PlacedObject;

public class PlacedObjectRenderer implements Disposable {
    private final Texture dynamiteTexture;
    private final Texture bearTrapTexture;

    public PlacedObjectRenderer() {
        this.dynamiteTexture = new Texture(Gdx.files.internal("items/carte/dynamite.png"));
        this.bearTrapTexture = new Texture(Gdx.files.internal("items/carte/piege_a_ours.png"));
    }

    public void render(SpriteBatch batch, List<PlacedObject> placedObjects) {
        for (PlacedObject placedObject : placedObjects) {
            Texture placedObjectTexture = this.textureForPlacedObject(placedObject);
            if (placedObjectTexture == null) {
                continue;
            }
            Bounds placedObjectBounds = placedObject.getBounds();
            batch.draw(
                placedObjectTexture,
                placedObjectBounds.getX(),
                placedObjectBounds.getY(),
                placedObjectBounds.getWidth(),
                placedObjectBounds.getHeight()
            );
        }
    }

    private Texture textureForPlacedObject(PlacedObject placedObject) {
        if (placedObject instanceof PlacedDynamite) {
            return dynamiteTexture;
        }
        if (placedObject instanceof PlacedBearTrap) {
            return bearTrapTexture;
        }
        return null;
    }

    @Override
    public void dispose() {
        dynamiteTexture.dispose();
        bearTrapTexture.dispose();
    }
}
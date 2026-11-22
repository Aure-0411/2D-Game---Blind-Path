package com.blindpath.presentation.renderer;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.blindpath.domain.chest.Chest;
import com.blindpath.domain.labyrinth.Border;

/** Dessine les coffres, fermés ou ouverts, tournés selon le mur contre lequel ils sont posés. */
public class ChestRenderer implements Disposable {

    record DrawSpec(float x, float y, float width, float height, float rotation) {}

    private final Texture closedTexture;
    private final Texture openTexture;

    /** Charge les images du coffre fermé et du coffre ouvert. */
    public ChestRenderer() {
        this.closedTexture = new Texture(Gdx.files.internal("chest/chest_closed.png"));
        this.openTexture = new Texture(Gdx.files.internal("chest/chest_open.png"));
    }

    static DrawSpec computeDrawSpec(Chest chest) {
        float rotation = switch (chest.getOrientation()) {
            case BOTTOM -> 0f;
            case RIGHT  -> 90f;
            case TOP    -> 180f;
            case LEFT   -> 270f;
        };

        boolean vertical = (chest.getOrientation() == Border.LEFT || chest.getOrientation() == Border.RIGHT);
        float drawW = vertical ? chest.getHeight() : chest.getWidth();
        float drawH = vertical ? chest.getWidth() : chest.getHeight();
        float drawX = chest.getX() + (chest.getWidth() - drawW) / 2f;
        float drawY = chest.getY() + (chest.getHeight() - drawH) / 2f;

        return new DrawSpec(drawX, drawY, drawW, drawH, rotation);
    }

    /**
     * @param batch le batch de dessin, déjà ouvert
     * @param chests les coffres du niveau
     */
    public void render(SpriteBatch batch, List<Chest> chests) {
        for (Chest chest : chests) {
            Texture texture = chest.isOpened() ? openTexture : closedTexture;
            DrawSpec spec = computeDrawSpec(chest);

            batch.draw(
                    texture,
                    spec.x(), spec.y(),
                    spec.width() / 2f, spec.height() / 2f,
                    spec.width(), spec.height(),
                    1f, 1f,
                    spec.rotation(),
                    0, 0,
                    texture.getWidth(), texture.getHeight(),
                    false, false
            );
        }
    }

    /** Libère les images des coffres. */
    @Override
    public void dispose() {
        closedTexture.dispose();
        openTexture.dispose();
    }
}
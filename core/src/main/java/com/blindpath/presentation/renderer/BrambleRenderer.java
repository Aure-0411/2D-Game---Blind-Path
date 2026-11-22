package com.blindpath.presentation.renderer;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Bramble;
import com.blindpath.domain.labyrinth.OpeningOrientation;

/** Dessine les ronces : l'image au repos ou l'image étendue, sans transition progressive. */
/** Affiche directement l'image au repos ou étendue, sans transition progressive. */
public final class BrambleRenderer implements Disposable {

    private final Texture resting = new Texture("maps/tilesets/assets/murs_speciaux/ronces_03_repos.png");
    private final Texture extended = new Texture("maps/tilesets/assets/murs_speciaux/ronces_03_activees.png");

    record DrawSpec(float x, float y, float width, float height, float rotation) {}

    static DrawSpec computeDrawSpec(Bramble bramble) {
        Bounds ground = bramble.getBounds();
        float width = 2f;
        float height = 1f + 2f * bramble.getExtension();
        return new DrawSpec(ground.getCenterX() - width / 2f,
                ground.getCenterY() - height / 2f, width, height,
                bramble.getOrientation() == OpeningOrientation.VERTICAL ? 90f : 0f);
    }

    /**
     * Dessine toutes les ronces du niveau. Le batch reçu doit être ouvert avec la projection
     * de la caméra du jeu.
     *
     * @param batch le batch de dessin, déjà ouvert
     * @param brambles les ronces du niveau
     */
    public void render(SpriteBatch batch, List<Bramble> brambles) {
        for (Bramble bramble : brambles) {
            Texture texture = bramble.isActive() ? extended : resting;
            DrawSpec spec = computeDrawSpec(bramble);
            batch.draw(texture, spec.x(), spec.y(), spec.width() / 2f, spec.height() / 2f,
                    spec.width(), spec.height(), 1f, 1f, spec.rotation(),
                    0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
    }

    /** Libère les images des ronces. */
    @Override
    public void dispose() {
        resting.dispose();
        extended.dispose();
    }
}

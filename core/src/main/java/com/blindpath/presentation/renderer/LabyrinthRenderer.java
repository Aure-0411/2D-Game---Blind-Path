package com.blindpath.presentation.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/** Dessine la carte Tiled du niveau (250 pixels = une case). */
public class LabyrinthRenderer {

    private final OrthogonalTiledMapRenderer renderer;

    /** @param map la carte du niveau */
    public LabyrinthRenderer(TiledMap map) {
        this.renderer = new OrthogonalTiledMapRenderer(map, 1f / 250f);
    }

    /**
     * Dessine la partie de la carte vue par la caméra.
     *
     * @param camera la caméra du jeu
     */
    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    /** Libère l'outil de dessin de la carte (la carte elle-même n'est pas libérée). */
    public void dispose(){
        renderer.dispose();
    }

}



package com.blindpath.presentation.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class LabyrinthRenderer {

    private final OrthogonalTiledMapRenderer renderer;

    public LabyrinthRenderer(TiledMap map) {
        this.renderer = new OrthogonalTiledMapRenderer(map, 1f / 250f);
    }

    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public void dispose(){
        renderer.dispose();
    }

}



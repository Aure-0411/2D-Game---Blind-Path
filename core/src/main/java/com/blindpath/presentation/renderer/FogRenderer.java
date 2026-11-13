package com.blindpath.presentation.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class FogRenderer {

    private final SpriteBatch batch;
    private final Texture texture;
    private final ShaderProgram shader;

    public FogRenderer() {
        this.batch = new SpriteBatch();
        this.texture = new Texture("animations/black_fog/noir_marbrures_000.png");
        this.shader = new ShaderProgram(
                Gdx.files.internal("shaders/fog.vert"),
                Gdx.files.internal("shaders/fog.frag")
        );
        if (!shader.isCompiled()) {
            throw new IllegalStateException(shader.getLog());
        }
        batch.setShader(shader);
    }

    public void render(OrthographicCamera camera, int cellX, int cellY) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        shader.setUniformf("u_visibleCell", (float) cellX, (float) cellY);
        shader.setUniformf("u_hasVisibleCell", 1f);
        batch.draw(texture, 0, 0, 30, 30);
        batch.end();

    }

    public void dispose() {
        batch.dispose();
        texture.dispose();
        shader.dispose();
    }
}

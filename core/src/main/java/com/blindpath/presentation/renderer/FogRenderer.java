package com.blindpath.presentation.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Dessine le brouillard noir animé qui cache le labyrinthe. Un shader y découpe un petit cercle
 * toujours visible autour du héros, et, pendant un claquement, le cercle animé de la lanterne.
 */
public class FogRenderer {

    private final SpriteBatch batch;

    private final ShaderProgram shader;
    private final Texture[] snapFrames;

    private final Texture[] fogFrames;
    private float fogTime = 0f;

    /**
     * Charge les images du brouillard et du cercle de claquement, et compile le shader.
     *
     * @throws IllegalStateException si le shader ne compile pas
     */
    public FogRenderer() {
        this.batch = new SpriteBatch();

        this.fogFrames = new Texture[120];
        for (int i = 0; i < 120; i++) {
            String path = String.format(
                    java.util.Locale.ROOT,
                    "animations/black_fog/noir_marbrures_%03d.png",
                    i);
            fogFrames[i] = new Texture(path);
        }

        this.shader = new ShaderProgram(
                Gdx.files.internal("shaders/fog.vert"),
                Gdx.files.internal("shaders/fog.frag")
        );
        if (!shader.isCompiled()) {
            throw new IllegalStateException(shader.getLog());
        }
        batch.setShader(shader);

        // Charge une seule fois les 15 images du masque Circle.
        this.snapFrames = new Texture[15];
        for (int i = 0; i < snapFrames.length; i++) {
            String path = String.format(
                    java.util.Locale.ROOT,
                    "animations/snap/circle/Snap_Circle_%04d.png",
                    i);
            snapFrames[i] = new Texture(path);
        }
    }

    /**
     * Dessine le brouillard par-dessus le labyrinthe.
     *
     * @param camera la caméra du jeu
     * @param centerX position horizontale du centre de la zone toujours visible
     * @param centerY position verticale du centre de la zone toujours visible
     * @param radius rayon de la zone toujours visible
     * @param revealing {@code true} pendant un claquement
     * @param snapX position horizontale du claquement
     * @param snapY position verticale du claquement
     * @param snapProgress avancement du claquement, de 0 à 1
     * @param snapRadius rayon révélé par le claquement
     * @param delta temps écoulé depuis la dernière image, en secondes
     */
    public void render(
            OrthographicCamera camera,
            float centerX, float centerY, float radius,
            boolean revealing,
            float snapX, float snapY,
            float snapProgress,
            float snapRadius,
            float delta) {

        fogTime += delta;
        int fogFrameIndex = (int) (fogTime * 24f) % fogFrames.length;

        if (revealing) {
            int frameIndex = Math.min(
                    snapFrames.length - 1,
                    (int) (snapProgress * snapFrames.length)
            );

            snapFrames[frameIndex].bind(1);
        }
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        shader.setUniformi("u_snapMask", 1);
        shader.setUniformf("u_snapActive", revealing ? 1f : 0f);
        shader.setUniformf("u_snapCenter", snapX, snapY);

        shader.setUniformf("u_visibleCenter", centerX, centerY);
        shader.setUniformf("u_visibleRadius", radius);
        shader.setUniformf("u_hasVisibleArea", 1f);

        shader.setUniformf("u_snapRadius", snapRadius);

        batch.draw(fogFrames[fogFrameIndex], 0, 0, 30, 30);
        batch.end();

    }

    /** Libère les images et le shader. */
    public void dispose() {
        // Chaque texture chargée doit être libérée à la fermeture de l'écran.
        for (Texture frame : snapFrames) {
            frame.dispose();
        }
        batch.dispose();

        for (Texture frame : fogFrames) {
            frame.dispose();
        }

        shader.dispose();
    }
}

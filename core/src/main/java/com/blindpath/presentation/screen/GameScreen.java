package com.blindpath.presentation.screen;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blindpath.domain.character.Direction;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.physics.CollisionWorld;
import com.blindpath.infrastructure.map.TiledLabyrinthLoader;
import com.blindpath.infrastructure.physics.Box2DCollisionWorld;
import com.blindpath.presentation.input.InputController;
import com.blindpath.presentation.renderer.CharacterRenderer;
import com.blindpath.presentation.renderer.CollisionDebugRenderer;
import com.blindpath.presentation.renderer.GnomeSprites;
import com.blindpath.presentation.renderer.LabyrinthRenderer;


public class GameScreen implements Screen {

    // Une unité du monde correspond à une case : la carte fait 30 x 30 cases.
    private static final float WORLD_WIDTH = 30f;
    private static final float WORLD_HEIGHT = 30f;
    // Le chemin est relatif au dossier assets, sans le préfixe "assets/".
    private static final String MAP_PATH = "maps/lvl0/Blind_Path_30x30.tmx";

    private TiledMap map;
    private LabyrinthRenderer renderer;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private Labyrinth labyrinth;

    private Level level;
    private CollisionWorld collisions;
    private InputController input;
    private SpriteBatch batch;
    private CharacterRenderer<Gnome> characterRenderer;
    private CollisionDebugRenderer debugRenderer;
    private boolean debugCollisions = false;
    private float stateTime;

    @Override
    public void show() {
        // show() est appelée quand l'écran devient actif. Ce contrôle évite de recharger les ressources si la même instance est réaffichée.
        if (map == null) {
            // Chargement provisoire ici ; il rejoindra ensuite le chargeur dédié.
            map = new TmxMapLoader().load(MAP_PATH);
            TiledLabyrinthLoader loader = new TiledLabyrinthLoader();
            List<Obstacle> obstacles = loader.loadObstacles(map);
            Entrance entrance = loader.loadEntrance(map);
            Exit exit = loader.loadExit(map);
            // Conserve les objets métier du niveau indépendamment de son rendu Tiled.
            // Les dimensions Tiled sont en cases, comme celles du modèle métier.
            int mapWidth = map.getProperties().get("width", Integer.class);
            int mapHeight = map.getProperties().get("height", Integer.class);
            this.labyrinth = new Labyrinth(mapWidth, mapHeight, entrance, exit, obstacles);
            renderer = new LabyrinthRenderer(map);

            // Le gnome démarre au centre de l'entrée ; Box2D reste caché derrière CollisionWorld.
            Gnome gnome = new Gnome();
            gnome.setPosition(
                    entrance.getX() + (entrance.getWidth() - gnome.getWidth()) / 2f,
                    entrance.getY() + (entrance.getHeight() - gnome.getHeight()) / 2f);
            collisions = new Box2DCollisionWorld();
            level = new Level(gnome, labyrinth, collisions);
            input = new InputController(level);
            batch = new SpriteBatch();
            characterRenderer = new CharacterRenderer<>(
                    GnomeSprites.ATLAS_PATH, GnomeSprites::regionName, Direction.DOWN, 0.15f);
            debugRenderer = new CollisionDebugRenderer();

            camera = new OrthographicCamera();
            // FitViewport conserve les proportions et peut ajouter des bandes noires.
            viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        }

        // true centre la caméra dans le monde, ici en (15, 15).
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void render(float delta) {
        // 1. Entrées + logique (déplacement avec collisions)
        input.handleInput(delta);
        level.update(delta);
        stateTime += delta;
        if (Gdx.input.isKeyJustPressed(Keys.F3)) {
            debugCollisions = !debugCollisions;
        }

        // 2. Dessin
        // Efface l'image précédente, y compris les bandes autour du viewport.
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        // Sélectionne la zone de la fenêtre dans laquelle dessiner la carte.
        viewport.apply();
        camera.update();
        renderer.render(camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        characterRenderer.render(batch, level.getGnome(), stateTime, input.isMoving());
        batch.end();

        if (debugCollisions) {
            debugRenderer.render(camera, level);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Adapte l'affichage à la fenêtre sans étirer la carte.
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }

    @Override
    public void pause() {
        // Aucune logique à suspendre pour ce premier affichage.
    }

    @Override
    public void resume() {
        // Aucune logique à reprendre pour le moment.
    }

    @Override
    public void hide() {
        // Masquer l'écran ne le détruit pas : il pourra être réaffiché.
    }

    @Override
    public void dispose() {
        // GameScreen possède ces ressources et les libère définitivement.
        // Le renderer ne libère pas la carte reçue : les deux appels sont nécessaires.
        if (renderer != null) {
            renderer.dispose();
            renderer = null;
        }
        if (characterRenderer != null) { characterRenderer.dispose(); characterRenderer = null; }
        if (debugRenderer != null) { debugRenderer.dispose(); debugRenderer = null; }
        if (batch != null) { batch.dispose(); batch = null; }
        if (collisions != null) { collisions.dispose(); collisions = null; }
        if (map != null) {
            map.dispose();
            map = null;
        }
    }
}
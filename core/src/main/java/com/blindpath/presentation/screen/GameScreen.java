package com.blindpath.presentation.screen;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.blindpath.Main;
import com.blindpath.domain.character.Direction;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.light.Light;
import com.blindpath.domain.physics.CollisionWorld;
import com.blindpath.infrastructure.map.TiledLabyrinthLoader;
import com.blindpath.infrastructure.physics.Box2DCollisionWorld;
import com.blindpath.presentation.input.InputController;
import com.blindpath.presentation.renderer.CharacterRenderer;
import com.blindpath.presentation.renderer.CollisionDebugRenderer;
import com.blindpath.presentation.renderer.FogRenderer;
import com.blindpath.presentation.renderer.GnomeSprites;
import com.blindpath.presentation.renderer.LabyrinthRenderer;

//monster
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.monster.Jumper;
import com.blindpath.domain.time.GameClock;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.infrastructure.pathfinding.GdxAiPathfinder;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.presentation.renderer.MonsterRenderer;
import com.blindpath.presentation.renderer.JumpscareRenderer;
import com.blindpath.presentation.renderer.DamageOverlayRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
    private FogRenderer fogRenderer;
    private final Light light = new Light();

    private Level level;
    private CollisionWorld collisions;
    private InputController input;
    private SpriteBatch batch;
    private CharacterRenderer<Gnome> characterRenderer;
    private CollisionDebugRenderer debugRenderer;
    private boolean debugCollisions = false;
    private boolean monsterVisible = false;
    private float stateTime;

    //monster (juste Jumper pour le moment)
    private Monster monster;
    private MonsterRenderer monsterRenderer;
    private LabyrinthGrid monsterGrid;
    private final GameClock clock = new GameClock();
    private ShapeRenderer shapes;
    private JumpscareRenderer jumpscare;
    private DamageOverlayRenderer damageOverlay;

    private boolean paused = false;
    private final Main main;
    private Stage pauseStage;
    private Skin pauseSkin;

    public GameScreen(Main main) {
        this.main = main;

    }

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
            // Grille du monstre : il occupe un bloc Monster.SIZE x Monster.SIZE,
            // donc une case n'est praticable que si tout le bloc évite les murs.
            monsterGrid = new LabyrinthGrid(labyrinth, Monster.SIZE);
            renderer = new LabyrinthRenderer(map);
            fogRenderer = new FogRenderer();

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
            shapes = new ShapeRenderer();
            monster = new Jumper(monsterGrid, new GridPosition((int) exit.getX(), (int) exit.getY()), new GdxAiPathfinder());
            level.setMonster(monster);
            clock.addListener(monster);
            monsterRenderer = new MonsterRenderer("jumper");
            jumpscare = new JumpscareRenderer("jumper");
            damageOverlay = new DamageOverlayRenderer();

            camera = new OrthographicCamera();
            // FitViewport conserve les proportions et peut ajouter des bandes noires.
            viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

            // pause //
            pauseStage = new Stage(new ScreenViewport());
            pauseSkin = new Skin();
            pauseSkin.add("default-font", new BitmapFont());
            pauseSkin.getFont("default-font").getData().setScale(3f);

            TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = pauseSkin.getFont("default-font");
            buttonStyle.fontColor = Color.WHITE;
            buttonStyle.overFontColor = Color.YELLOW;
            buttonStyle.downFontColor = Color.GRAY;

            pauseSkin.add("default", buttonStyle);

            Table pauseTable = new Table();
            pauseTable.setFillParent(true);

            TextButton menuButton = new TextButton("Retour au menu", pauseSkin);
            menuButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    main.setScreen(new MainMenuScreen(main));
                    GameScreen.this.dispose();
                }
            });

            TextButton quitButton = new TextButton("Quit", pauseSkin);
            quitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });

            pauseTable.add(menuButton).width(500).height(100).padBottom(30);
            pauseTable.row();
            pauseTable.add(quitButton).width(500).height(100);

            pauseStage.addActor(pauseTable);

        }

        // true centre la caméra dans le monde, ici en (15, 15).
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        jumpscare.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        damageOverlay.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {

        // set the pause key and effect//
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                Gdx.input.setInputProcessor(pauseStage);
            } else if (Gdx.input.getInputProcessor() == pauseStage) {
                Gdx.input.setInputProcessor(null);
            }
        }

        if (!paused) {
            // Les mises à jour du jeu.

            // 1. Entrées + logique (déplacement avec collisions)
            input.handleInput(delta);
            int lifeBefore = level.getGnome().getLife();
            level.update(delta);
            // Coup fatal (3e coup du monstre) : jumpscare ; sinon : contour rouge.
            if (level.getGnome().getLife() < lifeBefore) {
                if (level.getGnome().isDead()) {
                    jumpscare.trigger();
                } else {
                    damageOverlay.trigger();
                }
            }
            jumpscare.update(delta);
            damageOverlay.update(delta);
            // La règle de victoire appartient au modèle ; l'écran choisit l'affichage.
            if (level.isCompleted()) {
                main.setScreen(new EndScreen(main));
                dispose();
                return;
            }
            light.update(delta);
            // La position du modèle est le coin inférieur gauche : Light reçoit le centre.
            Gnome gnome = level.getGnome();
            light.updateHeroPosition(
                    gnome.getX() + gnome.getWidth() / 2f,
                    gnome.getY() + gnome.getHeight() / 2f);

            light.setOrigin(
                    light.getHeroCenterX(),
                    light.getHeroCenterY()
            );

            GridPosition gnomeCell = new GridPosition((int) Math.floor(light.getHeroCenterX()), (int) Math.floor(light.getHeroCenterY()));
            // Le monstre vise un bloc praticable qui recouvre la case du gnome.
            monster.setTarget(monsterGrid.anchorCovering(gnomeCell));
            clock.update(delta);
            monster.update(delta);

            if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                if (light.snap()) {
                    monster.onSnap(new SnapEvent(light.getHeroCenterX(), light.getHeroCenterY(), SnapSource.HERO, 0));
                }
            }

            stateTime += delta;
        }
        if (Gdx.input.isKeyJustPressed(Keys.F3)) {
            debugCollisions = !debugCollisions;
            monsterVisible = !monsterVisible;
        }

        
        if (Gdx.input.isKeyJustPressed(Keys.I)) level.getGnome().toggleInvincible();
        
        // 2. Dessin
        // Efface l'image précédente, y compris les bandes autour du viewport.
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        // Sélectionne la zone de la fenêtre dans laquelle dessiner la carte.
        viewport.apply();
        camera.update();
        renderer.render(camera);

        if (!monsterVisible) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            monsterRenderer.render(batch, monster, stateTime);
            batch.end();
        }
        // Dessine le voile avant le héros pour que celui-ci reste entièrement visible.
        fogRenderer.render(
                camera,
                light.getHeroCenterX(),
                light.getHeroCenterY(),
                light.getPermanentVisibilityRadius(),
                light.isRevealing(),
                light.getSnapX(),
                light.getSnapY(),
                light.getRevealProgress(),
                light.getRevealRadius(),
                paused ? 0f : delta
        );

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        characterRenderer.render(batch, level.getGnome(), stateTime, input.isMoving());
        batch.end();

        if (debugCollisions) {
            debugRenderer.render(camera, level);
        }

        if (monsterVisible) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            monsterRenderer.render(batch, monster, stateTime);
            batch.end();

            // On dessine la VRAIE hitbox (celle que Level utilise pour les dégâts),
            // pas une copie du calcul : le debug montre ce que le jeu utilise vraiment.
            Bounds hitbox = monster.getBounds();
            shapes.setProjectionMatrix(camera.combined);
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.setColor(Color.GREEN);
            shapes.rect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
            shapes.end();
        }

        damageOverlay.render(batch);
        jumpscare.render(batch);

        if (paused) {
            pauseStage.getViewport().apply();
            pauseStage.act(delta);
            pauseStage.draw();
        }

    }

    @Override
    public void resize(int width, int height) {
        // Adapte l'affichage à la fenêtre sans étirer la carte.
        if (viewport != null) {
            viewport.update(width, height, true);
        }
        if (pauseStage != null) {
            pauseStage.getViewport().update(width, height, true);
        }
        if (jumpscare != null) {
            jumpscare.resize(width, height);
        }
        if (damageOverlay != null) {
            damageOverlay.resize(width, height);
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
        if (Gdx.input.getInputProcessor() == pauseStage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        // GameScreen possède ces ressources et les libère définitivement.
        // Le renderer ne libère pas la carte reçue : les deux appels sont nécessaires.
        if (renderer != null) {
            renderer.dispose();
            renderer = null;
        }
        if (characterRenderer != null) {
            characterRenderer.dispose();
            characterRenderer = null;
        }
        if (debugRenderer != null) {
            debugRenderer.dispose();
            debugRenderer = null;
        }
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (collisions != null) {
            collisions.dispose();
            collisions = null;
        }
        if (map != null) {
            map.dispose();
            map = null;
        }
        if (fogRenderer != null) {
            fogRenderer.dispose();
            fogRenderer = null;
        }

        if (monsterRenderer != null) {
            monsterRenderer.dispose();
            monsterRenderer = null;
        }

        if (shapes != null) {
            shapes.dispose();
            shapes = null;
        }

        if (jumpscare != null) {
            jumpscare.dispose();
            jumpscare = null;
        }

        if (damageOverlay != null) {
            damageOverlay.dispose();
            damageOverlay = null;
        }

        if (pauseStage != null) {
            pauseStage.dispose();
            pauseStage = null;
        }

        if (pauseSkin != null) {
            pauseSkin.dispose();
            pauseSkin = null;
        }
    }
}

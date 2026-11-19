package com.blindpath.presentation.screen;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.blindpath.domain.chest.LootTable;
import com.blindpath.domain.game.GameSession;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.game.LevelConfig;
import com.blindpath.domain.game.LevelFactory;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.light.Light;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapSource;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;
import com.blindpath.domain.time.GameClock;
import com.blindpath.infrastructure.map.LevelRegistry;
import com.blindpath.infrastructure.map.TiledLabyrinthLoader;
import com.blindpath.presentation.input.InputController;
import com.blindpath.presentation.renderer.CharacterRenderer;
import com.blindpath.presentation.renderer.ChestRenderer;
import com.blindpath.presentation.renderer.CollisionDebugRenderer;
import com.blindpath.presentation.renderer.FogRenderer;
import com.blindpath.presentation.renderer.GnomeSprites;
import com.blindpath.presentation.renderer.LabyrinthRenderer;
import com.blindpath.presentation.renderer.MonsterRenderer;

public class GameScreen implements Screen {

    private static final float WORLD_WIDTH = 30f;
    private static final float WORLD_HEIGHT = 30f;

    private TiledMap map;
    private LabyrinthRenderer renderer;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private FogRenderer fogRenderer;
    private final Light light = new Light();

    private Level level;
    private LevelFactory factory;
    private InputController input;
    private SpriteBatch batch;
    private CharacterRenderer<Gnome> characterRenderer;
    private CollisionDebugRenderer debugRenderer;
    private boolean debugCollisions = false;
    private boolean monsterVisible = false;
    private float stateTime;
    private GameSession session;
    private ChestRenderer chestRenderer;
    private Monster monster;
    private MonsterRenderer monsterRenderer;
    private LabyrinthGrid monsterGrid;
    private final GameClock clock = new GameClock();
    private ShapeRenderer shapes;

    private boolean paused = false;
    private final Main main;
    private Stage pauseStage;
    private Skin pauseSkin;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        if (map == null) {
            LevelRegistry registry = new LevelRegistry();
            factory = new LevelFactory(registry, new TiledLabyrinthLoader(), new LootTable(), new Random());
            Gnome gnome = new Gnome();

            session = new GameSession(factory, gnome);
            session.startLevel(1);
            level = session.getLevel();

            Labyrinth labyrinth = level.getLabyrinth();
            Entrance entrance = labyrinth.getEntrance();
            gnome.setPosition(
                    entrance.getX() + (entrance.getWidth() - gnome.getWidth()) / 2f,
                    entrance.getY() + (entrance.getHeight() - gnome.getHeight()) / 2f);

            LevelConfig config = factory.configFor(session.getCurrentLevelNumber());
            map = new TmxMapLoader().load(config.mapPath());
            renderer = new LabyrinthRenderer(map);
            fogRenderer = new FogRenderer();

            monsterGrid = new LabyrinthGrid(labyrinth, Monster.SIZE);
            monster = level.getMonster();
            clock.addListener(monster);
            monsterRenderer = new MonsterRenderer(monster.getClass().getSimpleName().toLowerCase());

            chestRenderer = new ChestRenderer();

            input = new InputController(level);
            batch = new SpriteBatch();
            characterRenderer = new CharacterRenderer<>(
                    GnomeSprites.ATLAS_PATH, GnomeSprites::regionName, Direction.DOWN, 0.15f);
            debugRenderer = new CollisionDebugRenderer();
            shapes = new ShapeRenderer();

            camera = new OrthographicCamera();
            viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

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

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                Gdx.input.setInputProcessor(pauseStage);
            } else if (Gdx.input.getInputProcessor() == pauseStage) {
                Gdx.input.setInputProcessor(null);
            }
        }

        if (!paused) {
            input.handleInput(delta);
            level.update(delta);

            if (level.isCompleted()) {
                session.nextLevel();
                if (session.isWon()) {
                    main.setScreen(new EndScreen(main));
                    dispose();
                    return;
                }
                loadCurrentLevelResources();
                return;
            }

            if (level.getGnome().isDead()) {
                session.retry();
                loadCurrentLevelResources();
                return;
            }

            light.update(delta);
            Gnome gnome = level.getGnome();
            light.updateHeroPosition(
                    gnome.getX() + gnome.getWidth() / 2f,
                    gnome.getY() + gnome.getHeight() / 2f);

            light.setOrigin(
                    light.getHeroCenterX(),
                    light.getHeroCenterY());

            GridPosition gnomeCell = new GridPosition(
                    (int) Math.floor(light.getHeroCenterX()),
                    (int) Math.floor(light.getHeroCenterY()));
            monster.setTarget(monsterGrid.anchorCovering(gnomeCell));
            clock.update(delta);

            if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                if (light.snap()) {
                    level.emitSnap(new SnapEvent(light.getHeroCenterX(), light.getHeroCenterY(), SnapSource.HERO, 0));
                }
            }

            stateTime += delta;
        }

        if (Gdx.input.isKeyJustPressed(Keys.F3)) {
            debugCollisions = !debugCollisions;
            monsterVisible = !monsterVisible;
        }

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        viewport.apply();
        camera.update();
        renderer.render(camera);

        // 1. Avant le brouillard (mode normal)
        if (!monsterVisible || !debugCollisions) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            if (!debugCollisions) {
                chestRenderer.render(batch, level.getChests());
            }
            if (!monsterVisible) {
                monsterRenderer.render(batch, monster, stateTime);
            }
            batch.end();
        }

        // 2. Brouillard
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
                paused ? 0f : delta);

        // 3. Après le brouillard (gnome, coffres si F3 actif, monstre si F3 actif)
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (debugCollisions) {
            chestRenderer.render(batch, level.getChests());
        }
        characterRenderer.render(batch, level.getGnome(), stateTime, input.isMoving());
        if (monsterVisible) {
        monsterRenderer.render(batch, monster, stateTime);
        }
        batch.end();
        if (debugCollisions) {
        debugRenderer.render(camera, level);
        }
        if (monsterVisible) {
        Bounds hitbox = monster.getBounds();
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(Color.GREEN);
        shapes.rect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
        shapes.end();
        }
        if (paused) {
        pauseStage.getViewport().apply();
        pauseStage.act(delta);
        pauseStage.draw();
        }
    }

    private void loadCurrentLevelResources() {
        if (renderer != null) renderer.dispose();
        if (map != null) map.dispose();
        if (monsterRenderer != null) monsterRenderer.dispose();
        if (monster != null) clock.removeListener(monster);

        level = session.getLevel();
        Gnome gnome = level.getGnome();
        Labyrinth labyrinth = level.getLabyrinth();
        Entrance entrance = labyrinth.getEntrance();

        gnome.setPosition(
                entrance.getX() + (entrance.getWidth() - gnome.getWidth()) / 2f,
                entrance.getY() + (entrance.getHeight() - gnome.getHeight()) / 2f);

        LevelConfig config = factory.configFor(session.getCurrentLevelNumber());
        map = new TmxMapLoader().load(config.mapPath());
        renderer = new LabyrinthRenderer(map);

        monsterGrid = new LabyrinthGrid(labyrinth, Monster.SIZE);
        monster = level.getMonster();
        clock.addListener(monster);
        monsterRenderer = new MonsterRenderer(monster.getClass().getSimpleName().toLowerCase());

        input = new InputController(level);
    }


    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
        if (pauseStage != null) {
            pauseStage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == pauseStage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        if (renderer != null) { renderer.dispose(); renderer = null; }
        if (characterRenderer != null) { characterRenderer.dispose(); characterRenderer = null; }
        if (debugRenderer != null) { debugRenderer.dispose(); debugRenderer = null; }
        if (batch != null) { batch.dispose(); batch = null; }
        if (map != null) { map.dispose(); map = null; }
        if (fogRenderer != null) { fogRenderer.dispose(); fogRenderer = null; }
        if (monsterRenderer != null) { monsterRenderer.dispose(); monsterRenderer = null; }
        if (shapes != null) { shapes.dispose(); shapes = null; }
        if (pauseStage != null) { pauseStage.dispose(); pauseStage = null; }
        if (pauseSkin != null) { pauseSkin.dispose(); pauseSkin = null; }
        if (chestRenderer != null) { chestRenderer.dispose(); chestRenderer = null; }
    }
}
package com.blindpath.presentation.screen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
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
import com.blindpath.infrastructure.map.TiledMapStore;
import com.blindpath.presentation.hud.Hud;
import com.blindpath.presentation.input.InputController;
import com.blindpath.presentation.renderer.CharacterRenderer;
import com.blindpath.presentation.renderer.ChestRenderer;
import com.blindpath.presentation.renderer.BrambleRenderer;
import com.blindpath.presentation.renderer.LeverWallRenderer;
import com.blindpath.presentation.renderer.CollisionDebugRenderer;
import com.blindpath.presentation.renderer.DamageOverlayRenderer;
import com.blindpath.presentation.renderer.FogRenderer;
import com.blindpath.presentation.renderer.GnomeSprites;
import com.blindpath.presentation.renderer.JumpscareRenderer;
import com.blindpath.presentation.renderer.LabyrinthRenderer;
import com.blindpath.presentation.renderer.MonsterRenderer;
import com.blindpath.domain.monster.Screamer;

public class GameScreen implements Screen {

    private static final float WORLD_WIDTH = 30f;
    private static final float WORLD_HEIGHT = 30f;

    private TiledMap map;
    private TiledMapStore mapStore;
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
    private BrambleRenderer brambleRenderer;
    private LeverWallRenderer leverWallRenderer;
    private Monster monster;
    private MonsterRenderer monsterRenderer;
    private String currentMonsterName;
    private LabyrinthGrid monsterGrid;
    private final GameClock clock = new GameClock();
    private ShapeRenderer shapes;
    private JumpscareRenderer jumpscare;
    private DamageOverlayRenderer damageOverlay;

    private boolean paused = false;
    private final Main main;
    private Stage pauseStage;
    private Skin pauseSkin;

    private Hud hud;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        if (map == null) {
            hud = new Hud();
            LevelRegistry registry = new LevelRegistry();
            mapStore = new TiledMapStore();
            factory = new LevelFactory(registry, new TiledLabyrinthLoader(mapStore), new LootTable(), new Random());
            Gnome gnome = new Gnome();

            session = new GameSession(factory, gnome);
            session.startLevel(1);
            loadCurrentLevelResources();

            fogRenderer = new FogRenderer();
            chestRenderer = new ChestRenderer();
            brambleRenderer = new BrambleRenderer();
            leverWallRenderer = new LeverWallRenderer();
            damageOverlay = new DamageOverlayRenderer();
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
        jumpscare.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        damageOverlay.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
            int lifeBefore = level.getGnome().getLife();
            if (!level.getGnome().isDead()) {
                input.handleInput(delta);
                level.update(delta);
            }

            if (level.getGnome().getLife() < lifeBefore) {
                if (level.getGnome().isDead()) {
                    jumpscare.trigger();
                } else {
                    damageOverlay.trigger();
                }
            }
            jumpscare.update(delta);
            damageOverlay.update(delta);

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

            if (level.getGnome().isDead() && !jumpscare.isActive()) {
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
            monster.update(delta);

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

        
        if (Gdx.input.isKeyJustPressed(Keys.I)) level.getGnome().toggleInvincible();
        
        // Avance le préchargement du niveau suivant (budget de quelques ms par frame).
        mapStore.update();

        // 2. Dessin
        // Efface l'image précédente, y compris les bandes autour du viewport.
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        viewport.apply();
        camera.update();
        renderer.render(camera);
        // Les ronces sont dessinées dans le monde, avant le voile de brouillard.
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        brambleRenderer.render(batch, level.getLabyrinth().getBrambles());
        leverWallRenderer.render(batch, level.getLabyrinth().getRotatingWalls(), level.getLabyrinth().getLevers());
        batch.end();
        boolean monsterOnTop = monsterVisible || (monster instanceof Screamer screamer && screamer.isScreaming());

        // 1. Avant le brouillard (mode normal)
        if (!monsterOnTop || !debugCollisions) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            if (!debugCollisions) {
                chestRenderer.render(batch, level.getChests());
            }
            if (!monsterOnTop) {
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
        if (monsterOnTop) {
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

        hud.update(light.getCooldownProgress(), level.getGnome().getLife(),
                level.getGnome().getInventory().getStacks(), session.getCurrentLevelNumber());
        hud.render(paused ? 0f : delta);

        damageOverlay.render(batch);
        jumpscare.render(batch);

        if (paused) {
            pauseStage.getViewport().apply();
            pauseStage.act(delta);
            pauseStage.draw();
        }
    }

    private void loadCurrentLevelResources() {
        if (renderer != null) renderer.dispose();
        if (monster != null) clock.removeListener(monster);

        level = session.getLevel();
        Gnome gnome = level.getGnome();
        Labyrinth labyrinth = level.getLabyrinth();
        Entrance entrance = labyrinth.getEntrance();

        gnome.setPosition(
            entrance.getX() + (entrance.getWidth() - gnome.getWidth()) / 2f,
            entrance.getY() + (entrance.getHeight() - gnome.getHeight()) / 2f);

        LevelConfig config = factory.configFor(session.getCurrentLevelNumber());
        // La carte a déjà été chargée par la factory (même store) : get() est instantané.
        map = mapStore.get(config.mapPath());
        renderer = new LabyrinthRenderer(map);

        // On garde le niveau courant et on précharge le suivant en arrière-plan ;
        // le reste est libéré (les textures partagées entre cartes restent en mémoire).
        Set<String> keep = new HashSet<>();
        keep.add(config.mapPath());
        int next = session.getCurrentLevelNumber() + 1;
        if (next <= factory.totalLevels()) {
            keep.add(factory.configFor(next).mapPath());
            mapStore.preload(factory.configFor(next).mapPath());
        }
        mapStore.retainOnly(keep);

        monsterGrid = new LabyrinthGrid(labyrinth, Monster.SIZE);
        monster = level.getMonster();
        clock.addListener(monster);
        
        String monsterName = monster.getClass().getSimpleName().toLowerCase();
        if (!monsterName.equals(currentMonsterName)) {
            if (monsterRenderer != null) monsterRenderer.dispose();
            if (jumpscare != null) jumpscare.dispose();

            currentMonsterName = monsterName;
            monsterRenderer = new MonsterRenderer(monsterName);
            jumpscare = new JumpscareRenderer(monsterName);
        }
        if (Gdx.graphics != null && jumpscare != null) {
            jumpscare.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        input = new InputController(level);
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
        if (hud != null) {
            hud.resize(width, height);
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
        map = null; // la carte appartient au store
        if (mapStore != null) { mapStore.dispose(); mapStore = null; }
        if (fogRenderer != null) { fogRenderer.dispose(); fogRenderer = null; }
        if (monsterRenderer != null) { monsterRenderer.dispose(); monsterRenderer = null; }
        currentMonsterName = null;
        if (shapes != null) { shapes.dispose(); shapes = null; }
        if (jumpscare != null) { jumpscare.dispose(); jumpscare = null; }
        if (damageOverlay != null) { damageOverlay.dispose(); damageOverlay = null; }
        if (pauseStage != null) { pauseStage.dispose(); pauseStage = null; }
        if (pauseSkin != null) { pauseSkin.dispose(); pauseSkin = null; }
        if (chestRenderer != null) { chestRenderer.dispose(); chestRenderer = null; }
        if (brambleRenderer != null) { brambleRenderer.dispose(); brambleRenderer = null; }
        if (leverWallRenderer != null) { leverWallRenderer.dispose(); leverWallRenderer = null; }
        if (hud != null) { hud.dispose(); hud = null; }
    }
}

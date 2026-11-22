package com.blindpath.infrastructure.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.blindpath.domain.inventory.ItemType;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Bramble;
import com.blindpath.domain.labyrinth.Lever;
import com.blindpath.domain.labyrinth.RotatingWall;
import com.blindpath.domain.labyrinth.OpeningOrientation;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.LabyrinthLoader;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.labyrinth.Wall;
import com.blindpath.domain.monster.MonsterType;
import com.blindpath.domain.pathfinding.GridPosition;

/**
 * Chargeur de cartes Tiled (.tmx) : convertit les calques de la carte en objets du domaine.
 * <p>
 * Calques lus : {@code Collisions} (murs), {@code Entrance}, {@code Exit}, {@code Coffre},
 * {@code monstre_start}, {@code ronces} et {@code movableWall}. 250 pixels de la carte font une case.
 * Les erreurs de carte (forme, orientation, taille) sont signalées par une exception plutôt
 * que de laisser un mur sans collision.
 */
public class TiledLabyrinthLoader implements LabyrinthLoader {

    // Même conversion que dans LabyrinthRenderer : 250 pixels = une case.
    private static final float UNIT_SCALE = 1f / 250f;

    // Optionnel : quand il est fourni, la carte est chargée une seule fois et partagée avec le rendu.
    private final TiledMapStore store;

    /** Crée un chargeur qui ouvre et libère la carte à chaque lecture. */
    public TiledLabyrinthLoader() {
        this(null);
    }

    /**
     * Crée un chargeur qui lit les cartes depuis un cache partagé avec l'affichage.
     *
     * @param store le cache de cartes, ou {@code null} pour charger la carte à chaque lecture
     */
    public TiledLabyrinthLoader(TiledMapStore store) {
        this.store = store;
    }

    private <T> T read(String mapPath, Function<TiledMap, T> reader) {
        if (mapPath == null || mapPath.isBlank()) {
            throw new IllegalArgumentException("Le chemin de la carte doit être renseigné.");
        }
        if (store != null) {
            return reader.apply(store.get(mapPath)); // la carte appartient au store : on ne la libère pas
        }
        TiledMap map = new TmxMapLoader().load(mapPath);
        try {
            return reader.apply(map);
        } finally {
            map.dispose();
        }
    }

    private static ItemType parseLootType(MapObject object) {
        String rawLoot = object.getProperties().get("loot", String.class);
        if (rawLoot == null || rawLoot.isBlank()) {
            return null;
        }
        return ItemType.valueOf(rawLoot.trim().toUpperCase());
    }

    @Override
    public Labyrinth load(String mapPath) {
        return read(mapPath, map -> load(map));
    }

    /**
     * Construit le labyrinthe à partir d'une carte déjà chargée.
     *
     * @param map la carte
     * @return le labyrinthe
     * @throws IllegalArgumentException si la carte est {@code null} ou mal formée
     */
    public Labyrinth load(TiledMap map) {
        if (map == null) {
            throw new IllegalArgumentException("La carte doit être renseignée.");
        }
        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);
        Entrance entrance = loadEntrance(map);
        Exit exit = loadExit(map);
        List<Obstacle> obstacles = loadObstacles(map);
        List<Lever> levers = loadLevers(map);
        for (Lever lever : levers) obstacles.add(lever.getWall());
        return new Labyrinth(width, height, entrance, exit, obstacles, loadBrambles(map), levers);
    }

    /**
     * Lit les murs tournants et leurs leviers dans le calque {@code movableWall}.
     * Chaque mur référence son pivot et son levier, et porte une rotation de 90 ou -90 degrés.
     *
     * @param map la carte
     * @return les leviers, chacun avec son mur (vide si le calque n'existe pas)
     * @throws IllegalArgumentException si la carte est {@code null} ou si un mur est mal décrit
     */
    public List<Lever> loadLevers(TiledMap map) {
        if (map == null) throw new IllegalArgumentException("La carte doit être renseignée.");
        MapLayer layer = map.getLayers().get("movableWall");
        if (layer == null) return List.of();
        List<Lever> levers = new ArrayList<>();
        for (MapObject object : layer.getObjects()) {
            if (!"movableWall".equals(object.getName())) continue;
            if (!(object instanceof RectangleMapObject wallObject)) {
                throw new IllegalArgumentException("Le mur mobile doit être un rectangle.");
            }
            OpeningOrientation orientation = readOrientation(object);
            Rectangle rectangle = wallObject.getRectangle();
            validateOpeningSize(rectangle, orientation == OpeningOrientation.HORIZONTAL, orientation.toString());
            MapObject pivot = referencedObject(map, object, "pivot");
            if (!(pivot instanceof PointMapObject point)) {
                throw new IllegalArgumentException("Le pivot du mur doit être un point Tiled.");
            }
            MapObject lever = referencedObject(map, object, "levier");
            if (!(lever instanceof RectangleMapObject leverObject)) {
                throw new IllegalArgumentException("Le levier doit être un rectangle Tiled.");
            }
            Object rotation = object.getProperties().get("rotation");
            if (!(rotation instanceof Number number) || (number.floatValue() != 90f && number.floatValue() != -90f)) {
                throw new IllegalArgumentException("La propriété rotation du mur doit être 90 ou -90.");
            }
            RotatingWall wall = new RotatingWall(
                rectangle.x * UNIT_SCALE, rectangle.y * UNIT_SCALE,
                orientation, 
                point.getPoint().x * UNIT_SCALE, point.getPoint().y * UNIT_SCALE,
                number.intValue()
            );

            Rectangle leverBounds = leverObject.getRectangle();
            levers.add(new Lever(leverBounds.x * UNIT_SCALE, leverBounds.y * UNIT_SCALE,
                    leverBounds.width * UNIT_SCALE, leverBounds.height * UNIT_SCALE, wall));
        }
        return levers;
    }

    private MapObject referencedObject(TiledMap map, MapObject source, String property) {
        Object reference = source.getProperties().get(property);
        // Selon la version du chargeur, une référence Tiled est résolue en objet ou reste un ID.
        if (reference instanceof MapObject object) return object;
        if (reference instanceof Number id) {
            for (MapLayer layer : map.getLayers()) {
                for (MapObject object : layer.getObjects()) {
                    Number candidateId = (Number) object.getProperties().get("id");
                    if (candidateId != null && candidateId.intValue() == id.intValue()) return object;
                }
            }
        }
        throw new IllegalArgumentException("Référence objet manquante ou invalide : " + property);
    }

    /**
     * Lit les ronces du calque {@code ronces}. Elles ne sont pas ajoutées aux murs de collision.
     *
     * @param map la carte
     * @return les ronces (vide si le calque n'existe pas)
     * @throws IllegalArgumentException si la carte est {@code null} ou si des ronces sont mal décrites
     */
    public List<Bramble> loadBrambles(TiledMap map) {
        if (map == null) throw new IllegalArgumentException("La carte doit être renseignée.");
        MapLayer layer = map.getLayers().get("ronces");
        if (layer == null) return List.of();
        List<Bramble> brambles = new ArrayList<>();
        for (MapObject object : layer.getObjects()) {
            if (!(object instanceof RectangleMapObject rectangleObject)) {
                throw new IllegalArgumentException("Les ronces doivent être des rectangles : " + object.getName());
            }
            Number rotation = (Number) object.getProperties().get("rotation");
            if (rotation != null && rotation.floatValue() != 0f) {
                throw new IllegalArgumentException("Utiliser orientation plutôt que la rotation pour les ronces.");
            }
            OpeningOrientation orientation = readOrientation(object);
            Rectangle rectangle = rectangleObject.getRectangle();
            validateOpeningSize(rectangle, orientation == OpeningOrientation.HORIZONTAL, orientation.toString());
            brambles.add(new Bramble(rectangle.x * UNIT_SCALE, rectangle.y * UNIT_SCALE, orientation));
        }
        return brambles;
    }

    @Override
    public LabyrinthData loadAll(String mapPath) {
        return read(mapPath, map -> loadAll(map));
    }

    /**
     * Lit le labyrinthe, les coffres et le monstre d'une carte déjà chargée.
     *
     * @param map la carte
     * @return toutes les données du niveau
     * @throws IllegalArgumentException si la carte est {@code null} ou mal formée
     */
    public LabyrinthData loadAll(TiledMap map) {
        if (map == null) {
            throw new IllegalArgumentException("La carte doit être renseignée.");
        }
        return new LabyrinthData(
                load(map),
                loadChestSpawns(map),
                loadMonsterSpawn(map),
                loadMonsterType(map));
    }

    /**
     * Convertit les rectangles Tiled en obstacles indépendants de LibGDX.
     */
    public List<Obstacle> loadObstacles(TiledMap map) {
        if (map == null) {
            throw new IllegalArgumentException("La carte doit être renseignée.");
        }

        // Une couche masquée reste accessible : sa visibilité concerne le dessin.
        MapLayer collisions = map.getLayers().get("Collisions");
        if (collisions == null) {
            throw new IllegalArgumentException("La carte doit contenir une couche Collisions.");
        }

        List<Obstacle> obstacles = new ArrayList<>();

        for (MapObject object : collisions.getObjects()) {
            // Notre modèle Bounds représente uniquement des rectangles sans rotation.
            // Signaler les formes incompatibles évite de laisser un mur sans collision.
            if (!(object instanceof RectangleMapObject)) {
                throw new IllegalArgumentException(
                        "La couche Collisions doit contenir uniquement des rectangles : " + object.getName());
            }
            Number rotation = (Number) object.getProperties().get("rotation");
            if (rotation != null && rotation.floatValue() != 0f) {
                throw new IllegalArgumentException(
                        "Les rectangles de collision ne doivent pas être tournés : " + object.getName());
            }

            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            // TmxMapLoader a déjà converti l'axe vertical pour LibGDX.
            // On change seulement l'unité, sans inverser de nouveau les coordonnées.
            Wall wall = new Wall(
                    rectangle.x * UNIT_SCALE,
                    rectangle.y * UNIT_SCALE,
                    rectangle.width * UNIT_SCALE,
                    rectangle.height * UNIT_SCALE);
            obstacles.add(wall);
        }

        // Seuls des objets métier sortent du chargeur : aucune Rectangle LibGDX
        // n'est conservée dans les Wall.
        return obstacles;
    }

    /**
     * Lit l'ouverture d'entrée dans le calque Entrance.
     *
     * @param map la carte
     * @return l'entrée
     * @throws IllegalArgumentException si l'entrée manque ou est mal décrite
     */
    public Entrance loadEntrance(TiledMap map) {
        RectangleMapObject object = loadOpening(map, "Entrance");
        Rectangle rectangle = object.getRectangle();
        OpeningOrientation orientation = readOrientation(object);
        validateOpeningSize(rectangle, orientation == OpeningOrientation.HORIZONTAL, orientation.toString());
        return new Entrance(toGridCoordinate(rectangle.x), toGridCoordinate(rectangle.y), orientation);
    }

    /**
     * Lit l'ouverture de sortie dans le calque Exit.
     *
     * @param map la carte
     * @return la sortie
     * @throws IllegalArgumentException si la sortie manque ou est mal décrite
     */
    public Exit loadExit(TiledMap map) {
        RectangleMapObject object = loadOpening(map, "Exit");
        Rectangle rectangle = object.getRectangle();
        OpeningOrientation orientation = readOrientation(object);
        validateOpeningSize(rectangle, orientation == OpeningOrientation.HORIZONTAL, orientation.toString());
        return new Exit(toGridCoordinate(rectangle.x), toGridCoordinate(rectangle.y), orientation);
    }

    @Override
    public List<ChestSpawn> loadChestSpawns(String mapPath) {
        return read(mapPath, map -> loadChestSpawns(map));
    }

    /**
     * Lit les coffres du calque {@code Coffre} : position, mur d'appui (propriété Orientation)
     * et contenu imposé éventuel (propriété loot).
     *
     * @param map la carte
     * @return les emplacements des coffres (vide si le calque n'existe pas)
     * @throws IllegalArgumentException si la carte est {@code null} ou si un coffre est mal décrit
     */
    public List<ChestSpawn> loadChestSpawns(TiledMap map) {
        if (map == null) {
            throw new IllegalArgumentException("La carte doit être renseignée.");
        }

        MapLayer layer = map.getLayers().get("Coffre");
        if (layer == null) {
            return List.of();
        }

        List<ChestSpawn> spawns = new ArrayList<>();
        for (MapObject object : layer.getObjects()) {
            if (!(object instanceof RectangleMapObject rectObj)) {
                throw new IllegalArgumentException("Le calque Coffre doit contenir uniquement des rectangles.");
            }
            Rectangle rect = rectObj.getRectangle();
            String rawOrientation = object.getProperties().get("Orientation", String.class);
            if (rawOrientation == null || rawOrientation.isBlank()) {
                throw new IllegalArgumentException("Propriété Orientation manquante sur le coffre.");
            }
            Border orientation = Border.valueOf(rawOrientation.toUpperCase());
            validateOpeningSize(rect, orientation);

            ItemType fixedLoot = parseLootType(object);
            spawns.add(new ChestSpawn(
                toGridCoordinate(rect.x),
                toGridCoordinate(rect.y),
                orientation,
                fixedLoot
            ));
        }
        return spawns;
    }

    @Override
    public MonsterType loadMonsterType(String mapPath) {
        return read(mapPath, map -> loadMonsterType(map));
    }

    /**
     * Lit le monstre imposé par la carte (propriété monsterType de l'objet de {@code monstre_start}).
     *
     * @param map la carte
     * @return le type de monstre, ou {@code null} pour utiliser celui du palier
     * @throws IllegalArgumentException si la carte est {@code null}
     */
    public MonsterType loadMonsterType(TiledMap map) {
        if (map == null) {
            throw new IllegalArgumentException("La carte doit être renseignée.");
        }
        MapLayer layer = map.getLayers().get("monstre_start");
        if (layer == null || layer.getObjects().getCount() != 1) {
            return null;
        }
        MapObject object = layer.getObjects().get(0);
            String rawType = object.getProperties().get("monsterType", String.class);
        if (rawType == null || rawType.isBlank()) {
            return null;
        }
        return MonsterType.valueOf(rawType.trim().toUpperCase());
    }

    
    @Override
    public GridPosition loadMonsterSpawn(String mapPath) {
        return read(mapPath, map -> loadMonsterSpawn(map));
    }

    /**
     * Lit la case de départ du monstre dans le calque {@code monstre_start}. Le point Tiled marque
     * le centre du bloc 2 x 2 du monstre, la case renvoyée est son coin bas gauche.
     *
     * @param map la carte
     * @return la case de départ du monstre
     * @throws IllegalArgumentException si le calque ne contient pas exactement un point ou un rectangle
     */
    public GridPosition loadMonsterSpawn(TiledMap map) {
        if (map == null) {
            throw new IllegalArgumentException("La carte doit être renseignée.");
        }
        MapLayer layer = map.getLayers().get("monstre_start");
        if (layer == null || layer.getObjects().getCount() != 1) {
            throw new IllegalArgumentException("Le calque monstre_start doit contenir exactement un objet.");
        }

        MapObject object = layer.getObjects().get(0);
        float px;
        float py;

        if (object instanceof PointMapObject pointObj) {
            px = pointObj.getPoint().x;
            py = pointObj.getPoint().y;
        } else if (object instanceof RectangleMapObject rectObj) {
            px = rectObj.getRectangle().x;
            py = rectObj.getRectangle().y;
        } else {
            throw new IllegalArgumentException(
                    "L'objet dans monstre_start doit être un point ou un rectangle : " + object.getName());
        }

        int col = toGridCoordinate(px);
        int row = toGridCoordinate(py);

        // Le point Tiled est au centre de la zone 2x2 du monstre -> coin bas-gauche = (col - 1, row - 1)
        return new GridPosition(col - 1, row - 1);

    }

    // Entrée et sortie suivent les mêmes règles : on partage leur validation.
    private RectangleMapObject loadOpening(TiledMap map, String name) {
        if (map == null) {
            throw new IllegalArgumentException("La carte doit être renseignée.");
        }
        MapLayer layer = map.getLayers().get(name);
        if (layer == null) {
            throw new IllegalArgumentException("Calque manquant : " + name);
        }
        if (layer.getObjects().getCount() != 1) {
            throw new IllegalArgumentException("Le calque " + name + " doit contenir exactement un objet.");
        }
        MapObject object = layer.getObjects().get(name);
        if (!(object instanceof RectangleMapObject)) {
            throw new IllegalArgumentException("Objet rectangulaire manquant : " + name);
        }
        Number rotation = (Number) object.getProperties().get("rotation");
        if (rotation != null && rotation.floatValue() != 0f) {
            throw new IllegalArgumentException("L'ouverture ne doit pas être tournée : " + name);
        }
        return (RectangleMapObject) object;
    }

    private OpeningOrientation readOrientation(MapObject object) {
        String text = object.getProperties().get("orientation", String.class);
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Propriété orientation manquante : " + object.getName());
        }
        try {
            return OpeningOrientation.valueOf(text.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                "Orientation invalide pour " + object.getName() + " : " + text
                    + ". Valeurs attendues : HORIZONTAL, VERTICAL.", exception);
        }
    }

    private int toGridCoordinate(float pixels) {
        float coordinate = pixels * UNIT_SCALE;
        int rounded = Math.round(coordinate);
        // Une petite tolérance absorbe les imprécisions des calculs en float.
        if (!Float.isFinite(coordinate) || Math.abs(coordinate - rounded) > 0.0001f) {
            throw new IllegalArgumentException("L'ouverture doit être alignée sur la grille : " + coordinate);
        }
        return rounded;
    }

    private void validateOpeningSize(Rectangle rectangle, Border border) {
        validateOpeningSize(rectangle, border == Border.TOP || border == Border.BOTTOM, border.toString());
    }

    private void validateOpeningSize(Rectangle rectangle, boolean horizontal, String orientation) {
        int expectedWidth = horizontal ? 2 : 1;
        int expectedHeight = horizontal ? 1 : 2;
        if (toGridCoordinate(rectangle.width) != expectedWidth
                || toGridCoordinate(rectangle.height) != expectedHeight) {
            throw new IllegalArgumentException(
                "L'ouverture doit mesurer " + expectedWidth + " x " + expectedHeight + " cases pour " + orientation);
        }
    }
}
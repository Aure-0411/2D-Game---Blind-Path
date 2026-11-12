package com.blindpath.infrastructure.map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.blindpath.domain.labyrinth.Entrance;
import com.blindpath.domain.labyrinth.Exit;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.labyrinth.Wall;
import com.blindpath.domain.labyrinth.Border;

public class TiledLabyrinthLoader {

    // Même conversion que dans LabyrinthRenderer : 250 pixels = une case.
    private static final float UNIT_SCALE = 1f / 250f;

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

    /** Lit l'ouverture d'entrée dans le calque Entrance. */
    public Entrance loadEntrance(TiledMap map) {
        RectangleMapObject object = loadOpening(map, "Entrance");
        Rectangle rectangle = object.getRectangle();
        Border border = readBorder(object);
        validateOpeningSize(rectangle, border);
        return new Entrance(toGridCoordinate(rectangle.x), toGridCoordinate(rectangle.y), border);
    }

    /** Lit l'ouverture de sortie dans le calque Exit. */
    public Exit loadExit(TiledMap map) {
        RectangleMapObject object = loadOpening(map, "Exit");
        Rectangle rectangle = object.getRectangle();
        Border border = readBorder(object);
        validateOpeningSize(rectangle, border);
        return new Exit(toGridCoordinate(rectangle.x), toGridCoordinate(rectangle.y), border);
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

    private Border readBorder(MapObject object) {
        String text = object.getProperties().get("border", String.class);
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Propriété border manquante : " + object.getName());
        }
        try {
            // Les valeurs Tiled doivent correspondre exactement à l'énumération Java.
            return Border.valueOf(text);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                "Bord invalide pour " + object.getName() + " : " + text
                    + ". Valeurs attendues : TOP, BOTTOM, LEFT, RIGHT.", exception);
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
        boolean horizontal = border == Border.TOP || border == Border.BOTTOM;
        int expectedWidth = horizontal ? 2 : 1;
        int expectedHeight = horizontal ? 1 : 2;
        if (toGridCoordinate(rectangle.width) != expectedWidth
                || toGridCoordinate(rectangle.height) != expectedHeight) {
            throw new IllegalArgumentException(
                "L'ouverture doit mesurer " + expectedWidth + " x " + expectedHeight + " cases pour " + border);
        }
    }
}
package com.blindpath.domain.labyrinth;

import java.util.List;

/** Données du labyrinthe, exprimées en cases et indépendantes de LibGDX. */
public class Labyrinth {

    private final int width;
    private final int height;
    private final List<Obstacle> obstacles;
    private final List<Obstacle> boundaryWalls;
    private final Entrance entrance;
    private final Exit exit;

    public Labyrinth(int width, int height, Entrance entrance, Exit exit, List<Obstacle> obstacles) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Les dimensions du labyrinthe doivent être strictement positives.");
        }
        if (entrance == null || exit == null) {
            throw new IllegalArgumentException("L'entrée et la sortie doivent être renseignées.");
        }
        if (obstacles == null || obstacles.stream().anyMatch(obstacle -> obstacle == null)) {
            throw new IllegalArgumentException("La liste d'obstacles et ses éléments doivent être renseignés.");
        }

        this.width = width;
        this.height = height;
        validateOpening(entrance.getX(), entrance.getY(), entrance.getWidth(),
            entrance.getHeight(), entrance.getBorder());
        validateOpening(exit.getX(), exit.getY(), exit.getWidth(),
            exit.getHeight(), exit.getBorder());

        this.entrance = entrance;
        this.exit = exit;
        // Copie non modifiable : ajouter ou retirer un élément de la liste d'origine
        // ne change pas les obstacles du labyrinthe.
        this.obstacles = List.copyOf(obstacles);
        // Murs invisibles juste à l'extérieur de la carte (épaisseur 1 case).
        this.boundaryWalls = List.of(
            new Wall(-1, -1, width + 2, 1),      // bas
            new Wall(-1, height, width + 2, 1),  // haut
            new Wall(-1, 0, 1, height),          // gauche
            new Wall(width, 0, 1, height));      // droite
    }

    /**
     * Limites de la carte : le gnome ne peut pas en sortir, même par l'entrée ou la sortie
     * (leurs ouvertures sont dans la grille, ces murs sont juste derrière).
     */
    public List<Obstacle> getBoundaryWalls() {
        return boundaryWalls;
    }

    private void validateOpening(int x, int y, int openingWidth, int openingHeight, Border border) {
        // Vérifie le rectangle entier, avec l'origine du monde en bas à gauche.
        // Les additions en long évitent un dépassement de capacité des int.
        if (x < 0 || y < 0 || (long) x + openingWidth > width || (long) y + openingHeight > height) {
            throw new IllegalArgumentException("L'ouverture doit rester dans les limites du labyrinthe.");
        }
        if (border == null) {
            throw new IllegalArgumentException("Le bord de l'ouverture doit être renseigné.");
        }
        boolean onBorder = switch (border) {
            case LEFT -> x == 0;
            case RIGHT -> (long) x + openingWidth == width;
            case BOTTOM -> y == 0;
            case TOP -> (long) y + openingHeight == height;
        };
        if (!onBorder) {
            throw new IllegalArgumentException("L'ouverture doit se trouver sur le bord annoncé : " + border);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public Exit getExit() {
        return exit;
    }
}
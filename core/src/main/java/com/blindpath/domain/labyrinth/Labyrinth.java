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
    private final List<Bramble> brambles;
    private final List<Lever> levers;
    private final List<RotatingWall> rotatingWalls;

    /**
     * Crée un labyrinthe sans ronces ni leviers.
     *
     * @param width largeur, en cases
     * @param height hauteur, en cases
     * @param entrance l'entrée
     * @param exit la sortie
     * @param obstacles les murs et obstacles
     */
    public Labyrinth(int width, int height, Entrance entrance, Exit exit, List<Obstacle> obstacles) {
        this(width, height, entrance, exit, obstacles, List.of());
    }

    /**
     * Crée un labyrinthe sans leviers.
     *
     * @param width largeur, en cases
     * @param height hauteur, en cases
     * @param entrance l'entrée
     * @param exit la sortie
     * @param obstacles les murs et obstacles
     * @param brambles les ronces
     */
    public Labyrinth(int width, int height, Entrance entrance, Exit exit,
                     List<Obstacle> obstacles, List<Bramble> brambles) {
        this(width, height, entrance, exit, obstacles, brambles, List.of());
    }

    /**
     * Crée un labyrinthe complet. Les murs tournants sont repérés parmi les obstacles,
     * et des murs invisibles sont ajoutés tout autour de la carte.
     *
     * @param width largeur, en cases
     * @param height hauteur, en cases
     * @param entrance l'entrée, qui doit tenir dans la carte
     * @param exit la sortie, qui doit tenir dans la carte
     * @param obstacles les murs et obstacles
     * @param brambles les ronces
     * @param levers les leviers, dont chaque mur doit faire partie des obstacles
     * @throws IllegalArgumentException si une dimension n'est pas positive, si un élément
     *         manque, si l'entrée ou la sortie dépasse de la carte, ou si le mur d'un levier
     *         n'est pas dans le labyrinthe
     */
    public Labyrinth(int width, int height, Entrance entrance, Exit exit,
                     List<Obstacle> obstacles, List<Bramble> brambles, List<Lever> levers) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Les dimensions du labyrinthe doivent être strictement positives.");
        }
        if (entrance == null || exit == null) {
            throw new IllegalArgumentException("L'entrée et la sortie doivent être renseignées.");
        }
        if (obstacles == null || obstacles.stream().anyMatch(obstacle -> obstacle == null)) {
            throw new IllegalArgumentException("La liste d'obstacles et ses éléments doivent être renseignés.");
        }
        if (brambles == null || brambles.stream().anyMatch(bramble -> bramble == null)) {
            throw new IllegalArgumentException("Les ronces doivent être renseignées.");
        }
        this.brambles = List.copyOf(brambles);
        if (levers == null || levers.stream().anyMatch(lever -> lever == null)) {
            throw new IllegalArgumentException("Les leviers doivent être renseignés.");
        }
        this.levers = List.copyOf(levers);
        this.rotatingWalls = obstacles.stream().filter(RotatingWall.class::isInstance)
                .map(RotatingWall.class::cast).toList();
        if (levers.stream().anyMatch(lever -> !rotatingWalls.contains(lever.getWall()))) {
            throw new IllegalArgumentException("Le mur de chaque levier doit appartenir au labyrinthe.");
        }

        this.width = width;
        this.height = height;
        validateOpening(entrance.getX(), entrance.getY(), entrance.getWidth(),
            entrance.getHeight());
        validateOpening(exit.getX(), exit.getY(), exit.getWidth(),
            exit.getHeight());

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
     * Limites de la carte : le personnage ne peut pas en sortir, même par l'entrée ou la sortie
     * (leurs ouvertures sont dans la grille, ces murs sont juste derrière).
     */
    public List<Obstacle> getBoundaryWalls() {
        return boundaryWalls;
    }

    private void validateOpening(int x, int y, int openingWidth, int openingHeight) {
        // Vérifie le rectangle entier, avec l'origine du monde en bas à gauche.
        // Les additions en long évitent un dépassement de capacité des int.
        if (x < 0 || y < 0 || (long) x + openingWidth > width || (long) y + openingHeight > height) {
            throw new IllegalArgumentException("L'ouverture doit rester dans les limites du labyrinthe.");
        }
    }

    /** @return la largeur, en cases */
    public int getWidth() {
        return width;
    }

    /** @return la hauteur, en cases */
    public int getHeight() {
        return height;
    }

    /** @return les obstacles (copie non modifiable) */
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    /** @return les ronces (copie non modifiable) */
    public List<Bramble> getBrambles() {
        return brambles;
    }

    /** @return les leviers (copie non modifiable) */
    public List<Lever> getLevers() { return levers; }
    /** @return les murs tournants, repérés parmi les obstacles */
    public List<RotatingWall> getRotatingWalls() { return rotatingWalls; }

    /** @return l'entrée */
    public Entrance getEntrance() {
        return entrance;
    }

    /** @return la sortie */
    public Exit getExit() {
        return exit;
    }
}
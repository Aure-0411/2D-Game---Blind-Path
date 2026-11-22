package com.blindpath.domain.character;

/**
 * Les 8 directions de déplacement, plus {@link #NONE} pour l'arrêt.
 * Chaque direction connaît son pas sur la grille (dx, dy) et son vecteur normalisé,
 * pour qu'un déplacement en diagonale n'aille pas plus vite qu'en ligne droite.
 */
public enum Direction {
    /** Aucune direction : le personnage est à l'arrêt. */
    NONE(0, 0),
    UP(0, 1),
    UP_RIGHT(1, 1),
    RIGHT(1, 0),
    DOWN_RIGHT(1, -1),
    DOWN(0, -1),
    DOWN_LEFT(-1, -1),
    LEFT(-1, 0),
    UP_LEFT(-1, 1);

    private final int dx, dy;
    private final float normalizedX, normalizedY;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
        double length = Math.hypot(dx, dy);
        if (length == 0) {
            this.normalizedX = 0f;
            this.normalizedY = 0f;
        } else {
            this.normalizedX = (float) (dx / length);
            this.normalizedY = (float) (dy / length);
        }
    }

    /** @return le pas horizontal (-1, 0 ou 1) */
    public int getDx() { return dx; }
    /** @return le pas vertical (-1, 0 ou 1) */
    public int getDy() { return dy; }
    /** @return la composante horizontale du vecteur de longueur 1 */
    public float getNormalizedX() {return normalizedX; }
    /** @return la composante verticale du vecteur de longueur 1 */
    public float getNormalizedY() {return normalizedY; }

    /**
     * Déduit la direction à partir des touches enfoncées. Deux touches opposées s'annulent.
     *
     * @param up touche haut enfoncée
     * @param down touche bas enfoncée
     * @param left touche gauche enfoncée
     * @param right touche droite enfoncée
     * @return la direction correspondante, ou {@link #NONE} si aucune
     */
    public static Direction fromKeys(boolean up, boolean down, boolean left, boolean right) {
        int x = (right ? 1 : 0) - (left ? 1 : 0);
        int y = (up ? 1 : 0) - (down ? 1 : 0);

        for (Direction direction : values()) {
            if (direction.dx == x && direction.dy == y) {
                return direction;
            }
        }
        return NONE;
    }
}
package com.blindpath.domain.labyrinth;

/** Mur de deux cases, pivotant une seule fois autour d'un point choisi dans Tiled. */
public final class RotatingWall extends Wall {
    private final Bounds initialBounds;
    private final OpeningOrientation initialOrientation;
    private final float pivotX;
    private final float pivotY;
    private final int turn;
    private boolean rotated;
    private boolean waitingForClearance;

    /**
     * @param x position horizontale du coin bas gauche
     * @param y position verticale du coin bas gauche
     * @param orientation sens du mur avant rotation
     * @param pivotX position horizontale du point de rotation
     * @param pivotY position verticale du point de rotation
     * @param turn angle de rotation : 90 (sens inverse des aiguilles d'une montre) ou -90
     * @throws IllegalArgumentException si un paramètre n'est pas valide
     */
    public RotatingWall(float x, float y, OpeningOrientation orientation,
                        float pivotX, float pivotY, int turn) {
        super(x, y, orientation == OpeningOrientation.HORIZONTAL ? 2f : 1f,
                orientation == OpeningOrientation.HORIZONTAL ? 1f : 2f);
        if (orientation == null || !Float.isFinite(x) || !Float.isFinite(y)
                || !Float.isFinite(pivotX) || !Float.isFinite(pivotY)) {
            throw new IllegalArgumentException("La position, le pivot et l'orientation du mur doivent être renseignés.");
        }
        if (turn != 90 && turn != -90) {
            throw new IllegalArgumentException("La rotation du mur doit être 90 ou -90 degrés.");
        }
        this.initialBounds = super.getBounds();
        this.initialOrientation = orientation;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.turn = turn;
    }

    /** @return une copie de la position du mur avant rotation */
    public Bounds getInitialBounds() {
        return new Bounds(initialBounds.getX(), initialBounds.getY(),
                initialBounds.getWidth(), initialBounds.getHeight());
    }

    /** @return la position horizontale du pivot */
    public float getPivotX() { return pivotX; }
    /** @return la position verticale du pivot */
    public float getPivotY() { return pivotY; }
    /** @return l'angle de rotation (90 ou -90) */
    public int getTurn() { return turn; }
    /** @return {@code true} si le mur a déjà pivoté */
    public boolean isRotated() { return rotated; }
    /** @return le sens du mur avant rotation */
    public OpeningOrientation getInitialOrientation() { return initialOrientation; }

    /** @return la position actuelle du mur : initiale, ou tournée autour du pivot */
    @Override
    public Bounds getBounds() {
        if (!rotated) return getInitialBounds();
        float x = initialBounds.getX();
        float y = initialBounds.getY();
        float width = initialBounds.getWidth();
        float height = initialBounds.getHeight();
        // Rotation exacte à +/-90° : pas de trigonométrie ni d'erreur aux bords des cases.
        if (turn == 90) {
            return new Bounds(pivotX - (y + height - pivotY), pivotY + (x - pivotX), height, width);
        }
        return new Bounds(pivotX + (y - pivotY), pivotY - (x + width - pivotX), height, width);
    }

    /**
     * Fait pivoter le mur, une seule fois. Si quelqu'un se trouve sur sa nouvelle position,
     * le mur reste traversable jusqu'à ce qu'il en sorte.
     *
     * @param occupants les hitbox présentes (héros, monstre)
     * @return {@code true} si le mur vient de pivoter, {@code false} s'il l'avait déjà fait
     */
    public boolean rotate(Bounds... occupants) {
        if (rotated) return false;
        rotated = true;
        waitingForClearance = overlapsAny(occupants);
        setCollide(!waitingForClearance);
        return true;
    }

    /**
     * Le mur redevient solide seulement quand tous les occupants ont quitté sa nouvelle zone.
     *
     * @param occupants les hitbox présentes (héros, monstre)
     */
    public void updateClearance(Bounds... occupants) {
        if (waitingForClearance && !overlapsAny(occupants)) {
            waitingForClearance = false;
            setCollide(true);
        }
    }

    private boolean overlapsAny(Bounds[] occupants) {
        Bounds bounds = getBounds();
        for (Bounds occupant : occupants) {
            if (occupant != null && bounds.intersects(occupant)) return true;
        }
        return false;
    }
}

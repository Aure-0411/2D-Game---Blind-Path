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

    public Bounds getInitialBounds() {
        return new Bounds(initialBounds.getX(), initialBounds.getY(),
                initialBounds.getWidth(), initialBounds.getHeight());
    }

    public float getPivotX() { return pivotX; }
    public float getPivotY() { return pivotY; }
    public int getTurn() { return turn; }
    public boolean isRotated() { return rotated; }
    public OpeningOrientation getInitialOrientation() { return initialOrientation; }

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

    public boolean rotate(Bounds... occupants) {
        if (rotated) return false;
        rotated = true;
        waitingForClearance = overlapsAny(occupants);
        setCollide(!waitingForClearance);
        return true;
    }

    /** Le mur redevient solide seulement quand tous les occupants ont quitté sa nouvelle zone. */
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

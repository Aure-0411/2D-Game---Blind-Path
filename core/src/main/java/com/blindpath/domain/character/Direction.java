package com.blindpath.domain.character;

public enum Direction {
    NONE(0, 0),
    NORTH(0, 1),
    NORTH_EAST(1, 1),
    EAST(1, 0),
    SOUTH_EAST(1, -1),
    SOUTH(0, -1),
    SOUTH_WEST(-1, -1),
    WEST(-1, 0),
    NORTH_WEST(-1, 1);

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

    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public float getNormalizedX() {return normalizedX; }
    public float getNormalizedY() {return normalizedY; }


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
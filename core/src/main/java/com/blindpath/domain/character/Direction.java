package com.blindpath.domain.character;

public enum Direction {
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
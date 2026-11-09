package com.blindpath.domain.character;

public class Gnome extends Character{
    private float x, y;
    private float width, height;

    public Gnome(float startX, float startY) {
        super(0.1f, startX, startY, 1f, 1f);
    }
}
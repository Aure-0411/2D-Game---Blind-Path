package com.blindpath.domain.labyrinth;

public class Wall extends Obstacle {

    private final boolean blocksMonster;

    public Wall(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.blocksMonster = true;

    }

    public boolean blocksMonster() {
        return this.blocksMonster;
    }
}

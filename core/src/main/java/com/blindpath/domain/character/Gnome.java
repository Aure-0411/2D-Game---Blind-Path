package com.blindpath.domain.character;

public class Gnome extends Character{
    private Direction facing = Direction.SOUTH;

    public Gnome() {
        super(0.1f, 0, 0, 1f, 1f);
    }

    public Direction getFacing() { return facing; }

    public void setFacing(Direction facing) {
        if (facing != Direction.NONE) {
            this.facing = facing;
        }
    }
}
package com.blindpath.domain.labyrinth;

import java.util.ArrayList;
import java.util.List;

public class Labyrinth {

    private List<Obstacle> obstacles;
    // private List<Decoration> decorations;
    private Entrance entrance;
    private Exit exit;

    public Labyrinth(Entrance entrance, Exit exit) {
        this.obstacles = new ArrayList<>();
        // this.decorations = new ArrayList<>();
        this.entrance = entrance;
        this.exit = exit;
    }

    public List<Obstacle> geObstacles() {
        return List.copyOf(this.obstacles);
    }

    // public List<Decoration> getDecoration() {
    //     return List.copyOf(this.obstacles);
    // }

    public Entrance getEntrance() {
        return this.entrance;
    }

    public Exit getExit() {
        return this.exit;
    }
    
    // public Boolean isWalkableForMonster(GridPosition position, Bounds footprint) {
    //     return true;
    // }

    // public List<GridPosition> getNeighbors(GridPosition position, Bounds footprint ) {

    // }
}

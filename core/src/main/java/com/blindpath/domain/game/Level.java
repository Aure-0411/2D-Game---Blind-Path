package com.blindpath.domain.game;
package com.blindpath.domain.labyrinth;

import java.util.ArrayList;

import java.util.List;

public class Level {

    private Gnome gnome;
    private Labyrinth labyrinth;
    private Monster monster;
    private Pathfinder pathfinder;
    private List<Chest> chests;
    private List<PlacedObject> placedObjects;

    public Level(Gnome gnome, Labyrinth labyrinth, Monster monster, Pathfinder pathfinder) {
        if (gnome == null || labyrinth == null || pathfinder == null) {
            throw new IllegalArgumentException("Le gnome, le labyrinthe et le pathfinder doivent être renseignés.");
        }

        this.gnome = gnome;
        this.labyrinth = labyrinth;
        this.monster = monster;
        this.pathfinder = pathfinder;
        this.chests = new ArrayList<>();
        this.placedObjects = new ArrayList<>();
    }

    public void update(float deltaTime) {

    }

    public Boolean tryMove(Character character, float dx, float dy, float deltaTime) {

    }
    
    public  Boolean tryMoveMonster( Monster monster , GridPosition targer){

    }

    public void emitSnap (SnapEvent event) {

    }

    public void addPlacedObject(PlacedObject object){

    }
    
    public Boolean isCompleted (){
        
    }

}

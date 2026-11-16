package com.blindpath.domain.game;

// import java.util.ArrayList;
// import java.util.List;
import com.blindpath.domain.character.Character;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.physics.CollisionWorld;

public class Level {

    private Gnome gnome;
    private Labyrinth labyrinth;
    private final CollisionWorld collisions;
    // private Monster monster;
    // private Pathfinder pathfinder;
    // private List<Chest> chests;
    // private List<PlacedObject> placedObjects;

    public Level(Gnome gnome, Labyrinth labyrinth, CollisionWorld collisions/*, Monster monster, Pathfinder pathfinder*/) {
        if (gnome == null || labyrinth == null || collisions == null /*|| pathfinder == null*/) {
            throw new IllegalArgumentException("Le gnome, le labyrinthe, les collisions et le pathfinder doivent être renseignés.");
        }

        this.gnome = gnome;
        this.labyrinth = labyrinth;
        this.collisions = collisions;
        /*this.monster = monster;
        this.pathfinder = pathfinder;
        this.chests = new ArrayList<>();
        this.placedObjects = new ArrayList<>();*/

        for (Obstacle obstacle : labyrinth.getObstacles()) {
            if (obstacle.canCollide()) {
                collisions.addStatic(obstacle.getBounds());
            }
        }
        for (Obstacle wall : labyrinth.getBoundaryWalls()) {
            collisions.addStatic(wall.getBounds());
        }
        collisions.addDynamic(gnome);
    }
    
    public Gnome getGnome() { return gnome; }
        
    public Labyrinth getLabyrinth() { return labyrinth; }

    public void update(float deltaTime) {
        // Plus tard : lumière, monstre, objets posés...
    }

    /** Déplace le personnage en respectant les murs. Renvoie true s'il a bougé. */
    public boolean tryMove(Character character, float dx, float dy, float deltaTime) {
        if (character == null) {
            throw new IllegalArgumentException("Le personnage doit être renseigné.");
        }
        return collisions.move(character, dx, dy, deltaTime);
    }

    
    
    // public  Boolean tryMoveMonster( Monster monster , GridPosition target){

    // }

    // public void emitSnap (SnapEvent event) {

    // }

    // public void addPlacedObject(PlacedObject object){

    // }
    
    // public Boolean isCompleted (){

    // }

}
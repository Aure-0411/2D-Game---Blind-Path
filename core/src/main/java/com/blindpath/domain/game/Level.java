package com.blindpath.domain.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.chest.Chest;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.object.PlacedObject;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.CollisionWorld;
import com.blindpath.domain.monster.Inverter;

public class Level {

    private final Gnome gnome;
    private final Labyrinth labyrinth;
    private final CollisionWorld collisions;
    private Monster monster;
    private final Pathfinder pathfinder;
    private final List<Chest> chests;
    private final List<PlacedObject> placedObjects;

    public Level(Gnome gnome, Labyrinth labyrinth, CollisionWorld collisions, Monster monster, Pathfinder pathfinder, List<Chest> chests) {
        if (gnome == null || labyrinth == null || collisions == null || monster == null || pathfinder == null) {
            throw new IllegalArgumentException("Le gnome, le labyrinthe, les collisions, le monstre et le pathfinder doivent être renseignés.");
        }

        this.gnome = gnome;
        this.labyrinth = labyrinth;
        this.collisions = collisions;
        this.monster = monster;
        this.pathfinder = pathfinder;
        this.chests = (chests != null) ? chests : Collections.emptyList();
        this.placedObjects = new ArrayList<>();

        for (Obstacle obstacle : labyrinth.getObstacles()) {
            if (obstacle.canCollide()) {
                collisions.addStatic(obstacle.getBounds());
            }
        }
        for (Obstacle wall : labyrinth.getBoundaryWalls()) {
            collisions.addStatic(wall.getBounds());
        }
        for (Chest chest : this.chests) {
            chest.setOpener(gnome);
            if (chest.canCollide()) {
                collisions.addStatic(chest.getBounds());
            }
        }

        collisions.addDynamic(gnome);
    }

    public Gnome getGnome() { return gnome; }
    public Labyrinth getLabyrinth() { return labyrinth; }
    public Monster getMonster() { return monster; }
    public void setMonster(Monster monster) { this.monster = monster; }
    public List<Chest> getChests() { return chests; }
    public Pathfinder getPathfinder() { return pathfinder; }
    public List<PlacedObject> getPlacedObjects() { return placedObjects; }

    public void update(float deltaTime) {
        gnome.updateImmunity(deltaTime);
        if (monster != null && gnome.collide(monster)) {
            gnome.takeMonsterHit();
        }
        for (int i = placedObjects.size() - 1; i >= 0; i--) {
            PlacedObject object = placedObjects.get(i);
            if (monster != null && object.collide(monster)) {
                object.onTouch(monster);
                placedObjects.remove(i);
            }
        }
    }

    /** Déplace le personnage en respectant les murs. Renvoie true s'il a bougé. */
    public boolean tryMove(Character character, float dx, float dy, float deltaTime) {
        if (character == null) {
            throw new IllegalArgumentException("Le personnage doit être renseigné.");
        }
        return collisions.move(character, dx, dy, deltaTime);
    }

    public void emitSnap(SnapEvent event) {
        if (event == null) {
            return;
        }
        if (monster != null) {
            monster.onSnap(event);
        }
        for (Chest chest : chests) {
            chest.onSnap(event);
        }
    }

    public void addPlacedObject(PlacedObject object) {
        if (object == null) {
            throw new IllegalArgumentException("L'objet doit être renseigné.");
        }
        this.placedObjects.add(object);
    }

    public boolean areControlsInverted() {
        return monster instanceof Inverter inverter && inverter.invertsControls();
    }

    public boolean isCompleted() {
        return labyrinth.getExit().isReached(gnome);
    }
}
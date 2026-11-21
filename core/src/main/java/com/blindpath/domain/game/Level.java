package com.blindpath.domain.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.chest.Chest;
import com.blindpath.domain.labyrinth.Labyrinth;
import com.blindpath.domain.labyrinth.Bramble;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.labyrinth.RotatingWall;
import com.blindpath.domain.labyrinth.Lever;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.object.PlacedObject;
import com.blindpath.domain.pathfinding.Pathfinder;
import com.blindpath.domain.physics.CollisionWorld;
import com.blindpath.domain.monster.Screamer;
import com.blindpath.domain.monster.Inverter;

public class Level<T extends Character> {

    private final T hero;
    private final Labyrinth labyrinth;
    private final CollisionWorld collisions;
    private Monster monster;
    private final Pathfinder pathfinder;
    private final List<Chest> chests;
    private final List<PlacedObject> placedObjects;

    public Level(T hero, Labyrinth labyrinth, CollisionWorld collisions, Monster monster, Pathfinder pathfinder, List<Chest> chests) {
        if (hero == null || labyrinth == null || collisions == null || monster == null || pathfinder == null) {
            throw new IllegalArgumentException("Le personnnage, le labyrinthe, les collisions, le monstre et le pathfinder doivent être renseignés.");
        }

        this.hero = hero;
        this.labyrinth = labyrinth;
        this.collisions = collisions;
        this.monster = monster;
        this.pathfinder = pathfinder;
        this.chests = (chests != null) ? chests : Collections.emptyList();
        this.placedObjects = new ArrayList<>();

        for (Obstacle obstacle : labyrinth.getObstacles()) {
            if (obstacle instanceof RotatingWall) {
                collisions.addChangingObstacle(obstacle);
            } else if (obstacle.canCollide()) {
                collisions.addStatic(obstacle.getBounds());
            }
        }
        for (Obstacle wall : labyrinth.getBoundaryWalls()) {
            collisions.addStatic(wall.getBounds());
        }
        for (Chest chest : this.chests) {
            chest.setOpener(hero);
            if (chest.canCollide()) {
                collisions.addStatic(chest.getBounds());
            }
        }

        collisions.addDynamic(hero);
    }

    public T getHero() { return hero; }
    public Labyrinth getLabyrinth() { return labyrinth; }
    public Monster getMonster() { return monster; }
    public void setMonster(Monster monster) { this.monster = monster; }
    public List<Chest> getChests() { return chests; }
    public Pathfinder getPathfinder() { return pathfinder; }
    public List<PlacedObject> getPlacedObjects() { return placedObjects; }

    public void update(float deltaTime) {
        updateRotatingWalls();
        hero.updateCompass(deltaTime);
        hero.updateImmunity(deltaTime);
        hero.updateStun(deltaTime);
        if (monster != null && hero.collide(monster)) {
            hero.takeMonsterHit();
        }
        for (Bramble bramble : labyrinth.getBrambles()) {
            bramble.update(deltaTime);
            if (bramble.damages(hero.getBounds())) {
                hero.takeDamage(Bramble.DAMAGE);
            }
        }
        // Déclenche le soin automatique après les dégâts ; ne ressuscite pas le joueur.
        hero.getInventory().useHealthNeeded(hero, this);
        for (int i = placedObjects.size() - 1; i >= 0; i--) {
            PlacedObject object = placedObjects.get(i);
            object.update(deltaTime);
            if (monster != null && object.collide(monster)) {
                object.onTouch(monster);
            }
            if (object.isDone()) {
                placedObjects.remove(i);
            }
        }
    }

    /** Déplace le personnage en respectant les murs. Renvoie true s'il a bougé. */
    public boolean tryMove(Character character, float dx, float dy, float deltaTime) {
        if (character == null) {
            throw new IllegalArgumentException("Le personnage doit être renseigné.");
        }

        if (character.isStunned()) {
            return false;
        }

        updateRotatingWalls();
        boolean moved = collisions.move(character, dx, dy, deltaTime);
        updateRotatingWalls();
        return moved;
    }

    public void emitSnap(SnapEvent event) {
        if (event == null) {
            return;
        }
        for (Bramble bramble : labyrinth.getBrambles()) {
            bramble.onSnap(event);
        }
        for (Lever lever : labyrinth.getLevers()) {
            lever.onSnap(event, hero.getBounds(), monster == null ? null : monster.getBounds());
        }
        updateRotatingWalls();
        
        if (monster != null) {
            monster.onSnap(event);
        }

        if (monster instanceof Screamer screamer && screamer.consumeScream()) {
            hero.stun(Screamer.STUN_DURATION);
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

    private void updateRotatingWalls() {
        for (RotatingWall wall : labyrinth.getRotatingWalls()) {
            wall.updateClearance(hero.getBounds(), monster == null ? null : monster.getBounds());
        }
        collisions.syncChangingObstacles();
    }

    public boolean areControlsInverted() {
        return monster instanceof Inverter inverter && inverter.invertsControls();
    }

    public boolean isCompleted() {
        return labyrinth.getExit().isReached(hero);
    }
}
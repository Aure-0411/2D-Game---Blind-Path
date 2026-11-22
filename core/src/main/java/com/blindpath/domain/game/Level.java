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

/**
 * Un niveau en cours : le héros, le labyrinthe, son monstre, les coffres et les objets posés.
 * <p>
 * À la construction, le niveau enregistre tous les obstacles, les murs du bord et les coffres
 * dans le monde de collisions. À chaque image, {@link #update(float)} fait avancer les effets
 * temporaires et applique les dégâts.
 *
 * @param <T> le type du héros
 */
public class Level<T extends Character> {

    private final T hero;
    private final Labyrinth labyrinth;
    private final CollisionWorld collisions;
    private Monster monster;
    private final Pathfinder pathfinder;
    private final List<Chest> chests;
    private final List<PlacedObject> placedObjects;

    /**
     * Construit le niveau et prépare les collisions.
     *
     * @param hero le héros
     * @param labyrinth le labyrinthe chargé depuis la carte
     * @param collisions le monde de collisions
     * @param monster le monstre du niveau
     * @param pathfinder le calcul de chemin utilisé par le monstre
     * @param chests les coffres du niveau ({@code null} pour aucun)
     * @throws IllegalArgumentException si le héros, le labyrinthe, les collisions,
     *         le monstre ou le pathfinder est {@code null}
     */
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

    /** @return le héros */
    public T getHero() { return hero; }
    /** @return le labyrinthe du niveau */
    public Labyrinth getLabyrinth() { return labyrinth; }
    /** @return le monstre du niveau */
    public Monster getMonster() { return monster; }
    /**
     * Remplace le monstre du niveau.
     *
     * @param monster le nouveau monstre
     */
    public void setMonster(Monster monster) { this.monster = monster; }
    /** @return les coffres du niveau */
    public List<Chest> getChests() { return chests; }
    /** @return le calcul de chemin du niveau */
    public Pathfinder getPathfinder() { return pathfinder; }
    /** @return les objets posés par le joueur (piège à ours, dynamite) */
    public List<PlacedObject> getPlacedObjects() { return placedObjects; }

    /**
     * Fait avancer le niveau d'une image : murs tournants, effets du héros, contact avec
     * le monstre, ronces, soin automatique et objets posés. Les objets terminés sont retirés.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
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

    /**
     * Déplace un personnage en respectant les murs. Un personnage étourdi ne bouge pas.
     *
     * @param character le personnage à déplacer
     * @param dx composante horizontale de la direction
     * @param dy composante verticale de la direction
     * @param deltaTime temps écoulé, en secondes
     * @return {@code true} si le personnage a bougé
     * @throws IllegalArgumentException si le personnage est {@code null}
     */
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

    /**
     * Diffuse un claquement de doigts à tout ce qui y réagit : ronces, leviers, monstre et coffres.
     * Si le monstre est un Screamer qui crie, le héros est étourdi.
     *
     * @param event le claquement ({@code null} est ignoré)
     */
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

    /**
     * Ajoute un objet posé par le joueur dans le niveau.
     *
     * @param object l'objet posé
     * @throws IllegalArgumentException si l'objet est {@code null}
     */
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

    /** @return {@code true} si le monstre est un Inverter qui inverse actuellement les commandes */
    public boolean areControlsInverted() {
        return monster instanceof Inverter inverter && inverter.invertsControls();
    }

    /** @return {@code true} si le héros a atteint la sortie */
    public boolean isCompleted() {
        return labyrinth.getExit().isReached(hero);
    }
}
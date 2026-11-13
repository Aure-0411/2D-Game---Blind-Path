package com.blindpath.domain.physics;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Bounds;

/**
 * Contrat de collision du domaine. Aucune dépendance à LibGDX : l'implémentation
 * Box2D vit dans infrastructure.physics, une version Java pure sert aux tests.
 *
 * La position du Character reste la vérité du domaine : le moteur la lit, la corrige
 * et la réécrit.
 */
public interface CollisionWorld {

    /** Déclare un obstacle fixe (mur). */
    void addStatic(Bounds bounds);

    /** Déclare un personnage mobile soumis aux collisions. */
    void addDynamic(Character character);

    /**
     * Déplace le personnage dans la direction donnée pendant deltaTime, en s'arrêtant
     * (ou en glissant) contre les obstacles.
     *
     * @return true si la position a réellement changé
     */
    boolean move(Character character, float directionX, float directionY, float deltaTime);

    /** Libère les ressources natives éventuelles. */
    void dispose();
}

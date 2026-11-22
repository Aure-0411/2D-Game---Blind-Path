package com.blindpath.domain.character;

/** Tout ce qui peut se déplacer dans le labyrinthe. */
public interface Movable {
    /**
     * Déplace l'objet dans une direction pendant un temps donné.
     *
     * @param directionX composante horizontale de la direction
     * @param directionY composante verticale de la direction
     * @param deltaTime temps écoulé, en secondes
     */
    void move(float directionX, float directionY, float deltaTime);
}
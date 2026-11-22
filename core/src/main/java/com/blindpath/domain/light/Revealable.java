package com.blindpath.domain.light;

/** Ce qui peut révéler le labyrinthe par un claquement de doigts. */
public interface Revealable {
    /**
     * Déclenche un claquement.
     *
     * @return {@code true} si le claquement a eu lieu
     */
    boolean snap();
}
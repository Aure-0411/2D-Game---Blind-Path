package com.blindpath.domain.labyrinth;

import java.util.List;

import com.blindpath.domain.inventory.ItemType;
import com.blindpath.domain.monster.MonsterType;
import com.blindpath.domain.pathfinding.GridPosition;

/**
 * Charge un labyrinthe et ses éléments depuis un fichier de carte.
 * Le domaine ne dépend pas du format : l'implémentation Tiled est dans l'infrastructure.
 */
public interface LabyrinthLoader {

    /**
     * Emplacement d'un coffre lu dans la carte.
     *
     * @param x position horizontale
     * @param y position verticale
     * @param orientation le mur contre lequel le coffre est posé
     * @param fixedLoot le contenu imposé par la carte, ou {@code null} pour un tirage au hasard
     */
    record ChestSpawn(float x, float y, Border orientation, ItemType fixedLoot) {
        /**
         * Emplacement d'un coffre au contenu tiré au hasard.
         *
         * @param x position horizontale
         * @param y position verticale
         * @param orientation le mur contre lequel le coffre est posé
         */
        public ChestSpawn(float x, float y, Border orientation) {
            this(x, y, orientation, null);
        }
    }

    /**
     * Tout ce qu'on lit d'une carte en une fois.
     *
     * @param labyrinth le labyrinthe
     * @param chestSpawns les emplacements des coffres
     * @param monsterSpawn la case de départ du monstre
     * @param monsterType le monstre imposé par la carte, ou {@code null} pour celui du palier
     */
    record LabyrinthData(
        Labyrinth labyrinth,
        List<ChestSpawn> chestSpawns,
        GridPosition monsterSpawn,
        MonsterType monsterType
    ) {}    

    /**
     * @param path chemin de la carte
     * @return le labyrinthe décrit par la carte
     */
    Labyrinth load(String path);

    /**
     * @param path chemin de la carte
     * @return les emplacements des coffres (par défaut, un coffre en (1, 1))
     */
    default List<ChestSpawn> loadChestSpawns(String path) {
        return List.of(new ChestSpawn(1f, 1f, Border.BOTTOM));
    }

    /**
     * @param path chemin de la carte
     * @return le monstre imposé par la carte, ou {@code null} pour utiliser celui du palier
     */
    default MonsterType loadMonsterType(String path) {
        return null; // null = utilise le MonsterType du palier par défaut
    }

    /**
     * @param path chemin de la carte
     * @return la case de départ du monstre (par défaut, (5, 5))
     */
    default GridPosition loadMonsterSpawn(String path) {
        return new GridPosition(5, 5);
    }

    /**
     * Charge la carte et tous ses éléments.
     *
     * @param path chemin de la carte
     * @return le labyrinthe, les coffres, la case et le type du monstre
     */
    default LabyrinthData loadAll(String path) {
        return new LabyrinthData(
            load(path),
            loadChestSpawns(path),
            loadMonsterSpawn(path),
            loadMonsterType(path)
        );
    }    
}
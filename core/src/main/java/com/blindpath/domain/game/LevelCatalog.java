package com.blindpath.domain.game;

/** Liste des niveaux du jeu et de leur découpage en paliers. */
public interface LevelCatalog {
    /**
     * @param levelNumber le numéro du niveau, à partir de 1
     * @return la configuration de ce niveau
     */
    LevelConfig getLevel(int levelNumber);
    /** @return le nombre total de niveaux */
    int totalLevels();
    /**
     * @param levelNumber le numéro d'un niveau
     * @return le numéro du premier niveau de son palier (point de reprise après une mort)
     */
    int tierStartLevel(int levelNumber);
}
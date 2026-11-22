package com.blindpath.domain.game;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.chest.Chest;

/**
 * Une partie en cours : le niveau actuel, le héros et le nombre de morts.
 * <p>
 * Les coffres de chaque niveau sont créés une seule fois et gardés en mémoire,
 * pour qu'un coffre déjà ouvert le reste après un retry. Après une mort, la partie
 * reprend au premier niveau du palier en cours.
 *
 * @param <T> le type du héros
 */
public class GameSession<T extends Character> {

    private final LevelFactory factory;
    private final T hero;
    private final Map<Integer, List<Chest>> chestsByLevel = new HashMap<>();
    private int currentLevelNumber = 1;
    private int retryCount;
    private Level<T> level;
    private boolean won;

    /**
     * Crée une partie. Aucun niveau n'est chargé avant {@link #startLevel(int)}.
     *
     * @param factory la fabrique qui construit les niveaux
     * @param hero le héros de la partie
     * @throws IllegalArgumentException si l'un des paramètres est {@code null}
     */
    public GameSession(LevelFactory factory, T hero) {
        if (factory == null || hero == null) {
            throw new IllegalArgumentException("La factory et le joueur doivent être renseignés.");
        }
        this.factory = factory;
        this.hero = hero;
    }

    /**
     * Charge et démarre un niveau.
     *
     * @param number le numéro du niveau, à partir de 1
     */
    public void startLevel(int number) {
        currentLevelNumber = number;
        won = false;
        List<Chest> chests = chestsByLevel.computeIfAbsent(number, factory::createChests);
        level = factory.create(number, hero, chests);
    }

    /** Passe au niveau suivant, ou marque la partie gagnée après le dernier niveau. */
    public void nextLevel() {
        if (currentLevelNumber >= factory.totalLevels()) {
            won = true;
            return;
        }
        startLevel(currentLevelNumber + 1);
    }

        /** Recommence après une mort : remet le héros en état et repart du premier niveau du palier. */
        public void retry() {
        retryCount++;
        hero.resetForRetry();
        int checkpointLevel = factory.tierStartFor(currentLevelNumber);
        startLevel(checkpointLevel);
    }


    /** @return le héros de la partie */
    public T getHero() { return hero; }
    /** @return le niveau en cours, ou {@code null} avant le premier {@link #startLevel(int)} */
    public Level<T> getLevel() { return level; }
    /** @return le numéro du niveau en cours */
    public int getCurrentLevelNumber() { return currentLevelNumber; }
    /** @return le nombre de retries depuis le début de la partie */
    public int getRetryCount() { return retryCount; }
    /** @return {@code true} si le dernier niveau a été terminé */
    public boolean isWon() { return won; }
}
package com.blindpath.presentation.input;

import java.util.function.IntPredicate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.blindpath.domain.character.Character;
import com.blindpath.domain.character.Direction;
import com.blindpath.domain.game.Level;

/**
 * Lit le clavier et le traduit en actions : déplacement en 8 directions (touches W, A, S, D ou flèches)
 * et utilisation des objets de l'inventaire (touches 1 à 5, pavé numérique compris).
 * Les commandes sont inversées quand l'Inverter est proche.
 */
public class InputController {

    private static final int[] SLOT_KEYS = { Keys.NUM_1, Keys.NUM_2, Keys.NUM_3, Keys.NUM_4, Keys.NUM_5 };
    private static final int[] NUMPAD_SLOT_KEYS = { Keys.NUMPAD_1, Keys.NUMPAD_2, Keys.NUMPAD_3, Keys.NUMPAD_4, Keys.NUMPAD_5 };

    private final Level<?> level;
    private final Character character;
    private final IntPredicate keyDown;
    private final IntPredicate keyJustPressed;
    private boolean moving;

    /**
     * Contrôle le héros du niveau avec le vrai clavier.
     *
     * @param level le niveau en cours
     */
    public InputController(Level<?> level) {
        this(level, level.getHero());
    }

    /**
     * Contrôle un personnage avec le vrai clavier.
     *
     * @param level le niveau en cours
     * @param character le personnage contrôlé
     */
    public InputController(Level<?> level, Character character) {
        this(level, character, key -> Gdx.input.isKeyPressed(key), key -> Gdx.input.isKeyJustPressed(key));
    }

    /**
     * Contrôle le héros avec un clavier simulé, sans touche « juste pressée » (utile pour les tests).
     *
     * @param level le niveau en cours
     * @param keyDown indique si une touche est enfoncée
     */
    public InputController(Level<?> level, IntPredicate keyDown) {
        this(level, level.getHero(), keyDown, key -> false);
    }

    /**
     * @param level le niveau en cours
     * @param character le personnage contrôlé
     * @param keyDown indique si une touche est enfoncée
     * @param keyJustPressed indique si une touche vient d'être pressée
     * @throws IllegalArgumentException si le niveau ou le personnage est {@code null}
     */
    public InputController(Level<?> level, Character character, IntPredicate keyDown, IntPredicate keyJustPressed) {
        if (level == null || character == null) {
            throw new IllegalArgumentException("Le niveau et le personnage doivent être renseignés.");
        }
        this.level = level;
        this.character = character;
        this.keyDown = keyDown;
        this.keyJustPressed = keyJustPressed;
    }

    /**
     * @param keyDown indique si une touche est enfoncée
     * @return la direction demandée par le joueur
     */
    public static Direction readDirection(IntPredicate keyDown) {
        return readDirection(keyDown, false);
    }

    /**
     * @param keyDown indique si une touche est enfoncée
     * @param inverted {@code true} pour inverser haut/bas et gauche/droite
     * @return la direction demandée par le joueur
     */
    public static Direction readDirection(IntPredicate keyDown, boolean inverted) {
        boolean up = keyDown.test(Keys.W) || keyDown.test(Keys.UP);
        boolean down = keyDown.test(Keys.S) || keyDown.test(Keys.DOWN);
        boolean left = keyDown.test(Keys.A) || keyDown.test(Keys.LEFT);
        boolean right = keyDown.test(Keys.D) || keyDown.test(Keys.RIGHT);
        if (inverted) {
            return Direction.fromKeys(down, up, right, left);
        }
        return Direction.fromKeys(up, down, left, right);
    }

    /**
     * Traite les touches de l'image en cours : objets de l'inventaire, puis déplacement.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    public void handleInput(float deltaTime) {
        handleInventoryInput();

        Direction direction = readDirection(keyDown, level.areControlsInverted());

        if (direction != Direction.NONE) {
            character.setFacing(direction);
            moving = level.tryMove(character, direction.getNormalizedX(), direction.getNormalizedY(), deltaTime);
        } else {
            moving = false;
            character.setFacing(Direction.DOWN);
        }
    }

    private void handleInventoryInput() {
        for (int slot = 0; slot < SLOT_KEYS.length; slot++) {
            if (keyJustPressed.test(SLOT_KEYS[slot]) || keyJustPressed.test(NUMPAD_SLOT_KEYS[slot])) {
                if (slot < character.getInventory().getStacks().size()) {
                    character.getInventory().use(slot, character, level);
                }
            }
        }
    }

    /**
     * @return {@code true} si le personnage a réellement avancé à la dernière image
     *         ({@code false} s'il pousse contre un mur)
     */
    public boolean isMoving() {
        return moving;
    }
}
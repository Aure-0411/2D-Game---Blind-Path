package com.blindpath.presentation.input;

import java.util.function.IntPredicate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.blindpath.domain.character.Character;
import com.blindpath.domain.character.Direction;
import com.blindpath.domain.game.Level;

public class InputController {

    private static final int[] SLOT_KEYS = { Keys.NUM_1, Keys.NUM_2, Keys.NUM_3, Keys.NUM_4, Keys.NUM_5 };
    private static final int[] NUMPAD_SLOT_KEYS = { Keys.NUMPAD_1, Keys.NUMPAD_2, Keys.NUMPAD_3, Keys.NUMPAD_4, Keys.NUMPAD_5 };

    private final Level<?> level;
    private final Character character;
    private final IntPredicate keyDown;
    private final IntPredicate keyJustPressed;
    private boolean moving;

    public InputController(Level<?> level) {
        this(level, level.getHero());
    }

    public InputController(Level<?> level, Character character) {
        this(level, character, key -> Gdx.input.isKeyPressed(key), key -> Gdx.input.isKeyJustPressed(key));
    }

    public InputController(Level<?> level, IntPredicate keyDown) {
        this(level, level.getHero(), keyDown, key -> false);
    }

    public InputController(Level<?> level, Character character, IntPredicate keyDown, IntPredicate keyJustPressed) {
        if (level == null || character == null) {
            throw new IllegalArgumentException("Le niveau et le personnage doivent être renseignés.");
        }
        this.level = level;
        this.character = character;
        this.keyDown = keyDown;
        this.keyJustPressed = keyJustPressed;
    }

    public static Direction readDirection(IntPredicate keyDown) {
        return readDirection(keyDown, false);
    }

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

    /** Vrai si le personnage a réellement avancé à la dernière image (faux s'il pousse contre un mur). */
    public boolean isMoving() {
        return moving;
    }
}
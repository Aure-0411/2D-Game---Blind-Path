package com.blindpath.presentation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.blindpath.domain.character.Direction;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;
import java.util.function.IntPredicate;

public class InputController {

    private final Level level;
    private final IntPredicate keyDown;
    private boolean moving;

    public InputController(Level level) {
        this(level, key -> Gdx.input.isKeyPressed(key));
    }

    public InputController(Level level, IntPredicate keyDown) {
        this.level = level;
        this.keyDown = keyDown;
    }

    public static Direction readDirection(IntPredicate keyDown) {
        boolean up = keyDown.test(Keys.W) || keyDown.test(Keys.UP);
        boolean down = keyDown.test(Keys.S) || keyDown.test(Keys.DOWN);
        boolean left = keyDown.test(Keys.A) || keyDown.test(Keys.LEFT);
        boolean right = keyDown.test(Keys.D) || keyDown.test(Keys.RIGHT);
        return Direction.fromKeys(up, down, left, right);
    }

    public void handleInput(float deltaTime) {
        Direction direction = readDirection(keyDown);
        Gnome gnome = level.getGnome();

        if (direction != Direction.NONE) {
            gnome.setFacing(direction);
            moving = level.tryMove(gnome, direction.getNormalizedX(), direction.getNormalizedY(), deltaTime);
        } else {
            moving = false;
            gnome.setFacing(Direction.DOWN); // Retour à l'orientation par défaut quand tout est relâché
        }
    }

    /** Vrai si le gnome a réellement avancé à la dernière image (faux s'il pousse contre un mur). */
    public boolean isMoving() {
        return moving;
    }
}
package com.blindpath.presentation.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.blindpath.domain.monster.Monster;
import com.blindpath.domain.monster.Jumper;
import com.blindpath.domain.monster.Screamer;
import com.blindpath.domain.monster.JumperState;
import com.blindpath.domain.pathfinding.GridPosition;
import java.util.HashMap;
import java.util.Map;

/**
 * Dessine un monstre à partir de son atlas ({@code monsters/<name>/<name>.atlas}).
 * Générique : le même renderer sert pour tous les monstres qui suivent la
 * convention de nommage {@code <name>_<état>_<orientation>}.
 */
public class MonsterRenderer implements Disposable {
    // Même taille que la hitbox du monstre (Monster.SIZE = 2 cases) : ce qu'on voit = ce qui touche.
    private static final float SIZE = Monster.SIZE;
    private static final String[] STATES = {"move", "idle","stunned", "scream"};
    private static final float[] DURATIONS = {0.14f, 0.35f, 0.18f, 0.15f};
    private static final String[] ORIENTATION = {"left", "right", "up_left", "up_right", "down_left", "down_right"};
    private final TextureAtlas atlas;
    private final Map<String, Animation<TextureRegion>> animations = new HashMap<>();
    private GridPosition lastPosition;
    private String lastHorizontal = "left";
    private String direction = "left";
    /**
     * Charge l'atlas du monstre et prépare ses animations (déplacement, attente, étourdi, cri).
     *
     * @param name le nom du monstre, qui est aussi le nom de son dossier et de son atlas
     * @throws IllegalStateException si une animation obligatoire manque dans l'atlas
     */
    public MonsterRenderer(String name) {
        atlas = new TextureAtlas("monsters/" + name + "/" + name + ".atlas");
        for (int i = 0; i < STATES.length; i++) {
            for (String orientation : ORIENTATION) {
                String key = STATES[i] + "_" + orientation;
                Array<AtlasRegion> frames = atlas.findRegions(name + "_" + key);
                if (frames.isEmpty() && STATES[i].equals("scream")) continue;
                if (frames.isEmpty()) throw new IllegalStateException("nothing found for : " + name + "_" + key);
                animations.put(key, new Animation<>(DURATIONS[i], frames, Animation.PlayMode.LOOP));
            }
        }
    }
    private void updateOrientation(GridPosition currentPosition) {
        if (lastPosition != null) {
            int dx = currentPosition.getColumn() - lastPosition.getColumn();
            int dy = currentPosition.getRow() - lastPosition.getRow();
            if (dx > 0) { lastHorizontal = "right"; direction = "right"; }
            else if (dx < 0) { lastHorizontal = "left"; direction = "left"; }
            else if (dy > 0) direction = "up_" + lastHorizontal;
            else if (dy < 0) direction = "down_" + lastHorizontal;
        }
        lastPosition = currentPosition;
    }
    /**
     * Dessine le monstre sur son bloc de 2 x 2 cases. L'animation dépend de son état :
     * étourdi s'il est immobilisé, cri pour le Screamer, attente pour le Jumper en alerte,
     * déplacement sinon. L'orientation suit son dernier mouvement.
     *
     * @param batch le batch de dessin, déjà ouvert
     * @param monster le monstre
     * @param stateTime temps écoulé depuis le début de l'animation, en secondes
     */
    public void render(SpriteBatch batch, Monster monster, float stateTime) {
        GridPosition currentPosition = monster.getPosition();
        updateOrientation(currentPosition);
        String state = "move";
        if (monster instanceof Jumper jumper && jumper.getState() == JumperState.ALERT) state = "idle";

        if (monster instanceof Screamer screamer && screamer.isScreaming()) state = "scream";

        if (monster.isImmobilized()) {
            state = "stunned";
        }

        TextureRegion frame = animations.get(state + "_" + direction).getKeyFrame(stateTime, true);
        // La position du monstre est la case en bas à gauche de son bloc SIZE x SIZE
        // (comme sa hitbox, Monster.getBounds()). Le sprite recouvre exactement ce bloc :
        // centre du sprite = centre de la hitbox, et le pathfinding garantit que le bloc
        // ne touche aucun mur.
        float x = currentPosition.getColumn();
        float y = currentPosition.getRow();
        batch.draw(frame, x, y, SIZE, SIZE);
    }
    /** Libère l'atlas. */
    @Override public void dispose() { atlas.dispose(); }
}

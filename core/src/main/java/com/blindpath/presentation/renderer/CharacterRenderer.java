package com.blindpath.presentation.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.blindpath.domain.character.Character;
import com.blindpath.domain.character.Direction;
import com.blindpath.domain.character.Gnome;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class CharacterRenderer<T extends Character> implements Disposable {

    private final TextureAtlas atlas;
    private final Map<Direction, Animation<TextureRegion>> animations = new EnumMap<>(Direction.class);
    private final Direction defaultDirection;

    public CharacterRenderer(String ATLAS_PATH, Function<Direction, String> regionNameResolver, Direction defaultDirection, float frameDuration) {
        this.atlas = new TextureAtlas(ATLAS_PATH);
        this.defaultDirection = defaultDirection;
        for (Direction d : Direction.values()) {
            if (d == Direction.NONE) continue;

            String regionName = regionNameResolver.apply(d);
            // findRegions (au pluriel) récupère automatiquement les frames 0, 1, 2, 3
            Array<AtlasRegion> frames = atlas.findRegions(regionName);
            
            if (frames.isEmpty()) {
                throw new IllegalStateException("Aucune frame trouvée pour : " + regionName);
            }

            animations.put(d, new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP));
        }
    }

    public void render(SpriteBatch batch, T character, float stateTime, boolean isMoving) {
        Direction facing = character.getFacing();
        if (facing == Direction.NONE) {
            facing = defaultDirection;
        }
        
        Animation<TextureRegion> anim = animations.get(facing);
        if (anim != null) {
            // Si le personnage bouge, on joue l'animation. Sinon, on affiche la première frame (0)
            TextureRegion currentFrame = isMoving ? anim.getKeyFrame(stateTime, true) : anim.getKeyFrame(0);
            batch.draw(currentFrame, character.getX(), character.getY(), character.getWidth(), character.getHeight());
        }
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
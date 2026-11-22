package com.blindpath.infrastructure.physics;

import java.util.IdentityHashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Obstacle;
import com.blindpath.domain.physics.CollisionWorld;

/**
 * Seule classe du projet qui connaît Box2D. Le domaine ne voit que {@link CollisionWorld}.
 * Sans gravité (vue de dessus). 1 unité Box2D = 1 case = 1 unité du monde : aucune conversion.
 */
public class Box2DCollisionWorld implements CollisionWorld {

    private static final float MAX_STEP = 1f / 30f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final float MOVE_EPSILON = 1e-6f;

    static {
        Box2D.init(); // charge les natives avant le premier World
    }

    private final World world = new World(new Vector2(0f, 0f), true);
    private final Map<Character, Body> bodies = new IdentityHashMap<>();
    private record ChangingBody(Body body, Bounds bounds) {}
    private final Map<Obstacle, ChangingBody> changingBodies = new IdentityHashMap<>();

    @Override
    public void addStatic(Bounds bounds) {
        createStaticBody(bounds);
    }

    private Body createStaticBody(Bounds bounds) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(bounds.getCenterX(), bounds.getCenterY());
        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bounds.getWidth() / 2f, bounds.getHeight() / 2f);
        body.createFixture(shape, 0f);
        shape.dispose();
        return body;
    }

    @Override
    public void addChangingObstacle(Obstacle obstacle) {
        if (obstacle == null || changingBodies.containsKey(obstacle)) {
            throw new IllegalArgumentException("L'obstacle doit être renseigné et enregistré une seule fois.");
        }
        Bounds bounds = obstacle.getBounds();
        Body body = createStaticBody(bounds);
        body.setActive(obstacle.canCollide());
        changingBodies.put(obstacle, new ChangingBody(body, bounds));
    }

    @Override
    public void syncChangingObstacles() {
        for (var entry : changingBodies.entrySet()) {
            Obstacle obstacle = entry.getKey();
            ChangingBody previous = entry.getValue();
            Bounds bounds = obstacle.getBounds();
            Bounds old = previous.bounds();
            Body body = previous.body();
            if (old.getX() != bounds.getX() || old.getY() != bounds.getY()
                    || old.getWidth() != bounds.getWidth() || old.getHeight() != bounds.getHeight()) {
                world.destroyBody(body);
                body = createStaticBody(bounds);
                entry.setValue(new ChangingBody(body, bounds));
            }
            if (body.isActive() != obstacle.canCollide()) body.setActive(obstacle.canCollide());
        }
    }

    @Override
    public void addDynamic(Character character) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.fixedRotation = true;
        def.allowSleep = false;
        def.position.set(centerX(character), centerY(character));
        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.set(chamferedBox(character.getWidth() / 2f, character.getHeight() / 2f));
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = 1f;
        fixture.friction = 0f;     // glisse le long des murs au lieu de s'y accrocher
        fixture.restitution = 0f;  // pas de rebond
        body.createFixture(fixture);
        shape.dispose();

        bodies.put(character, body);
    }

    /**
     * Recale le corps Box2D sur la position du personnage, applique la vitesse demandée,
     * fait avancer la simulation d'un pas, puis recopie la nouvelle position dans le personnage.
     *
     * @param character le personnage, enregistré avec {@link #addDynamic(Character)}
     * @param dirX composante horizontale de la direction
     * @param dirY composante verticale de la direction
     * @param deltaTime temps écoulé, en secondes
     * @return {@code true} si la position a changé
     * @throws IllegalArgumentException si le personnage n'a pas été enregistré
     */
    @Override
    public boolean move(Character character, float dirX, float dirY, float deltaTime) {
        syncChangingObstacles();
        Body body = bodies.get(character);
        if (body == null) {
            throw new IllegalArgumentException("Personnage non enregistré : appeler addDynamic d'abord.");
        }
        if (deltaTime <= 0f) {
            return false;
        }

        float startX = character.getX();
        float startY = character.getY();

        // Le domaine est la source de vérité : on recale le corps (téléport, dash, respawn...).
        body.setTransform(centerX(character), centerY(character), 0f);
        body.setLinearVelocity(dirX * character.getSpeed(), dirY * character.getSpeed());
        world.step(Math.min(deltaTime, MAX_STEP), VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        Vector2 center = body.getPosition();
        character.setPosition(center.x - character.getWidth() / 2f, center.y - character.getHeight() / 2f);
        body.setLinearVelocity(0f, 0f);

        return Math.abs(character.getX() - startX) > MOVE_EPSILON
            || Math.abs(character.getY() - startY) > MOVE_EPSILON;
    }

    @Override
    public void dispose() {
        bodies.clear();
        changingBodies.clear();
        world.dispose();
    }

    private static float centerX(Character c) { return c.getX() + c.getWidth() / 2f; }
    private static float centerY(Character c) { return c.getY() + c.getHeight() / 2f; }

    /**
     * Rectangle aux coins coupés : évite de s'accrocher aux jointures entre deux murs
     * voisins (les "ghost collisions" de Box2D).
     */
    private static float[] chamferedBox(float hw, float hh) {
        float c = Math.min(hw, hh) * 0.25f;
        return new float[] {
            -hw + c, -hh,   hw - c, -hh,
             hw, -hh + c,   hw,  hh - c,
             hw - c, hh,   -hw + c, hh,
            -hw,  hh - c,  -hw, -hh + c
        };
    }
}

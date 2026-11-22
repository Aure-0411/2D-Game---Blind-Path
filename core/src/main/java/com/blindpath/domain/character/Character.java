package com.blindpath.domain.character;

import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Collidable;
import com.blindpath.domain.inventory.CompassEffect;
import com.blindpath.domain.inventory.Inventory;

/**
 * Personnage du jeu : position, vitesse, hitbox, vie et effets temporaires.
 * <p>
 * La vie est comptée en points de vie entiers : un cœur vaut 2 PV. Après un coup,
 * le personnage est immunisé pendant {@link #IMMUNITY_DURATION} secondes. Il gère aussi
 * l'étourdissement (cri du Screamer), l'effet de la boussole et le boost de la lanterne.
 */
public abstract class Character implements Movable, Collidable{
    /** Dégâts infligés par le contact d'un monstre (1 cœur = 2 PV). */
    public static final int MONSTER_DAMAGE = 2;
    /** Durée de l'immunité après un coup, en secondes. */
    public static final float IMMUNITY_DURATION = 2f;
    private final Inventory<Character> inventory;
    private final CompassEffect compassEffect = new CompassEffect();
    private int boostedSnapsLeft = 0;

    private int life;
    private int maxLife;
    private float speed;
    private float x, y;
    private float width, height;
    private Direction facing = Direction.DOWN;
    private boolean invincible;
    private float immunityTimer = 0f;
    private float stunTimer = 0f;

    /**
     * Crée un personnage avec sa vie au maximum (6 PV, soit 3 cœurs) et un inventaire vide.
     *
     * @param speed vitesse de déplacement, en cases par seconde
     * @param x position horizontale de départ
     * @param y position verticale de départ
     * @param width largeur de la hitbox
     * @param height hauteur de la hitbox
     */
    public Character (float speed, float x, float y, float width, float height){
        this.maxLife = 6;
        this.life = maxLife;
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.inventory = new Inventory<>(this);
    }

    /** @return la vie maximale, en PV */
    public int getMaxLife() { return maxLife; }
    /** @return la vie actuelle, en PV */
    public int getLife() { return life; }
    /**
     * Change directement la vie, sans immunité ni contrôle.
     *
     * @param life la nouvelle vie, en PV
     */
    public void setLife(int life) {this.life = life; }
    /** @return la vitesse de déplacement, en cases par seconde */
    public float getSpeed() { return speed; }
    /** @param speed la nouvelle vitesse de déplacement, en cases par seconde */
    public void setSpeed(float speed) {this.speed = speed; }
    /** @return la position horizontale */
    public float getX() { return x; }
    /** @return la position verticale */
    public float getY() { return y; }
    /** @return la largeur de la hitbox */
    public float getWidth() { return width; }
    /** @return la hauteur de la hitbox */
    public float getHeight() { return height; }
    /** @return l'inventaire du personnage */
    public Inventory<Character> getInventory() { return inventory; }
    /**
     * Téléporte le personnage, sans tenir compte des collisions.
     *
     * @param x nouvelle position horizontale
     * @param y nouvelle position verticale
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    /** @return la direction vers laquelle le personnage regarde */
    public Direction getFacing() { return facing; }
    /**
     * Change la direction du regard. {@link Direction#NONE} est ignorée,
     * pour que le personnage garde sa dernière orientation à l'arrêt.
     *
     * @param facing la nouvelle direction
     */
    public void setFacing(Direction facing) {
        if (facing != Direction.NONE) {
            this.facing = facing;
        }
    }

    /** Déplacement "brut", sans collision. Pour avancer en respectant les murs : Level.tryMove. */
    @Override
    public void move(float directionX, float directionY, float deltaTime) {
        x += directionX * speed * deltaTime;
        y += directionY * speed * deltaTime;
    }

    // ---- Collidable ----
    @Override
    public boolean canCollide() { return true; }

    @Override
    public boolean collide(Collidable other) {
        return canCollide() && other.canCollide() && getBounds().intersects(other.getBounds());
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(x, y, width, height);
    }

    // ---- Gestion de vie ----
    /** Applique les dégâts d'un contact avec un monstre ({@link #MONSTER_DAMAGE}). */
    public void takeMonsterHit() {
        takeDamage(MONSTER_DAMAGE);
    }

    /**
     * Retire de la vie, sauf si le personnage est invincible, immunisé ou déjà mort.
     * Chaque coup reçu déclenche {@link #IMMUNITY_DURATION} secondes d'immunité.
     *
     * @param amount les dégâts, en PV
     * @throws IllegalArgumentException si les dégâts sont négatifs
     */
    public void takeDamage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Les dégâts doivent être positifs ou nuls.");
        if (amount == 0 || isDead()) return;
        if (invincible || isImmune()) {
            return;
        }
        setLife(Math.max(0, getLife() - amount));
        immunityTimer = IMMUNITY_DURATION;
    }

    /** @return {@code true} pendant l'immunité qui suit un coup */
    public boolean isImmune() {
        return immunityTimer > 0;
    }

    /**
     * Fait avancer le compte à rebours de l'immunité.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    public void updateImmunity(float deltaTime) {
        immunityTimer = Math.max(0f, immunityTimer - deltaTime);
    }

    /**
     * Étourdit le personnage : il ne peut plus bouger pendant la durée donnée.
     * Si un étourdissement plus long est déjà en cours, il est conservé.
     * Sans effet si le personnage est invincible.
     *
     * @param duration durée de l'étourdissement, en secondes
     * @throws IllegalArgumentException si la durée est négative
     */
    public void stun(float duration) {

        if (duration < 0) {
            throw new IllegalArgumentException("duration must not be negative");
        }

        if (this.isInvincible()) {
            return;
        }
        stunTimer = Math.max(stunTimer, duration);
    }

    /** @return {@code true} tant que le personnage est étourdi */
    public boolean isStunned() {
        return stunTimer > 0;
    }

    /**
     * Fait avancer le compte à rebours de l'étourdissement.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    public void updateStun(float deltaTime) {
        stunTimer = Math.max(0f, stunTimer - deltaTime);
    }

    /** Active ou désactive l'invincibilité (mode debug, touche I). */
    public void toggleInvincible() {
        invincible = !invincible;
    }

    /** @return {@code true} si l'invincibilité de debug est active */
    public boolean isInvincible() {
        return invincible;
    }

    /** @return {@code true} si la vie est tombée à 0 */
    public boolean isDead() {
        return getLife() == 0;
    }

    // Effet du compas
    /** @return l'effet de boussole du personnage */
    public CompassEffect getCompassEffect() {
        return compassEffect;
    }

    /**
     * Active la boussole, qui indique la sortie pendant la durée donnée.
     *
     * @param durationSeconds durée de l'effet, en secondes
     */
    public void activateCompass(float durationSeconds) {
        compassEffect.activate(durationSeconds);
    }

    /** @return {@code true} tant que la boussole indique la sortie */
    public boolean isCompassActive() {
        return compassEffect.isActive();
    }

    /**
     * Fait avancer le compte à rebours de la boussole.
     *
     * @param deltaTime temps écoulé depuis la dernière image, en secondes
     */
    public void updateCompass(float deltaTime) {
        compassEffect.update(deltaTime);
    }

    // Retire effet temporaire après mort
    /**
     * Remet le personnage en état après une mort : vie au maximum, effets temporaires
     * retirés. L'inventaire est conservé.
     */
    public void resetForRetry() {
        life = maxLife;
        immunityTimer = 0f;
        stunTimer = 0f;
        compassEffect.reset();
        boostedSnapsLeft = 0;
        // inventory est conservé
    }

    /**
     * Renforce la lanterne pour un certain nombre de claquements (effet du Booster).
     *
     * @param snaps nombre de claquements renforcés
     */
    public void boostLantern(int snaps) {
        boostedSnapsLeft = snaps;
    }

    /** @return {@code true} s'il reste des claquements renforcés */
    public boolean isLanternBoosted() {
        return boostedSnapsLeft > 0;
    }

    /**
     * Consomme un claquement renforcé, s'il en reste.
     *
     * @return {@code true} si un claquement renforcé a été consommé
     */
    public boolean consumeBoostedSnap() {
        if (boostedSnapsLeft > 0) {
            boostedSnapsLeft--;
            return true;
        }
        return false;
    }
}
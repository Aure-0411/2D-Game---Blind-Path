package com.blindpath.domain.chest;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.labyrinth.Border;
import com.blindpath.domain.labyrinth.Bounds;
import com.blindpath.domain.labyrinth.Collidable;
import com.blindpath.domain.light.SnapEvent;
import com.blindpath.domain.light.SnapListener;

/**
 * Coffre posé contre un mur du labyrinthe. Il s'ouvre quand le joueur claque des doigts
 * assez près, et donne son contenu à l'inventaire du joueur.
 * <p>
 * Un coffre fait 1 x 2 cases : il est allongé le long du mur auquel il est adossé.
 * La zone d'ouverture est une ellipse qui dépasse le coffre de
 * {@link #DEFAULT_ACTIVATION_MARGIN} case de chaque côté.
 */
public class Chest implements SnapListener, Collidable {

    /** Petit côté du coffre, en cases. */
    public static final float SHORT_SIDE = 1f;
    /** Grand côté du coffre, en cases. */
    public static final float LONG_SIDE = 2f;
    /** Marge autour du coffre dans laquelle un claquement l'ouvre, en cases. */
    public static final float DEFAULT_ACTIVATION_MARGIN = 1f;

    private final float x, y;
    private final float width, height;
    private final Border orientation;
    private final float activationRadiusX, activationRadiusY;
    private final LootResult loot;
    private boolean opened;
    private Character opener;

    /**
     * Crée un coffre fermé.
     *
     * @param x position horizontale du coin bas gauche
     * @param y position verticale du coin bas gauche
     * @param orientation le mur contre lequel le coffre est posé
     * @param loot le contenu du coffre
     * @throws IllegalArgumentException si l'orientation ou le contenu est {@code null}
     */
    public Chest(float x, float y, Border orientation, LootResult loot) {
        if (orientation == null) {
            throw new IllegalArgumentException("L'orientation du coffre doit être renseignée.");
        }
        if (loot == null) {
            throw new IllegalArgumentException("Le loot du coffre doit être renseigné.");
        }
        this.x = x;
        this.y = y;
        this.orientation = orientation;

        boolean horizontal = (orientation == Border.TOP || orientation == Border.BOTTOM);
        this.width = horizontal ? LONG_SIDE : SHORT_SIDE;
        this.height = horizontal ? SHORT_SIDE : LONG_SIDE;

        this.activationRadiusX = (this.width / 2f) + DEFAULT_ACTIVATION_MARGIN;
        this.activationRadiusY = (this.height / 2f) + DEFAULT_ACTIVATION_MARGIN;
        this.loot = loot;
    }

    /** @return la position horizontale du coin bas gauche */
    public float getX() { return x; }
    /** @return la position verticale du coin bas gauche */
    public float getY() { return y; }
    /** @return la largeur, en cases */
    public float getWidth() { return width; }
    /** @return la hauteur, en cases */
    public float getHeight() { return height; }
    /** @return le mur contre lequel le coffre est posé */
    public Border getOrientation() { return orientation; }
    /** @return le demi-axe horizontal de la zone d'ouverture */
    public float getActivationRadiusX() { return activationRadiusX; }
    /** @return le demi-axe vertical de la zone d'ouverture */
    public float getActivationRadiusY() { return activationRadiusY; }

    /**
     * Indique le personnage qui recevra le contenu quand un claquement ouvrira le coffre.
     *
     * @param character le personnage qui ouvre le coffre
     */
    public void setOpener(Character character) {
        this.opener = character;
    }

    /**
     * Ouvre le coffre et ajoute son contenu à l'inventaire du personnage.
     *
     * @param character le personnage qui ouvre le coffre
     * @return {@code true} si le coffre vient d'être ouvert, {@code false} s'il l'était déjà
     *         ou si le personnage est {@code null}
     */
    public boolean open(Character character) {
        if (opened || character == null) {
            return false;
        }
        character.getInventory().add(loot.item(), loot.quantity());
        opened = true;
        return true;
    }

    /** @return {@code true} si le coffre a déjà été ouvert */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Teste si un point est dans la zone d'ouverture (ellipse centrée sur le coffre).
     *
     * @param px position horizontale du point
     * @param py position verticale du point
     * @return {@code true} si le point est dans la zone
     */
    public boolean isInRange(float px, float py) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float normX = (px - centerX) / activationRadiusX;
        float normY = (py - centerY) / activationRadiusY;
        return normX * normX + normY * normY <= 1f;
    }

    /**
     * Ouvre le coffre si le claquement a lieu dans sa zone d'ouverture.
     *
     * @param event le claquement
     */
    @Override
    public void onSnap(SnapEvent event) {
        if (event == null || opened || opener == null) {
            return;
        }
        if (isInRange(event.getSourceX(), event.getSourceY())) {
            open(opener);
        }
    }

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
}
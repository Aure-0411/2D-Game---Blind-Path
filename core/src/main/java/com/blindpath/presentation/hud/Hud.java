package com.blindpath.presentation.hud;


import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.blindpath.domain.inventory.BearTrap;
import com.blindpath.domain.inventory.Booster;
import com.blindpath.domain.inventory.Compass;
import com.blindpath.domain.inventory.Dynamite;
import com.blindpath.domain.inventory.Heart;
import com.blindpath.domain.inventory.Item;
import com.blindpath.domain.inventory.ItemStack;

/**
 * Interface affichée par-dessus le jeu : les cœurs, la barre de recharge de la lanterne,
 * les objets de l'inventaire avec leur quantité, le numéro du niveau, et l'icône
 * « commandes inversées » (qui clignote quand l'effet de l'Inverter se termine).
 */
public class Hud {

    private final Stage stage;
    private final ProgressBar cooldownBar;
    private final Texture whiteTexture;
    private final Texture heartFullTexture;
    private final Texture heartHalfTexture;
    private final Texture heartEmptyTexture;
    private final TextureRegionDrawable heartFullDrawable;
    private final TextureRegionDrawable heartHalfDrawable;
    private final TextureRegionDrawable heartEmptyDrawable;
    private final Image[] itemImages;
    private final Label[] itemQuantities;
    private final BitmapFont itemFont;
    private final Label levelLabel;
    private int displayedLevel = 1;
    private final Texture dynamiteTexture;
    private final Texture bearTrapTexture;
    private final Texture boosterTexture;
    private final Texture compassTexture;
    private final TextureRegionDrawable dynamiteDrawable;
    private final TextureRegionDrawable bearTrapDrawable;
    private final TextureRegionDrawable boosterDrawable;
    private final TextureRegionDrawable compassDrawable;

    private final Image[] hearts;
    private static final float BLINK_PER_SECOND = 4f;
    private final Texture invertedControlsTexture;
    private final Image invertedControlsIcon;
    private boolean controlsInverted;
    private boolean invertEnding;
    private float blinkTime;

    /** Crée l'interface et charge ses images. */
    public Hud() {
        this.stage = new Stage(new ScreenViewport());

        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE);
        pixel.fill();

        this.whiteTexture = new Texture(pixel);
        pixel.dispose();

        TextureRegionDrawable white = new TextureRegionDrawable(
                new TextureRegion(whiteTexture)
        );

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = white.tint(Color.valueOf("#5b5b5b"));
        style.background.setMinWidth(30);

        style.knobBefore = white.tint(Color.valueOf("#e68208"));
        style.knobBefore.setMinWidth(20);

        this.cooldownBar = new ProgressBar(0f, 1f, 0.01f, true, style);
        cooldownBar.setValue(1f);

        Table root = new Table();
        root.setFillParent(true);
        root.top().left();
        root.pad(30);
        root.padTop(250);

        // Le fond de ce conteneur reste visible autour de la jauge : bordure de 3 px.
        Table barFrame = new Table();
        barFrame.setBackground(white.tint(Color.valueOf("#F2F2F2")));
        barFrame.pad(3);
        barFrame.add(cooldownBar).width(20).height(120);

        root.add(barFrame);
        stage.addActor(root);
        root.row();

        heartFullTexture = new Texture("ui/hearts/heart_full.png");
        heartHalfTexture = new Texture("ui/hearts/heart_half.png");
        heartEmptyTexture = new Texture("ui/hearts/heart_empty.png");
        heartFullDrawable = new TextureRegionDrawable(new TextureRegion(heartFullTexture));
        heartHalfDrawable = new TextureRegionDrawable(new TextureRegion(heartHalfTexture));
        heartEmptyDrawable = new TextureRegionDrawable(new TextureRegion(heartEmptyTexture));
        hearts = new Image[3];

        for (int i = 0; i < hearts.length; i++) {
            hearts[i] = new Image(heartFullTexture);
            root.add(hearts[i])
                    .size(48)
                    .padTop(i == 0 ? 16 : 8);
            root.row();
        }

        invertedControlsTexture = new Texture("ui/inverter/inverted_controls.png");
        invertedControlsIcon = new Image(invertedControlsTexture);
        invertedControlsIcon.setVisible(false);
        root.add(invertedControlsIcon).size(48).padTop(16);
        root.row();

        // Items//
        // Les images sont chargées une seule fois, puis réutilisées à chaque mise à jour.
        dynamiteTexture = new Texture("items/inventaire/dynamite__icone.png");
        bearTrapTexture = new Texture("items/inventaire/piege_a_ours_icone.png");
        boosterTexture = new Texture("items/inventaire/recharge_magique_icone.png");
        compassTexture = new Texture("items/inventaire/boussole_icone.png");
        dynamiteDrawable = new TextureRegionDrawable(new TextureRegion(dynamiteTexture));
        bearTrapDrawable = new TextureRegionDrawable(new TextureRegion(bearTrapTexture));
        boosterDrawable = new TextureRegionDrawable(new TextureRegion(boosterTexture));
        compassDrawable = new TextureRegionDrawable(new TextureRegion(compassTexture));

        Table inventoryTable = new Table();
        inventoryTable.setFillParent(true);
        inventoryTable.right().padRight(30);
        itemImages = new Image[5];
        itemQuantities = new Label[5];
        itemFont = new BitmapFont();
        itemFont.getData().setScale(1.5f);

        Label.LabelStyle quantityStyle = new Label.LabelStyle(itemFont, Color.WHITE);
        for (int i = 0; i < 5; i++) {
            Table slotTable = new Table();
            itemImages[i] = new Image();
            itemQuantities[i] = new Label("", quantityStyle);
            slotTable.setBackground(white.tint(Color.valueOf("#000000")));
            inventoryTable.add(slotTable).size(64).padBottom(10);
            inventoryTable.row();
            slotTable.add(itemImages[i]).size(44);
            slotTable.row();
            slotTable.add(itemQuantities[i]).right();
        }
        stage.addActor(inventoryTable);

        levelLabel = new Label("Niveau 1", quantityStyle);
        Table levelTable = new Table();
        levelTable.setFillParent(true);
        levelTable.top().right().pad(30);
        levelTable.add(levelLabel);
        stage.addActor(levelTable);

    }

    /**
     * Met à jour l'affichage avec l'état du jeu.
     *
     * @param cooldownProgress l'avancement de la recharge de la lanterne, de 0 à 1
     * @param life la vie du héros, en PV (2 PV par cœur)
     * @param stacks les piles de l'inventaire
     * @param levelNumber le numéro du niveau en cours
     */
    public void update(float cooldownProgress, int life, List<ItemStack> stacks, int levelNumber) {
        if (displayedLevel != levelNumber) {
            levelLabel.setText("Niveau " + levelNumber);
            displayedLevel = levelNumber;
        }
        cooldownBar.setValue(cooldownProgress);
        for (int i = 0; i < hearts.length; i++) {

            int heartLife = Math.max(0, Math.min(2, life - 2 * (hearts.length - 1 - i)));
            if (heartLife == 2) {
                hearts[i].setDrawable(heartFullDrawable);
            }
            if (heartLife == 1) {
                hearts[i].setDrawable(heartHalfDrawable);
            }
            if (heartLife == 0) {
                hearts[i].setDrawable(heartEmptyDrawable);
            }
        }

        for (int i = 0; i < itemImages.length; i++) {
            if (i < stacks.size()) {
                ItemStack stack = stacks.get(i);
                itemImages[i].setDrawable(getItemDrawable(stack.getItem()));
                itemQuantities[i].setText(Integer.toString(stack.getQuantity()));
            } else {
                // Efface l'ancien contenu quand cet emplacement devient vide.
                itemQuantities[i].setText("");
                itemImages[i].setDrawable(null);
            }
        }
    }

    // L'association entre un objet métier et son image appartient au HUD.
    private TextureRegionDrawable getItemDrawable(Item item) {
        if (item instanceof Heart) {
            return heartFullDrawable;
        }
        if (item instanceof Dynamite) {
            return dynamiteDrawable;
        }
        if (item instanceof BearTrap) {
            return bearTrapDrawable;
        }
        if (item instanceof Booster) {
            return boosterDrawable;
        }
        if (item instanceof Compass) {
            return compassDrawable;
        }
        return null;
    }

    /**
     * @param inverted {@code true} pour afficher l'icône des commandes inversées
     * @param ending {@code true} pour faire clignoter l'icône (l'effet se termine)
     */
    public void setControlsInverted (boolean inverted, boolean ending) {
        controlsInverted = inverted;
        invertEnding = ending;
    }

    /**
     * Dessine l'interface.
     *
     * @param delta temps écoulé depuis la dernière image, en secondes
     */
    public void render(float delta) {
        blinkTime += delta;
        boolean blinkOn = (int) (blinkTime * BLINK_PER_SECOND) % 2 == 0;
        invertedControlsIcon.setVisible(controlsInverted && (!invertEnding || blinkOn));
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    /**
     * Adapte l'interface à la nouvelle taille de la fenêtre.
     *
     * @param width largeur, en pixels
     * @param height hauteur, en pixels
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /** Libère les images et la police de l'interface. */
    public void dispose() {
        stage.dispose();
        whiteTexture.dispose();
        heartFullTexture.dispose();
        heartHalfTexture.dispose();
        heartEmptyTexture.dispose();
        itemFont.dispose();
        dynamiteTexture.dispose();
        bearTrapTexture.dispose();
        boosterTexture.dispose();
        compassTexture.dispose();
        invertedControlsTexture.dispose();
    }

}

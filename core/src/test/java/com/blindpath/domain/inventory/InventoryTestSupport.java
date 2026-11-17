package com.blindpath.domain.inventory;

import java.util.List;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.labyrinth.*;
import com.blindpath.domain.physics.AabbCollisionWorld;

// Objets de test en Java pur : aucun contexte graphique ni Box2D natif.
final class InventoryTestSupport {
    static Level level(Gnome hero) {
        Labyrinth map = new Labyrinth(10, 10, new Entrance(0, 2, Border.LEFT),
            new Exit(9, 2, Border.RIGHT), List.of());
        return new Level(hero, map, new AabbCollisionWorld());
    }

    static class TestItem extends Item {
        int uses;
        boolean allowed = true;
        Gnome receivedUser;
        Level receivedLevel;
        TestItem() { this("Test item"); }
        TestItem(String name) { super(name); }
        @Override public boolean canUse(Gnome user, Level level) {
            return super.canUse(user, level) && allowed;
        }
        @Override public void use(Gnome user, Level level) {
            uses++;
            receivedUser = user;
            receivedLevel = level;
        }
    }
    static class TypeTwo extends TestItem {}
    static class TypeThree extends TestItem {}
    static class TypeFour extends TestItem {}
    static class TypeFive extends TestItem {}
    static class TypeSix extends TestItem {}
}

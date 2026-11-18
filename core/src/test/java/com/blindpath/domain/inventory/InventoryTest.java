package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.blindpath.domain.character.Gnome;
import com.blindpath.domain.game.Level;

import java.util.List;
import static com.blindpath.domain.inventory.InventoryTestSupport.*;

class InventoryTest {
    private Inventory fullInventory() {
        Inventory inventory = new Inventory();
        inventory.add(new TestItem(), 1);
        inventory.add(new TypeTwo(), 1);
        inventory.add(new TypeThree(), 1);
        inventory.add(new TypeFour(), 1);
        inventory.add(new TypeFive(), 1);
        return inventory;
    }
    @Test void newInventoryIsEmptyAndListCannotBeModified() {
        Inventory inventory = new Inventory();
        assertTrue(inventory.getStacks().isEmpty());
        assertThrows(UnsupportedOperationException.class,
            () -> inventory.getStacks().add(new ItemStack(new TestItem(), 1)));
    }
    @Test void sameTypeInstancesShareOneStackAndSurplusIsLost() {
        Inventory inventory = new Inventory();
        TestItem original = new TestItem();
        inventory.add(original, 2);
        inventory.add(new TestItem(), 10);
        assertEquals(1, inventory.getStacks().size());
        assertEquals(5, inventory.getStacks().get(0).getQuantity());
        assertSame(original, inventory.getStacks().get(0).getItem());
    }
    @Test void newStackAlsoCapsAtFive() {
        Inventory inventory = new Inventory();
        inventory.add(new TestItem(), 20);
        assertEquals(1, inventory.getStacks().size());
        assertEquals(5, inventory.getStacks().get(0).getQuantity());
    }
    @Test void fullInventoryRejectsNewTypeButStillCompletesExistingType() {
        Inventory inventory = fullInventory();
        inventory.add(new TypeSix(), 2);
        assertEquals(5, inventory.getStacks().size());
        assertFalse(inventory.getStacks().stream().anyMatch(s -> s.getItem() instanceof TypeSix));
        inventory.add(new TypeFive(), 10);
        assertEquals(5, inventory.getStacks().size());
        assertEquals(5, inventory.getStacks().get(4).getQuantity());
    }
    @Test void invalidAdditionsAreRejectedAndZeroDoesNothing() {
        Inventory inventory = new Inventory();
        assertThrows(IllegalArgumentException.class, () -> inventory.add(null, 1));
        assertThrows(IllegalArgumentException.class, () -> inventory.add(new TestItem(), -1));
        inventory.add(new TestItem(), 0);
        assertTrue(inventory.getStacks().isEmpty());
    }
    @Test void useAppliesSelectedEffectThenConsumesAndRemovesEmptyStack() {
        Inventory inventory = new Inventory();
        TestItem first = new TestItem();
        TypeTwo selected = new TypeTwo();
        inventory.add(first, 1);
        inventory.add(selected, 2);
        Gnome hero = new Gnome();
        Level level = level(hero);
        assertTrue(inventory.use(1, hero, level));
        assertEquals(0, first.uses);
        assertEquals(1, selected.uses);
        assertSame(hero, selected.receivedUser);
        assertSame(level, selected.receivedLevel);
        assertEquals(1, inventory.getStacks().get(1).getQuantity());
        assertTrue(inventory.use(1, hero, level));
        assertEquals(2, selected.uses);
        assertEquals(1, inventory.getStacks().size());
        assertSame(first, inventory.getStacks().get(0).getItem());
    }
    @Test void refusedUsePreservesQuantityAndDoesNotApplyEffect() {
        Inventory inventory = new Inventory();
        TestItem item = new TestItem();
        item.allowed = false;
        inventory.add(item, 2);
        Gnome hero = new Gnome();
        assertFalse(inventory.use(0, hero, level(hero)));
        assertEquals(0, item.uses);
        assertEquals(2, inventory.getStacks().get(0).getQuantity());
    }
    @Test void invalidUseContextAndSlotsAreRejected() {
        Inventory inventory = new Inventory();
        inventory.add(new TestItem(), 1);
        Gnome hero = new Gnome();
        Level level = level(hero);
        assertThrows(IllegalArgumentException.class, () -> inventory.use(0, null, level));
        assertThrows(IllegalArgumentException.class, () -> inventory.use(0, hero, null));
        for (int slot : new int[] {-1, 1, 5}) {
            assertThrows(IllegalArgumentException.class, () -> inventory.use(slot, hero, level));
        }
        assertThrows(IllegalArgumentException.class, () -> new Inventory().use(0, hero, level));
        assertEquals(1, inventory.getStacks().get(0).getQuantity());
    }
    @Test void failedEffectDoesNotConsumeItem() {
        Inventory inventory = new Inventory();
        TestItem failing = new TestItem() {
            @Override public void use(Gnome user, Level level) {
                throw new IllegalStateException("Effect failed");
            }
        };
        inventory.add(failing, 1);
        Gnome hero = new Gnome();
        assertThrows(IllegalStateException.class, () -> inventory.use(0, hero, level(hero)));
        assertEquals(1, inventory.getStacks().get(0).getQuantity());
    }
    @Test void automaticHealingUsesOnlyOneHeartPerCallAndRemovesEmptyStack() {
        Inventory inventory = new Inventory();
        TestItem other = new TestItem();
        inventory.add(other, 1);
        inventory.add(new Heart(), 2);
        Gnome hero = new Gnome();
        Level level = level(hero);
        hero.setLife(hero.getMaxLife() - 2);
        inventory.useHealthNeeded(hero, level);
        assertEquals(hero.getMaxLife() - 1, hero.getLife());
        assertEquals(1, inventory.getStacks().get(1).getQuantity());
        inventory.useHealthNeeded(hero, level);
        assertEquals(hero.getMaxLife(), hero.getLife());
        assertEquals(1, inventory.getStacks().size());
        assertSame(other, inventory.getStacks().get(0).getItem());
        assertEquals(0, other.uses);
    }
    @Test void noHealingOrConsumptionWhenFullDeadOrWithoutHeart() {
        Inventory inventory = new Inventory();
        inventory.add(new Heart(), 2);
        Gnome hero = new Gnome();
        Level level = level(hero);
        inventory.useHealthNeeded(hero, level);
        assertEquals(hero.getMaxLife(), hero.getLife());
        hero.setLife(0);
        inventory.useHealthNeeded(hero, level);
        assertEquals(0, hero.getLife());
        assertEquals(2, inventory.getStacks().get(0).getQuantity());
        hero.setLife(1);
        Inventory noHeart = new Inventory();
        noHeart.add(new TestItem(), 1);
        noHeart.useHealthNeeded(hero, level);
        assertEquals(1, hero.getLife());
        assertEquals(1, noHeart.getStacks().get(0).getQuantity());
    }
    @Test void invalidAutomaticHealingContextAndLifeAreRejected() {
        Inventory inventory = new Inventory();
        Gnome hero = new Gnome();
        Level level = level(hero);
        assertThrows(IllegalArgumentException.class, () -> inventory.useHealthNeeded(null, level));
        assertThrows(IllegalArgumentException.class, () -> inventory.useHealthNeeded(hero, null));
        for (int life : new int[] {-1, hero.getMaxLife() + 1}) {
            hero.setLife(life);
            assertThrows(IllegalArgumentException.class, () -> inventory.useHealthNeeded(hero, level));
        }
    }
    @Test void rejectedHeartUseDoesNotConsumeHeart() {
        Inventory inventory = new Inventory();
        inventory.add(new Heart(), 1);
        Gnome hero = new Gnome();
        assertFalse(inventory.use(0, hero, level(hero)));
        assertEquals(1, inventory.getStacks().get(0).getQuantity());
    }
}

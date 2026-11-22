package com.blindpath.domain.inventory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.blindpath.domain.character.Character;
import com.blindpath.domain.game.Level;
import com.blindpath.domain.inventory.InventoryTestSupport.TestItem;
import com.blindpath.domain.inventory.InventoryTestSupport.TypeFive;
import com.blindpath.domain.inventory.InventoryTestSupport.TypeFour;
import com.blindpath.domain.inventory.InventoryTestSupport.TypeSix;
import com.blindpath.domain.inventory.InventoryTestSupport.TypeThree;
import com.blindpath.domain.inventory.InventoryTestSupport.TypeTwo;

import static com.blindpath.domain.inventory.InventoryTestSupport.*;

class InventoryTest {

    private static Character newHero() {
        return new Character(3f, 0f, 0f, 0.8f, 0.8f) {};
    }

    @Test void fiveHudItemTypesKeepTheirAcquisitionOrderAndQuantities() {
        Character hero = newHero();
        Inventory<Character> inventory = hero.getInventory();
        Item[] items = {new Compass(), new Heart(), new Dynamite(), new BearTrap(), new Booster()};
        for (int i = 0; i < items.length; i++) {
            inventory.add(items[i], i + 1);
        }
        assertEquals(5, inventory.getStacks().size());
        for (int i = 0; i < items.length; i++) {
            assertSame(items[i], inventory.getStacks().get(i).getItem());
            assertEquals(i + 1, inventory.getStacks().get(i).getQuantity());
        }
        inventory.add(new Compass(), 1);
        assertEquals(5, inventory.getStacks().size());
        assertEquals(2, inventory.getStacks().get(0).getQuantity());
    }

    @Test void consumingLastHeartShiftsFollowingStacksAndFreesSlot() {
        Character hero = newHero();
        Inventory<Character> inventory = hero.getInventory();
        inventory.add(new Heart(), 1);
        Compass compass = new Compass();
        inventory.add(compass, 3);
        var oldList = inventory.getStacks();
        hero.setLife(hero.getMaxLife() - 1);
        assertTrue(inventory.use(0, hero, level(hero)));
        assertEquals(1, inventory.getStacks().size());
        assertSame(compass, inventory.getStacks().get(0).getItem());
        assertEquals(3, inventory.getStacks().get(0).getQuantity());
        assertEquals(2, oldList.size(), "La liste renvoyée auparavant ne doit pas changer de structure");
        inventory.add(new Heart(), 2);
        assertInstanceOf(Heart.class, inventory.getStacks().get(1).getItem());
    }

    private Inventory<Character> fullInventory() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        inventory.add(new TestItem(), 1);
        inventory.add(new TypeTwo(), 1);
        inventory.add(new TypeThree(), 1);
        inventory.add(new TypeFour(), 1);
        inventory.add(new TypeFive(), 1);
        return inventory;
    }

    @Test void newInventoryIsEmptyAndListCannotBeModified() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        assertTrue(inventory.getStacks().isEmpty());
        assertThrows(UnsupportedOperationException.class,
            () -> inventory.getStacks().add(new ItemStack(new TestItem(), 1)));
    }

    @Test void sameTypeInstancesShareOneStackAndSurplusIsLost() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        TestItem original = new TestItem();
        inventory.add(original, 2);
        inventory.add(new TestItem(), 10);
        assertEquals(1, inventory.getStacks().size());
        assertEquals(5, inventory.getStacks().get(0).getQuantity());
        assertSame(original, inventory.getStacks().get(0).getItem());
    }

    @Test void newStackAlsoCapsAtFive() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        inventory.add(new TestItem(), 20);
        assertEquals(1, inventory.getStacks().size());
        assertEquals(5, inventory.getStacks().get(0).getQuantity());
    }

    @Test void fullInventoryRejectsNewTypeButStillCompletesExistingType() {
        Inventory<Character> inventory = fullInventory();
        inventory.add(new TypeSix(), 2);
        assertEquals(5, inventory.getStacks().size());
        assertFalse(inventory.getStacks().stream().anyMatch(s -> s.getItem() instanceof TypeSix));
        inventory.add(new TypeFive(), 10);
        assertEquals(5, inventory.getStacks().size());
        assertEquals(5, inventory.getStacks().get(4).getQuantity());
    }

        @Test void invalidAdditionsAreRejectedAndZeroDoesNothing() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        assertThrows(IllegalArgumentException.class, () -> inventory.add(null, 1));
        assertThrows(IllegalArgumentException.class, () -> inventory.add(new TestItem(), -1));
        inventory.add(new TestItem(), 0);
        assertTrue(inventory.getStacks().isEmpty());
    }

    @Test void useAppliesSelectedEffectThenConsumesAndRemovesEmptyStack() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        TestItem first = new TestItem();
        TypeTwo selected = new TypeTwo();
        inventory.add(first, 1);
        inventory.add(selected, 2);
        Character hero = newHero();
        Level<?> level = level(hero);
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
        Inventory<Character> inventory = new Inventory<>(newHero());
        TestItem item = new TestItem();
        item.allowed = false;
        inventory.add(item, 2);
        Character hero = newHero();
        assertFalse(inventory.use(0, hero, level(hero)));
        assertEquals(0, item.uses);
        assertEquals(2, inventory.getStacks().get(0).getQuantity());
    }

    @Test void invalidUseContextAndSlotsAreRejected() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        inventory.add(new TestItem(), 1);
        Character hero = newHero();
        Level<?> level = level(hero);
        assertThrows(IllegalArgumentException.class, () -> inventory.use(0, null, level));
        assertThrows(IllegalArgumentException.class, () -> inventory.use(0, hero, null));
        for (int slot : new int[] {-1, 1, 5}) {
            assertThrows(IllegalArgumentException.class, () -> inventory.use(slot, hero, level));
        }
        assertThrows(IllegalArgumentException.class, () -> new Inventory<>(hero).use(0, hero, level));
        assertEquals(1, inventory.getStacks().get(0).getQuantity());
    }

    @Test void failedEffectDoesNotConsumeItem() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        TestItem failing = new TestItem() {
            @Override public void use(Character user, Level<?> level) {
                throw new IllegalStateException("Effect failed");
            }
        };
        inventory.add(failing, 1);
        Character hero = newHero();
        assertThrows(IllegalStateException.class, () -> inventory.use(0, hero, level(hero)));
        assertEquals(1, inventory.getStacks().get(0).getQuantity());
    }

    @Test void automaticHealingUsesOnlyOneHeartPerCallAndRemovesEmptyStack() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        TestItem other = new TestItem();
        inventory.add(other, 1);
        inventory.add(new Heart(), 2);
        Character hero = newHero();
        Level<?> level = level(hero);
        hero.setLife(hero.getMaxLife() - 3);
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
        Inventory<Character> inventory = new Inventory<>(newHero());
        inventory.add(new Heart(), 2);
        Character hero = newHero();
        Level<?> level = level(hero);
        inventory.useHealthNeeded(hero, level);
        assertEquals(hero.getMaxLife(), hero.getLife());
        hero.setLife(0);
        inventory.useHealthNeeded(hero, level);
        assertEquals(0, hero.getLife());
        assertEquals(2, inventory.getStacks().get(0).getQuantity());
        hero.setLife(1);
        Inventory<Character> noHeart = new Inventory<>(hero);
        noHeart.add(new TestItem(), 1);
        noHeart.useHealthNeeded(hero, level);
        assertEquals(1, hero.getLife());
        assertEquals(1, noHeart.getStacks().get(0).getQuantity());
    }

    @Test void invalidAutomaticHealingContextAndLifeAreRejected() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        Character hero = newHero();
        Level<?> level = level(hero);
        assertThrows(IllegalArgumentException.class, () -> inventory.useHealthNeeded(null, level));
        assertThrows(IllegalArgumentException.class, () -> inventory.useHealthNeeded(hero, null));
        for (int life : new int[] {-1, hero.getMaxLife() + 1}) {
            hero.setLife(life);
            assertThrows(IllegalArgumentException.class, () -> inventory.useHealthNeeded(hero, level));
        }
    }

    @Test void rejectedHeartUseDoesNotConsumeHeart() {
        Inventory<Character> inventory = new Inventory<>(newHero());
        inventory.add(new Heart(), 1);
        Character hero = newHero();
        assertFalse(inventory.use(0, hero, level(hero)));
        assertEquals(1, inventory.getStacks().get(0).getQuantity());
    }
}
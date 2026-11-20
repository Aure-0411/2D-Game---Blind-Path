package com.blindpath.domain.inventory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ItemStackTest {
    @Test void constructorPreservesItemAndQuantity() {
        Item item = new InventoryTestSupport.TestItem();
        ItemStack stack = new ItemStack(item, 3);
        assertSame(item, stack.getItem());
        assertEquals(3, stack.getQuantity());
    }
    @Test void invalidInitialStateIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ItemStack(null, 1));
        for (int quantity : new int[] {-1, 0, 6}) {
            assertThrows(IllegalArgumentException.class,
                () -> new ItemStack(new InventoryTestSupport.TestItem(), quantity));
        }
    }
    @Test void addingCapsAtFiveAndDiscardsSurplus() {
        ItemStack stack = new ItemStack(new InventoryTestSupport.TestItem(), 1);
        stack.add(0);
        assertEquals(1, stack.getQuantity());
        stack.add(2);
        assertEquals(3, stack.getQuantity());
        stack.add(Integer.MAX_VALUE);
        assertEquals(5, stack.getQuantity());
        stack.add(1);
        assertEquals(5, stack.getQuantity());
    }
    @Test void negativeAdditionDoesNotChangeStack() {
        ItemStack stack = new ItemStack(new InventoryTestSupport.TestItem(), 3);
        assertThrows(IllegalArgumentException.class, () -> stack.add(-1));
        assertEquals(3, stack.getQuantity());
    }
    @Test void removingConsumesOneAndCannotGoBelowZero() {
        ItemStack stack = new ItemStack(new InventoryTestSupport.TestItem(), 2);
        stack.rmv();
        assertEquals(1, stack.getQuantity());
        stack.rmv();
        assertEquals(0, stack.getQuantity());
        assertThrows(IllegalArgumentException.class, stack::rmv);
        assertEquals(0, stack.getQuantity());
    }
}
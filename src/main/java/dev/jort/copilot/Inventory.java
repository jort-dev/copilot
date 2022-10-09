package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class Inventory {
    @Inject
    private Client client;

    private ItemContainer inventory;

    private void load() {
        inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null) {
            log.warn("Inventory is null.");
        }
    }

    public void update() {
        load();
    }

    public int count(int... itemIds) {
        load();
        int total = 0;

        for (int itemId : itemIds) {
            total += inventory.count(itemId);
        }

        return total;
    }

    public boolean contains(int... itemIds) {
        return count(itemIds) > 0;
    }

    public int getFreeSlots() {
        load();

        int freeSlots = 28;
        for (Item item : inventory.getItems()) {
            if (item.getQuantity() > 0) {
                freeSlots--;
            }
        }
        return freeSlots;
    }

    public boolean isEmpty() {
        return getFreeSlots() == 28;
    }

    public boolean isFull() {
        return getFreeSlots() == 0;
    }

    public boolean hasFreeSlots() {
        return getFreeSlots() > 0;
    }
}

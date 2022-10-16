package dev.jort.copilot.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class Inventory {
    @Inject
    private Client client;

    private ItemContainer inventory;

    private boolean load() {
        inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null) {
            //seems to be null when client is started with empty inventory
            return false;
        }
        return true;
    }

    public void update() {
        load();
    }

    public int count(int... itemIds) {
        if(!load()){
            return 0;
        }
        if(itemIds == null){ //sometimes happens at boot or something
            return 0;
        }
        int total = 0;

        for (int itemId : itemIds) {
            total += inventory.count(itemId);
        }

        return total;
    }

    public boolean containsAny(int... itemIds) {
        return count(itemIds) > 0;
    }

    public boolean containsAll(int... itemIds){
        for (int itemId : itemIds){
            if (!containsAny(itemId)){
                return false;
            }
        }
        return true;
    }

    public boolean containsOnly(int... itemIds) {
        return count(itemIds) == getUsedSlots();
    }

    public int getUsedSlots(){
        if(!load()){
            return 0;
        }

        int usedSlots = 0;
        for (Item item : inventory.getItems()) {
            if (item.getQuantity() > 0) {
                usedSlots++;
            }
        }
        return usedSlots;
    }

    public int getFreeSlots() {
        return 28 - getUsedSlots();
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

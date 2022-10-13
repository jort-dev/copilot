package dev.jort.copilot.helpers;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Bank {

    /*
    Container id: 786433
    Root container id: 786432

    Deposit inventory id: 786474
    Root deposit inventory id: 786447
     */

    @Inject
    private Client client;

    private ItemContainer bank;

    private void load() {
        ItemContainer bank = client.getItemContainer(InventoryID.BANK);
        if (bank != null) {
            this.bank = bank;
        }
    }

    public boolean isOpen() {
        Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        return bankContainer != null && !bankContainer.isHidden();
    }

    public int count(int... itemIds) {
        load();
        int total = 0;

        if (bank == null) return 0;

        for (int itemId : itemIds) {
            total += bank.count(itemId);
        }

        return total;
    }

    public boolean contains(int... itemIds) {
        return count(itemIds) > 0;
    }
}

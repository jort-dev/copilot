package dev.jort.copilot.priority_scripts;

import dev.jort.copilot.dtos.GroundItem;
import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.helpers.GroundItems;
import dev.jort.copilot.other.PriorityScript;
import dev.jort.copilot.other.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.events.ConfigChanged;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Loot extends PriorityScript {
    @Inject
    GroundItems groundItems;

    private String[] items = new String[0];
    private GroundItem[] groundItemArray = new GroundItem[0];


    @Override
    public void onStart() {
        readConfig();
    }

    public boolean needsToRun() {
        boolean shouldRun = true;
        if (!config.lootAlert()) {
            entityOverlay.clearGroundItemsToHighlight();
            shouldRun = false;
        }
        if (config.lootAlertLoot() == null || config.lootAlertLoot().equals("")) {
            shouldRun = false;
        }
        determineGroundItemsToHighlight();
        if (groundItemArray.length == 0) {
            shouldRun = false;
        }
        return shouldRun;
    }

    public void determineGroundItemsToHighlight() {
        List<GroundItem> groundItemList = groundItems.filter(groundItem -> Util.containsAny(groundItem.getName().toLowerCase(), items));
        groundItemArray = new GroundItem[groundItemList.size()];
        for (int i = 0; i < groundItemList.size(); i++) {
            groundItemArray[i] = groundItemList.get(i);
        }
    }

    @Override
    public int onLoop() {
        if (!needsToRun()) {
            entityOverlay.clearGroundItemsToHighlight();
            action = waitAction;
            return Run.DONE;
        }
        action = new IdHolder()
                .setName("Pickup loot");
        entityOverlay.setGroundItemsToHighlight(groundItemArray);
        alert.handleAlert(true);
        return Run.AGAIN;
    }

    public void onConfigChanged(ConfigChanged event) {
        readConfig();
    }

    public void readConfig() {
        String[] lootNames = config.lootAlertLoot().split(",");

        List<String> itemList = new ArrayList<>();
        for (String lootName : lootNames) {
            String toAdd = lootName.trim().toLowerCase();
            itemList.add(toAdd);
        }
        items = itemList.toArray(new String[0]);
    }
}

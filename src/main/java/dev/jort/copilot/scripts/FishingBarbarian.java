package dev.jort.copilot.scripts;

import dev.jort.copilot.other.Action;

public class FishingBarbarian extends Script{

    @Override
    public void loop() {
        if (inventory.isFull()){
            if(inventory.containsOnly(ids.COOKED_FISH_IDS)){
                widgetOverlay.setItemIdsToHighlight(ids.COOKED_FISH_IDS);
                widgetOverlay.setHighlightOneItemOnly(false);
                action = new Action()
                        .setHint("Drop fish")
                        .setItemIds(ids.COOKED_FISH_IDS);
                return;
            }
            action = new Action()
                    .setHint("Click fire")
                    .setObjectIds(ids.BARBARIAN_FIRE);
            return;
        }
    }
}

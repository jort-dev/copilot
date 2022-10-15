package dev.jort.copilot.scripts;

import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.Script;
import net.runelite.api.widgets.Widget;

public class FishingBarbarian extends Script {

    public Widget getMakeWidget() {
        return client.getWidget(270, 14);
    }

    public boolean isMakeWidgetVisible() {
        Widget widget = getMakeWidget();
        return widget != null && !widget.isHidden();
    }

    @Override
    public void loop() {
        handleAction();
        determineIfAlertIsNeeded();
    }


    public void handleAction() {
        if (tracker.isAnimating()) {
            idHolder = waitIdHolder;
            widgetOverlay.clear();
            entityOverlay.clear();
            return;
        }

        //if only cooked fish in inventory, drop
        if (inventory.contains(ids.COOKED_FISH_IDS) && !inventory.contains(ids.RAW_FISH_IDS)) {
            entityOverlay.clear();
            widgetOverlay.setItemIdsToHighlight(ids.COOKED_FISH_IDS);
            widgetOverlay.setHighlightOneItemOnly(false);
            idHolder = new IdHolder()
                    .setName("Drop fish")
                    .setItemIds(ids.COOKED_FISH_IDS);
            return;
        }


        if (inventory.isFull()) {
            //if cooking widget is visible, click it
            if (isMakeWidgetVisible()) {
                entityOverlay.clear();
                Widget widgetToHighlight = getMakeWidget();
                if (widgetToHighlight != null) {
                    idHolder = new IdHolder()
                            .setName("Cook fish")
                            .setWidgetIds(widgetToHighlight.getId());
                    widgetOverlay.setWidgetToHighlight(widgetToHighlight);
                }
                return;
            }

            //else click the fire to summon it
            idHolder = new IdHolder()
                    .setName("Click fire")
                    .setObjectIds(ids.BARBARIAN_FIRE);
            widgetOverlay.clear();
            entityOverlay.setGameObjectIdsToHighlight(ids.BARBARIAN_FIRE);
            return;
        }

        //click fishing spot
        idHolder = new IdHolder()
                .setName("Click fishing spot")
                .setObjectIds(ids.ROD_FISHING_SPOT_IDS)
                .setActions("Lure");

        widgetOverlay.clear();
        entityOverlay.setNpcIdsToHighlight(ids.ROD_FISHING_SPOT_IDS);
        entityOverlay.setOnlyHighlightClosest(true);
    }

    public void determineIfAlertIsNeeded() {
        boolean lastClickedIdMatches = idHolder.matchId(tracker.getLastClickedId());
        boolean lastClickedActionMatches = idHolder.matchAction(tracker.getLastClickedMenuOption());
        boolean lastActionCorrect = lastClickedIdMatches || lastClickedActionMatches;

        boolean walkingToCorrectGoal = tracker.isWalking() && lastActionCorrect;
        boolean isWaiting = idHolder.equals(waitIdHolder);
        boolean isAlertNeeded = !isWaiting && !walkingToCorrectGoal;
        alert.handleAlert(isAlertNeeded);
    }
}


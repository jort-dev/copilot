package dev.jort.copilot.scripts;

import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.Script;
import net.runelite.api.widgets.Widget;

public class FishingBarbarian extends Script {

    @Override
    public void onLoop() {
        handleAction();
        determineIfAlertIsNeeded();
    }


    public void handleAction() {
        if (tracker.isAnimating()) {
            action = waitAction;
            widgetOverlay.clear();
            entityOverlay.clear();
            return;
        }

        //if only cooked fish in inventory, drop
        if (inventory.containsAny(ids.COOKED_FISH_IDS) && !inventory.containsAny(ids.RAW_FISH_IDS)) {
            entityOverlay.clear();
            widgetOverlay.setItemIdsToHighlight(ids.COOKED_FISH_IDS);
            widgetOverlay.setHighlightOneItemOnly(false);
            action = new IdHolder()
                    .setName("Drop fish")
                    .setItemIds(ids.COOKED_FISH_IDS);
            return;
        }


        if (inventory.isFull()) {
            //if cooking widget is visible, click it
            if (widgets.isMakeWidgetVisible()) {
                entityOverlay.clear();
                Widget widgetToHighlight = widgets.getMakeWidget();
                if (widgetToHighlight != null) {
                    action = new IdHolder()
                            .setName("Cook fish")
                            .setWidgetIds(widgetToHighlight.getId());
                    widgetOverlay.setWidgetToHighlight(widgetToHighlight);
                }
                return;
            }

            //else click the fire to summon it
            action = new IdHolder()
                    .setName("Click fire")
                    .setObjectIds(ids.BARBARIAN_FIRE);
            widgetOverlay.clear();
            entityOverlay.setGameObjectIdsToHighlight(ids.BARBARIAN_FIRE);
            return;
        }

        //click fishing spot
        action = new IdHolder()
                .setName("Click fishing spot")
                .setObjectIds(ids.ROD_FISHING_SPOT_IDS)
                .setActions("Lure");

        widgetOverlay.clear();
        entityOverlay.setNpcIdsToHighlight(ids.ROD_FISHING_SPOT_IDS);
        entityOverlay.setOnlyHighlightClosest(true);
    }

    public void determineIfAlertIsNeeded() {
        boolean lastClickedIdMatches = action.matchId(tracker.getLastClickedId());
        boolean lastClickedActionMatches = action.matchAction(tracker.getLastClickedMenuOption());
        boolean lastActionCorrect = lastClickedIdMatches || lastClickedActionMatches;

        boolean walkingToCorrectGoal = tracker.isWalking() && lastActionCorrect;
        boolean isWaiting = action.equals(waitAction);
        boolean isAlertNeeded = !isWaiting && !walkingToCorrectGoal;
        alert.handleAlert(isAlertNeeded);
    }
}


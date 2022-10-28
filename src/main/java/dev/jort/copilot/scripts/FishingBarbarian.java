package dev.jort.copilot.scripts;

import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.other.Script;
import net.runelite.api.widgets.Widget;

public class FishingBarbarian extends Script {

    @Override
    public int onLoop() {
        if (!config.fishingBarbarian()) {
            return Run.DONE;
        }
        handleAction();
        handleAlert();
        return Run.AGAIN;
    }


    public void handleAction() {
        if (tracker.isAnimating()) {
            action = waitAction;
            return;
        }

        //if only cooked fish in inventory, drop
        if (inventory.containsAny(ids.COOKED_FISH_IDS) && !inventory.containsAny(ids.RAW_FISH_IDS)) {
            action = new IdHolder()
                    .setName("Drop fish")
                    .setItemIds(ids.COOKED_FISH_IDS);
            return;
        }


        if (inventory.isFull()) {
            //if cooking widget is visible, click it
            if (widgets.isMakeWidgetVisible()) {
                Widget widgetToHighlight = widgets.getMakeWidget();
                if (widgetToHighlight != null) {
                    action = new IdHolder()
                            .setName("Cook fish - space")
                            .setWidgets(widgetToHighlight);
                }
                return;
            }

            //else click the fire to summon it
            action = new IdHolder()
                    .setName("Click fire")
                    .setGameObjectIds(ids.BARBARIAN_FIRE);
            return;
        }

        //click fishing spot
        action = new IdHolder()
                .setName("Click fishing spot")
                .setGameObjectIds(ids.ROD_FISHING_SPOT_IDS)
                .setActions("Lure");

        entityOverlay.setOnlyHighlightClosest(true);
    }

    public void handleAlert() {
        boolean lastClickedIdMatches = action.matchId(tracker.getLastClickedId());
        boolean lastClickedActionMatches = action.matchAction(tracker.getLastClickedMenuOption());
        boolean lastActionCorrect = lastClickedIdMatches || lastClickedActionMatches;

        boolean walkingToCorrectGoal = tracker.isWalking() && lastActionCorrect;
        boolean isWaiting = action.equals(waitAction);
        boolean isAlertNeeded = !isWaiting && !walkingToCorrectGoal;
        alert.handleAlert(isAlertNeeded);
    }
}


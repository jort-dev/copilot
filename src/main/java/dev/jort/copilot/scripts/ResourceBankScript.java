package dev.jort.copilot.scripts;

import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.other.Script;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

@ToString
@Slf4j
/*
General script for activities where you harvest resources from a GameObject, and bank them nearby.
 */
public abstract class ResourceBankScript extends Script {
    int[] resourceItemIds;
    int[] resourceObjectIds;
    int[] toolIds;
    public IdHolder waitAction = new IdHolder().setName("Wait");

    IdHolder clickResourceAction;
    IdHolder openBankAction;

    /**
     * Required to call before use.
     */
    public void setResources(IdHolder resources, int... toolIds) {
        this.resourceItemIds = resources.getItemIds();
        this.resourceObjectIds = resources.getGameObjectIds();
        this.toolIds = toolIds;
        if (this.toolIds == null || this.toolIds.length == 0) {
            toolIds = null;
        }
        log.info("Initialized " + resources.getName());

        clickResourceAction = new IdHolder()
                .setName("Click resource")
                .setGameObjectIds(resourceObjectIds);

        openBankAction = new IdHolder()
                .setName("Open bank")
                //dont highlight banker npcs, pathing is bad
                .setGameObjectIds(ids.BANK_OBJECT_IDS);
    }


    @Override
    public int onLoop() {
        determineAction();
        determineIfAlertIsNeeded();
        return Run.AGAIN;
    }

    public IdHolder determineAction() {
        if (bank.isOpen()) {
            //deposit resource if we have it
            if (inventory.containsAny(resourceItemIds)) {
                Widget widgetToClick = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
                if (widgetToClick != null) {
                    action = new IdHolder()
                            .setName("Deposit inventory")
                            .setWidgets(widgetToClick);
                }

                if (inventory.containsOnly(resourceItemIds) && resourceItemIds.length == 1) {
                    //if only one type of resource in inventory: we can also press the resource to deposit all
                    action.setItemIds(resourceItemIds);
                }

                if (isUsingTools()) {
                    //if using a tool in the inventory, we have to deposit by clicking the resources
                    action.setWidgets(); //clear the deposit all option
                    action.setItemIds(resourceItemIds);
                }
                return action;
            }

            //close bank when everything is deposited
            //if in resized mode or resource is behind bank interface, you have to close the bank first
            action = clickResourceAction;
            entityOverlay.setOnlyHighlightClosest(true);
            Widget bankBarWidget = client.getWidget(12, 2);
            if (bankBarWidget != null) {
                Widget closeButtonWidget = bankBarWidget.getChild(11);
                if (closeButtonWidget != null) {
                    action.setWidgets(closeButtonWidget);
                }
            }
            return action;
        }

        if (inventory.isFull() && !isWalkingToCorrectGoal()) {
            //open bank
            action = openBankAction;
            entityOverlay.setOnlyHighlightClosest(false);
            return action;
        }

        if (!tracker.isAnimating() && !isWalkingToCorrectGoal()) {
            //click resource
            action = clickResourceAction;
            entityOverlay.setOnlyHighlightClosest(true);
            return action;
        }

        action = waitAction;
        return action;
    }

    public boolean isUsingTools() {
        return this.toolIds != null;
    }

    public boolean isWalkingToCorrectGoal() {
        //create fresh one, because if reusing one in script, it keeps switching to the wait one, which does not have ids
        boolean clickedResource = clickResourceAction.matchId(tracker.getLastClickedId());
        boolean clickedBank = openBankAction.matchId(tracker.getLastClickedId());
        boolean clickedAction = action.matchId(tracker.getLastClickedId());
        return tracker.isWalking() && (clickedResource || clickedAction || clickedBank);
    }

    public boolean isAlertNeeded() {
        boolean isWaiting = action.equals(waitAction);
        boolean isAlertNeeded = !isWaiting && !isWalkingToCorrectGoal() && !tracker.isAnimating();
        return isAlertNeeded;
    }

    public void determineIfAlertIsNeeded() {
        alert.handleAlert(isAlertNeeded());
    }
}

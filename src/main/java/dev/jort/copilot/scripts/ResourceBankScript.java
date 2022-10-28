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

    //'static' idholder to remove entity highlight when tree is clicked, otherwise flashes because it kept switching to the wait action
    IdHolder clickResourceAction;

    /**
     * Required to call before use.
     */
    public void setResources(IdHolder resources) {
        this.resourceItemIds = resources.getItemIds();
        this.resourceObjectIds = resources.getGameObjectIds();

        clickResourceAction = new IdHolder()
                .setName("Click resource")
                .setGameObjectIds(resourceObjectIds);
        log.info("Initialized " + resources.getName());
    }


    @Override
    public int onLoop() {
        determineAction();
        determineIfAlertIsNeeded();
        return Run.AGAIN;
    }

    public IdHolder determineAction() {
        if (bank.isOpen()) {
            //deposit inventory
            if (!inventory.isEmpty()) {
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
                return action;
            }
        }

        if (inventory.isFull()) {
            //open bank
            action = new IdHolder()
                    .setName("Open bank")
                    //dont highlight banker npcs, pathing is bad
                    .setGameObjectIds(ids.BANK_OBJECT_IDS);
            entityOverlay.setOnlyHighlightClosest(false);
            return action;
        }

        if (!tracker.isAnimating() && !isWalkingToCorrectGoal()) {
            //click resource
            action = clickResourceAction.setName("Click resource");
            entityOverlay.setOnlyHighlightClosest(true);

            if (bank.isOpen()) {
                //if in resized mode or resource is behind bank interface, you have to close the bank first
                action.setName("Close bank or click resource");
                Widget bankBarWidget = client.getWidget(12, 2);
                if (bankBarWidget != null) {
                    Widget closeButtonWidget = bankBarWidget.getChild(11);
                    if (closeButtonWidget != null) {
                        action.setWidgets(closeButtonWidget);
                    }
                }
            }
            return action;
        }

        action = waitAction;
        return action;
    }

    public boolean isWalkingToCorrectGoal() {
        return tracker.isWalking() && clickResourceAction.matchId(tracker.getLastClickedId());
    }

    public void determineIfAlertIsNeeded() {
        boolean isWaiting = action.equals(waitAction);
        boolean isAlertNeeded = !isWaiting && !isWalkingToCorrectGoal();
        alert.handleAlert(isAlertNeeded);
    }
}

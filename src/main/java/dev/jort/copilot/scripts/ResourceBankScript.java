/*
General script for activities where you harvest resources from a GameObject, and bank them nearby.
 */
package dev.jort.copilot.scripts;

import dev.jort.copilot.other.Action;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

@ToString
@Slf4j
public class ResourceBankScript extends Script {
    int[] bankObjectIds;
    int[] resourceItemIds;
    int[] resourceObjectIds;

    /**
     * Required: initialize the script.
     * We cant do this in the constructor: @Inject is not fired there yet
     * @param bankObjectIds The ids of the objects we can click to bank.
     * @param resourceItemIds The item ids the resource gives.
     * @param resourceObjectIds The ids of the resource objects to harvest.
     */
    public void initialize(int[] bankObjectIds, int[] resourceItemIds, int[] resourceObjectIds) {
        this.bankObjectIds = bankObjectIds;
        this.resourceItemIds = resourceItemIds;
        this.resourceObjectIds = resourceObjectIds;
        log.info("Initialized " + this);
    }


    @Override
    public void loop() {
         determineAction();
        determineIfAlertIsNeeded();
    }

    public Action determineAction() {
        if (bank.isOpen()) {
            //deposit inventory
            if (!inventory.isEmpty()) {
                entityOverlay.clear();
                Widget widgetToClick = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
                widgetOverlay.setWidgetToHighlight(widgetToClick);
                action = new Action()
                        .setHint("Deposit inventory")
                        .setWidgetIds(widgetToClick.getId());

                if (inventory.containsOnly(resourceItemIds) && resourceItemIds.length == 1) {
                    //if only one type of resource in inventory: we can also press the resource to deposit all
                    widgetOverlay.setItemIdsToHighlight(resourceItemIds);
                    action.setItemIds(resourceItemIds);
                }
                return action;
            }
        }

        if (inventory.isFull()) {
            //open bank
            entityOverlay.setGameObjectIdsToHighlight(bankObjectIds).setOnlyHighlightClosest(false);
            widgetOverlay.clear();
            action = new Action()
                    .setHint("Open bank")
                    //dont highlight banker npcs, pathing is bad
                    .setObjectIds(bankObjectIds);
            return action;
        }

        if (!tracker.isAnimating()) {
            //click resource
            entityOverlay.setGameObjectIdsToHighlight(resourceObjectIds).setOnlyHighlightClosest(true);
            action = new Action()
                    .setHint("Click resource")
                    .setObjectIds(resourceObjectIds);

            if (bank.isOpen()) {
                //if in resized mode or resource is behind bank interface, you have to close the bank first
                action.setHint("Close bank or click resource");
                Widget bankBarWidget = client.getWidget(12, 2);
                if (bankBarWidget != null) {
                    Widget closeButtonWidget = bankBarWidget.getChild(11);
                    widgetOverlay.setWidgetToHighlight(closeButtonWidget);
                    action.setWidgetIds(ids.BANK_CLOSE, closeButtonWidget.getId());
                }
            } else {
                widgetOverlay.clear();
            }
            return action;
        }

        entityOverlay.clear();
        widgetOverlay.clear();
        action = waitAction;
        return action;
    }

    public void determineIfAlertIsNeeded() {
        boolean walkingToCorrectGoal = tracker.isWalking() && action.match(tracker.getLastClickedId());
        boolean isWaiting = action.equals(waitAction);
        boolean isAlertNeeded = !isWaiting && !walkingToCorrectGoal;

        alert.handleAlert(isAlertNeeded);
    }
}

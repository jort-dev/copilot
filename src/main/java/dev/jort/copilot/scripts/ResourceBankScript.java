/*
General script for activities where you harvest resources from a GameObject, and bank them nearby.
 */
package dev.jort.copilot.scripts;

import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.Script;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

@ToString
@Slf4j
public class ResourceBankScript extends Script {
    int[] resourceItemIds;
    int[] resourceObjectIds;

    /**
     * Required to call before use.
     */
    public void setResources(IdHolder resources) {
        this.resourceItemIds = resources.getItemIds();
        this.resourceObjectIds = resources.getObjectIds();
        log.info("Initialized " + resources.getName());
    }


    @Override
    public void loop() {
        determineAction();
        determineIfAlertIsNeeded();
    }

    public IdHolder determineAction() {
        if (bank.isOpen()) {
            //deposit inventory
            if (!inventory.isEmpty()) {
                entityOverlay.clear();
                Widget widgetToClick = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
                if (widgetToClick != null) {
                    widgetOverlay.setWidgetToHighlight(widgetToClick);
                    idHolder = new IdHolder()
                            .setName("Deposit inventory")
                            .setWidgetIds(widgetToClick.getId());
                }

                if (inventory.containsOnly(resourceItemIds) && resourceItemIds.length == 1) {
                    //if only one type of resource in inventory: we can also press the resource to deposit all
                    widgetOverlay.setItemIdsToHighlight(resourceItemIds);
                    idHolder.setItemIds(resourceItemIds);
                }
                return idHolder;
            }
        }

        if (inventory.isFull()) {
            //open bank
            entityOverlay.setGameObjectIdsToHighlight(ids.BANK_OBJECT_IDS).setOnlyHighlightClosest(false);
            widgetOverlay.clear();
            idHolder = new IdHolder()
                    .setName("Open bank")
                    //dont highlight banker npcs, pathing is bad
                    .setObjectIds(ids.BANK_OBJECT_IDS);
            return idHolder;
        }

        if (!tracker.isAnimating()) {
            //click resource
            entityOverlay.setGameObjectIdsToHighlight(resourceObjectIds).setOnlyHighlightClosest(true);
            idHolder = new IdHolder()
                    .setName("Click resource")
                    .setObjectIds(resourceObjectIds);

            if (bank.isOpen()) {
                //if in resized mode or resource is behind bank interface, you have to close the bank first
                idHolder.setName("Close bank or click resource");
                Widget bankBarWidget = client.getWidget(12, 2);
                if (bankBarWidget != null) {
                    Widget closeButtonWidget = bankBarWidget.getChild(11);
                    if (closeButtonWidget != null) {
                        idHolder.setWidgetIds(ids.BANK_CLOSE, closeButtonWidget.getId());
                    }
                    widgetOverlay.setWidgetToHighlight(closeButtonWidget);
                }
            } else {
                widgetOverlay.clear();
            }
            return idHolder;
        }

        entityOverlay.clear();
        widgetOverlay.clear();
        idHolder = waitIdHolder;
        return idHolder;
    }

    public void determineIfAlertIsNeeded() {
        boolean walkingToCorrectGoal = tracker.isWalking() && idHolder.matchId(tracker.getLastClickedId());
        boolean isWaiting = idHolder.equals(waitIdHolder);
        boolean isAlertNeeded = !isWaiting && !walkingToCorrectGoal;
        alert.handleAlert(isAlertNeeded);
    }
}

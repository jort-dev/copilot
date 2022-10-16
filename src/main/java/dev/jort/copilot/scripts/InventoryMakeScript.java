package dev.jort.copilot.scripts;

import dev.jort.copilot.helpers.Ids;
import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.Script;
import dev.jort.copilot.other.Util;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;

/*
General script where you get items from the bank, and make them into other items.
Examples: fletching, glass blowing
 */
public class InventoryMakeScript extends Script {

    String resourceName = "Molten glass";
    String productName = "Lantern lens";
    String toolName = "Glassblowing pipe";
    int[] resourceIds;
    int[] productIds;
    int[] toolIds;
    int[] toolAndResourceIds;

    @Override
    public void onStart() {
        resourceIds = Ids.determineIds(ItemID.class, resourceName.replace(" ", "_").toUpperCase());
        productIds = Ids.determineIds(ItemID.class, productName.replace(" ", "_").toUpperCase());
        toolIds = Ids.determineIds(ItemID.class, toolName.replace(" ", "_").toUpperCase());
        toolAndResourceIds = Util.concatArrays(toolIds, resourceIds);
    }

    @Override
    public void onLoop() {
        determineAction();
        determineIfAlertIsNeeded();
    }

    public void determineAction(){

        if (bank.isOpen()) {
            if (inventory.containsAny(productIds)) {
                action = new IdHolder()
                        .setName("Deposit product")
                        .setItemIds(productIds);
                entityOverlay.clear();
                widgetOverlay.setUseInventoryContainer(true);
                widgetOverlay.clearHighlightedWidgets();
                widgetOverlay.setItemIdsToHighlight(productIds);
                return;
            }
            if (!inventory.containsAny(toolIds)) {
                action = new IdHolder()
                        .setName("Withdraw tool")
                        .setItemIds(toolIds);
                entityOverlay.clear();
                widgetOverlay.setUseInventoryContainer(false);
                widgetOverlay.clearHighlightedWidgets();
                widgetOverlay.setItemIdsToHighlight(toolIds);
                return;
            }
            if (!inventory.containsAny(resourceIds) || !inventory.isFull()) { // could have withdrawn only 10 for example
                action = new IdHolder()
                        .setName("Withdraw resource")
                        .setItemIds(resourceIds);
                entityOverlay.clear();
                widgetOverlay.setUseInventoryContainer(false);
                widgetOverlay.clearHighlightedWidgets();
                widgetOverlay.setItemIdsToHighlight(resourceIds);
                return;
            }
            //if code reach here: we have resource, tool and no product
            Widget closeBankWidget = widgets.getBankCloseWidget();
            action = new IdHolder()
                    .setName("Close bank")
                    .setWidgetIds(closeBankWidget.getId());
            entityOverlay.clear();
            widgetOverlay.clearHighlightedItems();
            widgetOverlay.setWidgetToHighlight(closeBankWidget);
            return;
        }

        if (!inventory.containsAll(toolAndResourceIds)) {
            action = new IdHolder()
                    .setName("Open bank")
                    .setObjectIds(ids.BANK_OBJECT_IDS);
            entityOverlay.setGameObjectIdsToHighlight(ids.BANK_OBJECT_IDS);
            widgetOverlay.clear();
            return;
        }

        //if code reaches here: we have full invent of resource and the tool
        if(!tracker.hasAnimated(2000)){
            if(widgets.isMakeWidgetVisible()){
                Widget makeWidgetContainer =  client.getWidget(270, 13);
                Widget productWidget = widgets.findWidgetWithName(makeWidgetContainer, productName);
                if (productWidget != null){
                    action = new IdHolder()
                            .setName("Click make widget")
                            .setWidgetIds(productWidget.getId());
                    widgetOverlay.setWidgetToHighlight(productWidget);
                    widgetOverlay.clearHighlightedItems();
                    entityOverlay.clear();
                }
                return;
            }
            action = new IdHolder()
                    .setName("Make product")
                    .setItemIds(toolAndResourceIds);
            widgetOverlay.setUseInventoryContainer(true);
            widgetOverlay.setItemIdsToHighlight(toolIds);
            entityOverlay.clear();
            return;
        }

        action = waitAction;
        entityOverlay.clear();
        widgetOverlay.clear();
    }

    public void determineIfAlertIsNeeded() {
        boolean isWaiting = action.equals(waitAction);
        alert.handleAlert(!isWaiting);
    }
}

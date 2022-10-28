package dev.jort.copilot.scripts;

import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.helpers.Ids;
import dev.jort.copilot.other.Script;
import dev.jort.copilot.other.Util;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;

/*
General script where you get items from the bank, and make them into other items.
Examples: fletching, glass blowing
 */
public class InventoryMakeScript extends Script {

    String resourceName = "N/A";
    String productName = "N/A";
    String toolName = "N/A";
    int[] resourceIds;
    int[] productIds;
    int[] toolIds;
    int[] toolAndResourceIds;

    public void setValues(String resourceName, String productName, String toolName) {
        this.resourceName = resourceName;
        this.productName = productName;
        this.toolName = toolName;
        resourceIds = Ids.determineIds(ItemID.class, resourceName.replace(" ", "_").toUpperCase());
        productIds = Ids.determineIds(ItemID.class, productName.replace(" ", "_").toUpperCase());
        toolIds = Ids.determineIds(ItemID.class, toolName.replace(" ", "_").toUpperCase());
        toolAndResourceIds = Util.concatArrays(toolIds, resourceIds);
    }


    @Override
    public int onLoop() {
        determineAction();
        determineIfAlertIsNeeded();
        return Run.AGAIN;
    }

    public void determineAction() {

        if (bank.isOpen()) {
            //deposit the crafted product
            if (inventory.containsAny(productIds)) {
                action = new IdHolder()
                        .setName("Deposit product")
                        .setItemIds(productIds);
                widgetOverlay.setUseInventoryContainer(true);
                return;
            }
            //withdraw tool if maybe accidentally banked
            if (!inventory.containsAny(toolIds)) {
                action = new IdHolder()
                        .setName("Withdraw tool")
                        .setItemIds(toolIds);
                widgetOverlay.setUseInventoryContainer(false);
                return;
            }
            //keep withdrawing resources until inventory is full
            if (!inventory.containsAny(resourceIds) || !inventory.isFull()) { // could have withdrawn only 10 for example
                action = new IdHolder()
                        .setName("Withdraw resource")
                        .setItemIds(resourceIds);
                widgetOverlay.setUseInventoryContainer(false);
                return;
            }

            //close the bank: : we have resource, tool and no product if code reaches here
            Widget closeBankWidget = widgets.getBankCloseWidget();
            action = new IdHolder()
                    .setName("Close bank - esc")
                    .setWidgets(closeBankWidget);
            return;
        }

        //if we dont have resources or tool, we need to open bank (tool may be accidentally banked)
        if (!inventory.containsAll(toolAndResourceIds)) {
            action = new IdHolder()
                    .setName("Open bank")
                    .setGameObjectIds(ids.BANK_OBJECT_IDS);
            return;
        }

        //if code reaches here: we have full invent of resource and the tool
        if (!tracker.hasAnimated(2000)) {
            //if make menu is visible, click the product widget
            if (widgets.isMakeWidgetVisible()) {
                Widget makeWidgetContainer = client.getWidget(270, 13);
                Widget productWidget = widgets.getWidgetWithText(makeWidgetContainer, productName);
                if (productWidget != null) {
                    action = new IdHolder()
                            .setName("Click make widget - space")
                            .setWidgets(productWidget);
                }
                return;
            }
            //if nothing is selected or the resource is selected, click tool
            if (!tracker.isItemSelected() || tracker.isItemSelected(resourceName)) {
                action = new IdHolder()
                        .setName("Click tool")
                        .setItemIds(toolIds);
                widgetOverlay.setUseInventoryContainer(true);
                return;
            }
            //if tool is selected, click resource
            if (tracker.isItemSelected(toolName)) {
                action = new IdHolder()
                        .setName("Click resource")
                        .setItemIds(resourceIds);
                widgetOverlay.setUseInventoryContainer(true);
                return;
            }
        }

        action = waitAction;
    }

    public void determineIfAlertIsNeeded() {
        boolean isWaiting = action.equals(waitAction);
        alert.handleAlert(!isWaiting);
    }
}

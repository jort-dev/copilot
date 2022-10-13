package dev.jort.copilot.scripts;

import dev.jort.copilot.Action;
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

    private boolean interactionNeeded = false;

    //we cant do this in the constructor: otherwise @Inject is not fired there yet
    public void initialize(int[] bankObjectIds, int[] resourceItemIds, int[] resourceObjectIds) {
        this.bankObjectIds = bankObjectIds;
        this.resourceItemIds = resourceItemIds;
        this.resourceObjectIds = resourceObjectIds;
        log.info("Initialized " + this);
    }


    @Override
    public void loop() {
        determineAction();
        determineOverlay();
    }

    public void determineAction() {
        if (bank.isOpen()) {
            //deposit inventory
            if (!inventory.isEmpty()) {
                overlay3D.clear();
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
                return;
            }
        }

        if (inventory.isFull()) {
            //open bank
            overlay3D.setGameObjectIdsToHighlight(bankObjectIds).setOnlyHighlightClosest(false);
            widgetOverlay.clear();
            action = new Action()
                    .setHint("Open bank")
                    //dont highlight banker npcs, pathing is bad
                    .setObjectIds(bankObjectIds);
            return;
        }

        if (!tracker.isAnimating()) {
            //click resource
            overlay3D.setGameObjectIdsToHighlight(resourceObjectIds).setOnlyHighlightClosest(true);
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
            return;
        }

        overlay3D.clear();
        widgetOverlay.clear();
        action = waitAction;
    }

    public void determineOverlay() {
        boolean walkingToCorrectGoal = tracker.isWalking() && action.match(tracker.getLastClickedId());
        if (action.equals(waitAction) || walkingToCorrectGoal) {
            setInteractionNeeded(false);
        } else {
            setInteractionNeeded(true);
        }
    }

    public void setInteractionNeeded(boolean interactionNeeded) {
        if (this.interactionNeeded) {
            //if first time switching from no-interaction to interaction-needed, sound alert
            sound.missedTick();
        }
        this.interactionNeeded = interactionNeeded;
        notificationOverlay.setEnabled(interactionNeeded);
        if (!interactionNeeded) {
            widgetOverlay.clear();
            overlay3D.clear();
        }
    }
}

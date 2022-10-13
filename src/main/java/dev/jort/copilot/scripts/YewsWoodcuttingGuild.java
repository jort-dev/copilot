package dev.jort.copilot.scripts;

import dev.jort.copilot.Action;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class YewsWoodcuttingGuild extends Script{
    private boolean interactionNeeded = false;

    @Override
    public void loop() {
        determineAction();
        determineOverlay();
    }

    public void determineAction() {
        if (bank.isOpen()) {
            //deposit inventory
            if (!inventory.isEmpty()) {
                overlay3D.clearGameObjectsToHightlight();
                Widget widgetToClick = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
                widgetOverlay.setWidgetToHighlight(widgetToClick);
                action = new Action()
                        .setHint("Deposit inventory")
                        .setWidgetIds(widgetToClick.getId());

                if (inventory.containsOnly(ids.YEW_LOGS)) {
                    //if only yew in inventory: we can also press a yew log to deposit all
                    widgetOverlay.setItemIdsToHighlight(ids.YEW_LOGS);
                    action.setItemIds(ids.YEW_LOGS);
                }
                return;
            }
        }

        if (inventory.isFull()) {
            //open bank
            overlay3D.setGameObjectIdsToHighlight(ids.BANK_CHEST_IDS).setOnlyHighlightClosest(false);
            widgetOverlay.clearAll();
            action = new Action()
                    .setHint("Open bank")
                    //dont highlight banker npcs, pathing is bad
                    .setObjectIds(ids.BANK_CHEST_IDS);
            return;
        }

        if (!tracker.isAnimating()) {
            //click tree
            overlay3D.setGameObjectIdsToHighlight(ids.YEW_IDS).setOnlyHighlightClosest(true);
            action = new Action()
                    .setHint("Click tree")
                    .setObjectIds(ids.YEW_IDS);

            if (bank.isOpen()) {
                //if in resized mode or tree is behind bank interface, you have to close the bank first
                action.setHint("Close bank or click tree");
                Widget bankBarWidget = client.getWidget(12, 2);
                if (bankBarWidget != null) {
                    Widget closeButtonWidget = bankBarWidget.getChild(11);
                    widgetOverlay.setWidgetToHighlight(closeButtonWidget);
                    action.setWidgetIds(ids.BANK_CLOSE, closeButtonWidget.getId());
                }
            } else {
                widgetOverlay.clearAll();
            }
            return;
        }

        overlay3D.clearGameObjectsToHightlight();
        widgetOverlay.clearAll();
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

    public boolean isInteractionNeeded() {
        return interactionNeeded;
    }

    public void setInteractionNeeded(boolean interactionNeeded) {
        if (this.interactionNeeded) {
            //if first time switching from no-interaction to interaction-needed, sound alert
            sound.missedTick();
        }
        this.interactionNeeded = interactionNeeded;
        notificationOverlay.setEnabled(interactionNeeded);
        if (!interactionNeeded) {
            widgetOverlay.clearAll();
            overlay3D.clearGameObjectsToHightlight();
        }
    }
}

package dev.jort.copilot.scripts;

import dev.jort.copilot.Action;
import dev.jort.copilot.CopilotNotificationOverlay;
import dev.jort.copilot.Ids;
import dev.jort.copilot.Sound;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class WillowsDraynor extends Script {

    private boolean interactionNeeded = false;

    @Inject
    CopilotNotificationOverlay notificationOverlay;

    @Inject
    Sound sound;

    @Inject
    Ids ids;

    private Action waitAction = new Action().setHint("Wait");

    private Action action = waitAction;


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

                if (inventory.containsOnly(ids.WILLOW_LOGS)) {
                    //if only willow in inventory: we can also press a willow log to deposit all
                    widgetOverlay.setItemIdsToHighlight(ids.WILLOW_LOGS);
                    action.setItemIds(ids.WILLOW_LOGS);
                }
                return;
            }
        }

        if (inventory.isFull()) {
            //open bank
            overlay3D.setGameObjectIdsToHighlight(ids.BANK_BOOTH).setOnlyHighlightClosest(false);
            widgetOverlay.clearAll();
            action = new Action()
                    .setHint("Open bank")
                    //dont highlight banker npcs, pathing is bad
                    .setObjectIds(ids.BANK_BOOTH);
            return;
        }

        if (!tracker.isAnimating()) {
            //click tree
            overlay3D.setGameObjectIdsToHighlight(ids.WILLOW_IDS).setOnlyHighlightClosest(true);
            action = new Action()
                    .setHint("Click tree")
                    .setObjectIds(ids.WILLOW_IDS);

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
        if (this.interactionNeeded){
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


    public Action getAction() {
        return action;
    }

}

package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class WillowsDraynor extends Script {

    private boolean shouldInteract = false;

    @Inject
    CopilotNotificationOverlay notificationOverlay;

    @Inject
    Ids ids;

    int idToClick = 0; //todo: check if last clicked thing matches and update overlay accordingly


    @Override
    void loop() {

        if (inventory.isFull()) {
            if (bank.isOpen()) {
                setHint("Deposit inventory");
                overlay3D.removeGameObjectToHighlight();
                widgetOverlay.setWidgetToHighlight(client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY));

            } else {
                setHint("Open bank");
                overlay3D.setGameObjectToHighlight(gameObjects.closest(ids.BANK_BOOTH));
                widgetOverlay.removeWidgetToHighlight();
                idToClick = ids.BANK_BOOTH;
            }
        } else {
            if (tracker.isAnimating()) {
                setHint("Wait for tree to fall");
                overlay3D.removeGameObjectToHighlight();
                widgetOverlay.removeWidgetToHighlight();
            } else {
                setHint("Click tree");
                overlay3D.setGameObjectToHighlight(gameObjects.closest(ids.WILLOW_IDS));
                widgetOverlay.removeWidgetToHighlight();
            }
        }

        if ((tracker.isWalking() || tracker.isAnimating())) {
            setShouldInteract(false);
            widgetOverlay.removeWidgetToHighlight();
            overlay3D.removeGameObjectToHighlight();
        } else {
            setShouldInteract(true);
        }

    }

    public boolean isShouldInteract() {
        return shouldInteract;
    }

    public void setShouldInteract(boolean shouldInteract) {
        this.shouldInteract = shouldInteract;
        notificationOverlay.setEnabled(shouldInteract);
    }
}

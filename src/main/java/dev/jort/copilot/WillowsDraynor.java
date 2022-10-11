package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Slf4j
public class WillowsDraynor extends Script {

    private boolean shouldInteract = false;

    @Inject
    CopilotNotificationOverlay notificationOverlay;

    @Inject
    Ids ids;

    List<Integer> idsToClick = new ArrayList<>();


    @Override
    void loop() {

        if (inventory.isFull()) {
            if (bank.isOpen()) {
                setHint("Deposit inventory");
                overlay3D.removeGameObjectToHighlight();
                Widget widgetToClick = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
                widgetOverlay.setWidgetToHighlight(widgetToClick);
                setIdsToClick(widgetToClick.getId(), ids.WILLOW_LOGS);
            } else {
                setHint("Open bank");
                overlay3D.setGameObjectToHighlight(gameObjects.closest(ids.BANK_BOOTH));
                widgetOverlay.removeWidgetToHighlight();
                setIdsToClick(ids.BANKERS_IDS);
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
                setIdsToClick(ids.WILLOW_IDS);
            }
        }

        if ((tracker.isWalking() || tracker.isAnimating())) {
            setShouldInteract(false);
            clearIdsToClick();
            widgetOverlay.removeWidgetToHighlight();
            overlay3D.removeGameObjectToHighlight();
        } else {
            setShouldInteract(true);
        }

    }

    public void clearIdsToClick(){
        idsToClick.clear();
    }

    public void setIdsToClick(int...ids){
        idsToClick.clear();
        for (int id : ids){
            idsToClick.add(id);
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

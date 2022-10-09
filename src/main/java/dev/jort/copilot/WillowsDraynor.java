package dev.jort.copilot;

import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Singleton;

@Singleton
public class WillowsDraynor extends Script {


    public static final int BANK_BOOTH = 10355;
    public static final int WILLOW = 10829;

    @Override
    void loop() {
        if (inventory.isFull()) {
            if (bank.isOpen()) {
                setHint("Deposit inventory");
                overlay3D.removeGameObjectToHighlight();
                widgetOverlay.setWidgetToHighlight(client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY));
            } else {
                setHint("Open bank");
                overlay3D.setGameObjectToHighlight(gameObjects.closest(BANK_BOOTH));
                widgetOverlay.removeWidgetToHighlight();
            }
        } else {
            if (players.isAnimating()) {
                setHint("Wait for tree to fall");
                overlay3D.removeGameObjectToHighlight();
            } else {
                setHint("Click tree");
                overlay3D.setGameObjectToHighlight(gameObjects.closest(WILLOW));
                widgetOverlay.removeWidgetToHighlight();
            }
        }
    }
}

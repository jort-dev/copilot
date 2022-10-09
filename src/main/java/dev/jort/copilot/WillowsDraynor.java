package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.ArrayList;

@Singleton
@Slf4j
public class WillowsDraynor extends Script {


    public static final int BANK_BOOTH = 10355;

    public static int[] WILLOW_IDS;

    public WillowsDraynor() {
        final ArrayList<Integer> ids = new ArrayList<>();
        for (Field field : ObjectID.class.getFields()) {
            if (!field.getName().contains("WILLOW_TREE")) {
                continue;
            }
            if (field.getName().endsWith("STUMP")) {
                continue;
            }

            int value = -1;
            field.setAccessible(true);
            try {
                value = field.getInt(value); //idk why i need to pass a value but it works
            } catch (IllegalAccessException e) {
                log.warn("Can't access field " + field.getName());
            }
            log.info("Adding " + field.getName() + "=" + value + " to trees to cut.");
            ids.add(value);
        }
        WILLOW_IDS = ids.stream().mapToInt(i -> i).toArray();
        log.debug("Parsed tree ids:");
        for(int i : WILLOW_IDS){
            log.info("WILLOW ID " + i);
        }
    }

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
                overlay3D.setGameObjectToHighlight(gameObjects.closest(WILLOW_IDS));
                widgetOverlay.removeWidgetToHighlight();
            }
        }
    }
}

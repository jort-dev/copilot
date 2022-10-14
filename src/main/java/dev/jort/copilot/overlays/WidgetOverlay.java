package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.helpers.Ids;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class WidgetOverlay extends Overlay implements CopilotOverlay {
    @Inject
    Client client;

    @Inject
    Ids ids;

    @Inject
    CopilotConfig config;

    @Inject
    CopilotOverlayUtil overlayUtil;

    private Widget widgetToHighlight;

    private int[] itemIdsToHighlight;

    private boolean highlightOneItemOnly = false;

    private boolean enabled = false;

    public WidgetOverlay() {
        setPosition(OverlayPosition.DYNAMIC); // prevent renders being shifted
        setLayer(OverlayLayer.ALWAYS_ON_TOP); // otherwise drawn widgets are not shown
        setPriority(OverlayPriority.HIGHEST); // probably also needed for same reason
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!enabled) {
            return null;
        }
        renderWidget(graphics);
        renderItem(graphics);
        return null;
    }

    public void renderItem(Graphics2D graphics) {
        if (itemIdsToHighlight == null) {
            return;
        }
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget == null || inventoryWidget.isHidden()) {
            inventoryWidget = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
        }
        if (inventoryWidget == null || inventoryWidget.isHidden()) {
            return;
        }

        if (inventoryWidget.getDynamicChildren() == null) {
            return;
        }

        for (Widget itemWidget : inventoryWidget.getDynamicChildren()) {
            for (int itemId : itemIdsToHighlight) {
                if (itemId == itemWidget.getItemId()) {
                    overlayUtil.highlightShape(graphics, itemWidget.getBounds());
                    if(highlightOneItemOnly){
                        return;
                    }
                }
            }
        }
    }


    private void renderWidget(Graphics2D graphics) {
        if (widgetToHighlight == null) {
            return;
        }
        if (widgetToHighlight.isHidden()) {
            return;
        }
        overlayUtil.highlightShape(graphics, widgetToHighlight.getBounds());
    }

    public void setWidgetToHighlight(Widget widgetToHighlight) {
        this.widgetToHighlight = widgetToHighlight;
    }

    public void clearHighlightedWidgets() {
        widgetToHighlight = null;
    }

    public void setItemIdsToHighlight(int... itemIdsToHighlight) {
        this.itemIdsToHighlight = itemIdsToHighlight;
    }

    public void clearHighlightedItems() {
        itemIdsToHighlight = new int[0];
    }

    public void setHighlightOneItemOnly(boolean oneItemOnly){
        this.highlightOneItemOnly = oneItemOnly;
    }

    @Override
    public void clear() {
        clearHighlightedItems();
        clearHighlightedWidgets();
    }


    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public void setEnabled(boolean enable) {
        if (enable) {
            enable();
            return;
        }
        disable();
    }
}

package dev.jort.copilot.overlays;

import dev.jort.copilot.Ids;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class WidgetOverlay extends Overlay {
    @Inject
    Client client;

    @Inject
    Ids ids;

    private Widget widgetToHighlight;

    private int[] itemIdsToHighlight;

    public WidgetOverlay() {
        setPosition(OverlayPosition.DYNAMIC); // prevent renders being shifted
        setLayer(OverlayLayer.ALWAYS_ON_TOP); // otherwise drawn widgets are not shown
        setPriority(OverlayPriority.HIGHEST); // probably also needed for same reason
    }

    @Override
    public Dimension render(Graphics2D graphics) {
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
                    highlightWidget(graphics, itemWidget);
                }
            }
        }
    }

    private void highlightWidget(Graphics2D graphics, Widget widget) {
        Rectangle bounds = widget.getBounds();
        Color color = Color.CYAN;
        graphics.setColor(color);
        graphics.draw(bounds);
        graphics.setColor(new Color(color.getRed(), color.getBlue(), color.getGreen(), 20));
        graphics.fill(bounds);
    }

    private void renderWidget(Graphics2D graphics) {
        if (widgetToHighlight == null) {
            return;
        }
        if (widgetToHighlight.isHidden()) {
            return;
        }
        highlightWidget(graphics, widgetToHighlight);
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

    public void clearAll() {
        clearHighlightedItems();
        clearHighlightedWidgets();
    }

}

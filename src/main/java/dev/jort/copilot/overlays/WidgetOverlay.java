package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.helpers.Combat;
import dev.jort.copilot.helpers.Ids;
import dev.jort.copilot.helpers.Widgets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
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

    private Widget[] widgetsToHighlight = new Widget[0];

    private int[] itemIdsToHighlight;

    private boolean highlightOneItemOnly = false;

    private boolean enabled = false;

    private boolean highlightInventoryContainer = true; //bank has different inventory container

    public WidgetOverlay() {
        setPosition(OverlayPosition.DYNAMIC); // prevent renders being shifted
        setLayer(OverlayLayer.ABOVE_WIDGETS); // otherwise drawn widgets are not shown
        setPriority(OverlayPriority.MED);
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        if (!enabled) {
            return null;
        }
        renderWidgets(graphics);
        renderItem(graphics);
        return null;
    }

    private boolean containerWidgetValid(Widget widget) {
        return widget != null && !widget.isHidden();
    }

    public Widget getContainerWidget() {
        if (highlightInventoryContainer) {
            Widget inventoryContainerWidget = client.getWidget(WidgetInfo.INVENTORY);
            if (!containerWidgetValid(inventoryContainerWidget)) {
                inventoryContainerWidget = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
            }
            if (!containerWidgetValid(inventoryContainerWidget)) {
                inventoryContainerWidget = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
            }
            if (!containerWidgetValid(inventoryContainerWidget)) {
                inventoryContainerWidget = client.getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER);
            }
            return inventoryContainerWidget; // may be null
        }
        Widget otherContainerWidget = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        return otherContainerWidget;
    }

    public void renderItem(Graphics2D graphics) {
        if (itemIdsToHighlight == null) {
            return;
        }
        Widget containerWidget = getContainerWidget();
        if (containerWidget == null) {
            return;
        }

        if (containerWidget.getDynamicChildren() == null) {
            return;
        }

        for (Widget itemWidget : containerWidget.getDynamicChildren()) {
            for (int itemId : itemIdsToHighlight) {
                if (itemId == itemWidget.getItemId()) {
                    overlayUtil.highlightShape(graphics, itemWidget.getBounds());
                    if (highlightOneItemOnly) {
                        return;
                    }
                }
            }
        }
    }


    private void renderWidgets(Graphics2D graphics) {
        if (widgetsToHighlight == null) {
            return;
        }
        for (Widget widget : widgetsToHighlight) {
            if (widget == null) {
                continue;
            }
            if (widget.isHidden()) {
                continue;
            }
            overlayUtil.highlightShape(graphics, widget.getBounds());
        }
    }

    public void setWidgetsToHighlight(Widget... widgetsToHighlight) {
        this.widgetsToHighlight = widgetsToHighlight;
    }

    public Widget[] getWidgetsToHighlight(){
        return widgetsToHighlight;
    }

    public void clearHighlightedWidgets() {
        widgetsToHighlight = new Widget[0];
    }

    public void setItemIdsToHighlight(int... itemIdsToHighlight) {
        this.itemIdsToHighlight = itemIdsToHighlight;
    }

    public void setUseInventoryContainer(boolean inventoryContainer) {
        this.highlightInventoryContainer = inventoryContainer;
    }

    public void clearHighlightedItems() {
        itemIdsToHighlight = new int[0];
    }

    public void setHighlightOneItemOnly(boolean oneItemOnly) {
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

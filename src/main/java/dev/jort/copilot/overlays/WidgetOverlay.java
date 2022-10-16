package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.helpers.Ids;
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

    private Widget widgetToHighlight;

    private int[] itemIdsToHighlight;

    private boolean highlightOneItemOnly = false;

    private boolean enabled = false;

    private boolean highlightInventoryContainer = true; //bank has different inventory container

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

    private boolean containerWidgetValid(Widget widget){
        return widget != null && !widget.isHidden();
    }

    public Widget getContainerWidget() {
        if (highlightInventoryContainer){
            Widget inventoryContainerWidget = client.getWidget(WidgetInfo.INVENTORY);
            if (!containerWidgetValid(inventoryContainerWidget)) {
                inventoryContainerWidget = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
            }
            if(!containerWidgetValid(inventoryContainerWidget)){
                inventoryContainerWidget = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
            }
            if(!containerWidgetValid(inventoryContainerWidget)){
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
        if (containerWidget == null){
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

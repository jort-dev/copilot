package dev.jort.copilot;

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
public class CopilotWidgetOverlay extends Overlay {
    @Inject
    Client client;

    private Widget widgetToHighlight;

    public CopilotWidgetOverlay(){
        setPosition(OverlayPosition.DYNAMIC); // prevent renders being shifted
        setLayer(OverlayLayer.ALWAYS_ON_TOP); // otherwise drawn widgets are not shown
        setPriority(OverlayPriority.HIGHEST); // probably also needed for same reason
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (widgetToHighlight == null) {
            return null;
        }
        if (widgetToHighlight.isHidden()){
            log.warn("Widget is not visible");
            return null;
        }
        Rectangle bounds = widgetToHighlight.getBounds();
        Color color = Color.CYAN;

        graphics.setColor(color);
        graphics.draw(bounds);
        graphics.setColor(new Color(color.getRed(), color.getBlue(), color.getGreen(), 20));
        graphics.fill(bounds);
        return null;
    }

    public Widget getWidgetToHighlight() {
        return widgetToHighlight;
    }

    public void setWidgetToHighlight(Widget widgetToHighlight) {
        this.widgetToHighlight = widgetToHighlight;
    }

    public void removeWidgetToHighlight(){
        widgetToHighlight = null;
    }
}

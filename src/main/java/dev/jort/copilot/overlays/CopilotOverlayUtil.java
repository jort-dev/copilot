package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.helpers.Widgets;
import dev.jort.copilot.dtos.IdHolder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class CopilotOverlayUtil {
    @Inject
    CopilotConfig config;

    @Inject
    Client client;

    @Inject
    Widgets widgets;


    //OVERLAYS
    @Inject
    public EntityOverlay entityOverlay;
    @Inject
    public NotificationOverlay notificationOverlay;
    @Inject
    public InfoOverlay infoOverlay;
    @Inject
    public WidgetOverlay widgetOverlay;

    public void handleOverlays(IdHolder action) {
        if (action == null) {
            log.info("action null");
            return;
        }

        //this luckily does not create flicker
        widgetOverlay.clear();
        widgetOverlay.setWidgetsToHighlight(action.getWidgets());
        widgetOverlay.setItemIdsToHighlight(action.getItemIds());
        entityOverlay.clear();
        entityOverlay.setNpcIdsToHighlight(action.getNpcIds());
        entityOverlay.setGameObjectIdsToHighlight(action.getGameObjectIds());

        if (config.hideWidgets()) {
            boolean areWidgetsRendered = action.getItemIds().length > 0 || action.getWidgets().length > 0;
            widgets.hideWidgets(!areWidgetsRendered);
        }

    }

    public void highlightShape(Graphics2D graphics, Shape shape) {
        highlightShape(graphics, shape, config.highlightColor());
    }

    public void highlightShape(Graphics2D graphics, Shape shape, Color color) {
        if (shape == null) {
            return;
        }

        //ignore the opacity, only use it to fill the shape
        color = new Color(color.getRed(), color.getBlue(), color.getGreen());

        //darker color when hovering over object
        Point mousePosition = client.getMouseCanvasPosition();
        if (shape.contains(mousePosition.getX(), mousePosition.getY())) {
            color = color.darker();
        }

        //draw the outline
        graphics.setColor(color);
        graphics.draw(shape);

        //fill
        color = new Color(color.getRed(), color.getBlue(), color.getGreen(), config.highlightColor().getAlpha());
        graphics.setColor(color);
        graphics.fill(shape);
    }
}

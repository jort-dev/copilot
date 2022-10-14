package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class CopilotOverlayUtil {
    @Inject
    CopilotConfig config;

    @Inject
    Client client;

    public void highlightShape(Graphics2D graphics, Shape shape){
        if(shape == null){
            return;
        }

        Color color = config.highlightColor();
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

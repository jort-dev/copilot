package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
@Slf4j
public class CopilotOverlay3D extends Overlay {

    private final Client client;

    private GameObject gameObjectToHighlight = null;

    @Inject
    private CopilotOverlay3D(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC); // prevent renders being shifted
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Color color = Color.CYAN;
        if (gameObjectToHighlight == null) {
            return null;
        }
        Shape box = gameObjectToHighlight.getClickbox();
        if (box == null) {
            return null;
        }

        //draw the outline
        Point mousePosition = client.getMouseCanvasPosition();
        if (box.contains(mousePosition.getX(), mousePosition.getY())) {
            //darken outline when mouse is inside
            graphics.setColor(color.darker());
        } else {
            graphics.setColor(color);
        }
        graphics.draw(box);

        //fill the outline
        graphics.setColor(new Color(color.getRed(), color.getBlue(), color.getGreen(), 20));
        graphics.fill(box);

        return null;
    }

    public GameObject getGameObjectToHighlight() {
        return gameObjectToHighlight;
    }

    public void removeGameObjectToHighlight() {
        if (gameObjectToHighlight == null) {
            return;
        }
        gameObjectToHighlight = null;
        log.info("Removed game object to draw");
    }

    public void setGameObjectToHighlight(GameObject gameObjectToHighlight) {
        if (this.gameObjectToHighlight != null && this.gameObjectToHighlight.equals(gameObjectToHighlight)) {
            return;
        }
        this.gameObjectToHighlight = gameObjectToHighlight;
        log.info("Updated game object to draw to : " + gameObjectToHighlight.getId());
    }
}

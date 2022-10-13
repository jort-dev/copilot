package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.helpers.GameObjects;
import dev.jort.copilot.other.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;

@Singleton
@Slf4j
public class GameObjectOverlay extends Overlay implements CopilotOverlay {

    private final Client client;

    @Inject
    GameObjects gameObjects;

    @Inject
    CopilotConfig config;

    private int[] gameObjectIdsToHighlight = new int[0];

    private int[] npcIdsToHighlight = new int[0];


    private boolean onlyHighlightClosest = false;
    private boolean enabled = false;

    @Inject
    private GameObjectOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC); // prevent renders being shifted
    }

    public Client getClient() {
        return client;
    }

    public void highlightGameObject(Graphics2D graphics, GameObject gameObject) {
        if (gameObject == null) {
            return;
        }
        Shape box = gameObject.getClickbox();
        if (box == null) {
            return;
        }

        Color color = config.highlightColor();

        //darker color when hovering over object
        Point mousePosition = client.getMouseCanvasPosition();
        if (box.contains(mousePosition.getX(), mousePosition.getY())) {
            color = color.darker();
        }
        graphics.setColor(color);

        //draw outline
        graphics.draw(box);

        //fill outline
        graphics.setColor(new Color(color.getRed(), color.getBlue(), color.getGreen(), config.highlightOpacity));
        graphics.fill(box);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!enabled) {
            return null;
        }
        if (onlyHighlightClosest) {
            highlightGameObject(graphics, gameObjects.closest(gameObjectIdsToHighlight));
            return null;
        }

        List<GameObject> gameObjectList = gameObjects.filter(gameObject -> Util.arrayContains(gameObject.getId(), gameObjectIdsToHighlight));
        for (GameObject gameObject : gameObjectList) {
            highlightGameObject(graphics, gameObject);
        }
        return null;
    }

    public boolean isOnlyHighlightClosest() {
        return onlyHighlightClosest;
    }

    public void setOnlyHighlightClosest(boolean onlyHighlightClosest) {
        this.onlyHighlightClosest = onlyHighlightClosest;
    }

    public int[] getGameObjectIdsToHighlight() {
        return gameObjectIdsToHighlight;
    }

    @Override
    public void clear() {
        gameObjectIdsToHighlight = new int[0];
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    public GameObjectOverlay setGameObjectIdsToHighlight(int... gameObjectIdsToHighlight) {
        this.gameObjectIdsToHighlight = gameObjectIdsToHighlight;
        return this;
    }

    @Override
    public void setEnabled(boolean enable){
        if (enable){
            enable();
            return;
        }
        disable();
    }
}

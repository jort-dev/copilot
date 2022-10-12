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
import java.util.List;

@Singleton
@Slf4j
public class CopilotOverlay3D extends Overlay {

    private final Client client;

    @Inject
    GameObjects gameObjects;

    private int[] gameObjectIdsToHighlight = new int[0];


    private boolean onlyHighlightClosest = false;

    @Inject
    private CopilotOverlay3D(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC); // prevent renders being shifted
    }

    public Client getClient() {
        return client;
    }

    public void highlightGameObject(Graphics2D graphics, GameObject gameObject){
        if (gameObject == null) {
            return;
        }
        Shape box = gameObject.getClickbox();
        if (box == null) {
            return;
        }

        Color color = Color.CYAN;
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
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (onlyHighlightClosest){
            highlightGameObject(graphics, gameObjects.closest(gameObjectIdsToHighlight));
            return null;
        }

        List<GameObject> gameObjectList = gameObjects.filter(gameObject -> Util.arrayContains(gameObject.getId(), gameObjectIdsToHighlight));
        for (GameObject gameObject : gameObjectList){
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

    public void clearGameObjectsToHightlight(){
        gameObjectIdsToHighlight = new int[0];
    }

    public CopilotOverlay3D setGameObjectIdsToHighlight(int... gameObjectIdsToHighlight) {
        this.gameObjectIdsToHighlight = gameObjectIdsToHighlight;
        return this;
    }


}

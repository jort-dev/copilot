package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.helpers.GameObjects;
import dev.jort.copilot.other.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;

@Singleton
@Slf4j
public class EntityOverlay extends Overlay implements CopilotOverlay {

    private final Client client;

    @Inject
    GameObjects gameObjects;

    @Inject
    CopilotConfig config;

    @Inject
    CopilotOverlayUtil overlayUtil;

    private int[] gameObjectIdsToHighlight = new int[0];


    private int[] npcIdsToHighlight = new int[0];


    private boolean onlyHighlightClosest = false;
    private boolean enabled = false;

    @Inject
    private EntityOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC); // prevent renders being shifted
    }

    public Client getClient() {
        return client;
    }

    public void highlightTileObject(Graphics2D graphics, TileObject tileObject) {
        if (tileObject == null) {
            return;
        }
        overlayUtil.highlightShape(graphics, tileObject.getClickbox());
    }

    public void highlightNpc(Graphics2D graphics, NPC npc) {
        if (npc == null) {
            return;
        }
        Perspective.getClickbox(client, npc.getModel(), npc.getOrientation(), npc.getLocalLocation().getX(), npc.getLocalLocation().getY(), 0);
        Perspective.getClickbox(client, npc.getModel(), npc.getOrientation(), npc.getLocalLocation().getX(), npc.getLocalLocation().getY(), 0);
        overlayUtil.highlightShape(graphics, npc.getCanvasTilePoly());
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!enabled) {
            return null;
        }
        if (onlyHighlightClosest) {
            highlightTileObject(graphics, gameObjects.closest(gameObjectIdsToHighlight));
            return null;

        }

        List<GameObject> gameObjectList = gameObjects.filter(gameObject -> Util.arrayContains(gameObject.getId(), gameObjectIdsToHighlight));
        for (GameObject gameObject : gameObjectList) {
            highlightTileObject(graphics, gameObject);
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
        npcIdsToHighlight = new int[0];
    }

    public void setNpcIdsToHighlight(int... npcIdsToHighlight) {
        this.npcIdsToHighlight = npcIdsToHighlight;
    }


    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    public EntityOverlay setGameObjectIdsToHighlight(int... gameObjectIdsToHighlight) {
        this.gameObjectIdsToHighlight = gameObjectIdsToHighlight;
        return this;
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

package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.dtos.GroundItem;
import dev.jort.copilot.helpers.GameObjects;
import dev.jort.copilot.helpers.GroundItems;
import dev.jort.copilot.helpers.GroundObjects;
import dev.jort.copilot.helpers.Npcs;
import dev.jort.copilot.other.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

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
    GroundObjects groundObjects;

    @Inject
    GroundItems groundItems;

    @Inject
    Npcs npcs;

    @Inject
    CopilotConfig config;

    @Inject
    CopilotOverlayUtil overlayUtil;

    private int[] gameObjectIdsToHighlight = new int[0];

    private int[] secondaryGameObjectIdsToHighlight = new int[0];


    private int[] npcIdsToHighlight = new int[0];

    private GroundItem[] groundItemsToHighlight = new GroundItem[0];


    private boolean onlyHighlightClosest = false;
    private boolean enabled = false;

    @Inject
    private EntityOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC); // prevent renders being shifted
        setLayer(OverlayLayer.UNDER_WIDGETS);
        setPriority(OverlayPriority.MED);
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

    public void highlightTileObject(Graphics2D graphics, TileObject tileObject, Color color) {
        if (tileObject == null) {
            return;
        }
        overlayUtil.highlightShape(graphics, tileObject.getClickbox(), color);
    }

    public void highlightActor(Graphics2D graphics, Actor actor) {
        if (actor == null) {
            return;
        }
//        Perspective.getClickbox(client, actor.getModel(), actor.getOrientation(), actor.getLocalLocation().getX(), actor.getLocalLocation().getY(), 0);
        overlayUtil.highlightShape(graphics, actor.getCanvasTilePoly());
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (!enabled) {
            return null;
        }

        //todo: implement like rest
        for (GroundItem groundItem : groundItemsToHighlight) {
            if (groundItem == null) {
                continue;
            }
            drawTile(graphics, groundItem.getLocation(), config.highlightColor());
        }

        if (config.bogged()) {
            List<GroundObject> pumpIt = groundObjects.filter(groundObject -> groundObject.getId() == 13838);
            for (GroundObject groundObject : pumpIt) {
                highlightTileObject(graphics, groundObject);
            }
        }
        if (onlyHighlightClosest) {
            highlightTileObject(graphics, gameObjects.closest(gameObjectIdsToHighlight));
            highlightTileObject(graphics, gameObjects.closest(secondaryGameObjectIdsToHighlight));
            highlightActor(graphics, npcs.closest(npcIdsToHighlight));

            return null;

        }

        List<GameObject> gameObjectList = gameObjects.filter(gameObject -> Util.arrayContains(gameObject.getId(), gameObjectIdsToHighlight));
        for (GameObject gameObject : gameObjectList) {
            highlightTileObject(graphics, gameObject);
        }

        gameObjectList = gameObjects.filter(gameObject -> Util.arrayContains(gameObject.getId(), secondaryGameObjectIdsToHighlight));
        for (GameObject gameObject : gameObjectList) {
            highlightTileObject(graphics, gameObject, config.alternativeHighlightColor());
        }

        List<NPC> npcList = npcs.filter(npc -> Util.arrayContains(npc.getId(), npcIdsToHighlight));
        for (NPC npc : npcList) {
            highlightActor(graphics, npc);
        }

        return null;
    }

    public void drawTile(Graphics2D graphics, WorldPoint point, Color color) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (point.distanceTo(playerLocation) >= 10) {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null) {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        Color lineColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
        Color fillColor = new Color(0, 0, 0, color.getAlpha());
        Stroke stroke = new BasicStroke(2.0f);
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, lineColor, fillColor, stroke);
        }
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
        secondaryGameObjectIdsToHighlight = new int[0];
    }

    public void clearGroundItemsToHighlight() {
        groundItemsToHighlight = new GroundItem[0];
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

    public void setGameObjectIdsToHighlight(int... gameObjectIdsToHighlight) {
        this.gameObjectIdsToHighlight = gameObjectIdsToHighlight;
    }

    public void setSecondaryGameObjectIdsToHighlight(int... secondaryGameObjectIdsToHighlight) {
        this.secondaryGameObjectIdsToHighlight = secondaryGameObjectIdsToHighlight;
    }


    public GroundItem[] getGroundItemsToHighlight() {
        return groundItemsToHighlight;
    }

    public void setGroundItemsToHighlight(GroundItem... groundItemsToHighlight) {
        this.groundItemsToHighlight = groundItemsToHighlight;
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

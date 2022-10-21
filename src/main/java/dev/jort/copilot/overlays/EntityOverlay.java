package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.helpers.GameObjects;
import dev.jort.copilot.helpers.GroundObjects;
import dev.jort.copilot.helpers.Npcs;
import dev.jort.copilot.other.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

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
    Npcs npcs;

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

    public void highlightActor(Graphics2D graphics, Actor actor) {
        if (actor == null) {
            return;
        }
//        Perspective.getClickbox(client, actor.getModel(), actor.getOrientation(), actor.getLocalLocation().getX(), actor.getLocalLocation().getY(), 0);
        overlayUtil.highlightShape(graphics, actor.getCanvasTilePoly());
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(config.bogged()){
            List<GroundObject> pumpIt = groundObjects.filter(groundObject -> groundObject.getId() == 13838);
            for (GroundObject groundObject : pumpIt){
                highlightTileObject(graphics, groundObject);
            }
        }

        if (!enabled) {
            return null;
        }
        if (onlyHighlightClosest) {
            highlightTileObject(graphics, gameObjects.closest(gameObjectIdsToHighlight));
            highlightActor(graphics, npcs.closest(npcIdsToHighlight));
            return null;

        }

        List<GameObject> gameObjectList = gameObjects.filter(gameObject -> Util.arrayContains(gameObject.getId(), gameObjectIdsToHighlight));
        for (GameObject gameObject : gameObjectList) {
            highlightTileObject(graphics, gameObject);
        }

        List<NPC> npcList = npcs.filter(npc -> Util.arrayContains(npc.getId(), npcIdsToHighlight));
        for (NPC npc : npcList) {
            highlightActor(graphics, npc);
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

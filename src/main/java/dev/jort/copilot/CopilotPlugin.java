package dev.jort.copilot;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
        name = "Copilot"
)
public class CopilotPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private CopilotConfig config;

    @Inject
    private GameObjects gameObjects;
    @Inject
    Inventory inventory;
    @Inject
    Players players;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CopilotOverlay2D overlay2d;

    @Inject
    public CopilotOverlay3D overlay3d;

    @Inject
    CopilotWidgetOverlay widgetOverlay;

    @Inject
    private Chat chat;

    @Inject
    WillowsDraynor willowsDraynor;



    @Override
    protected void startUp() throws Exception {
        log.info("Copilot started!");
        overlayManager.add(overlay2d);
        overlayManager.add(overlay3d);
        overlayManager.add(widgetOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay2d);
        overlayManager.remove(overlay3d);
        log.info("Copilot stopped!");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            chat.send("Started!");
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        gameObjects.add(event.getGameObject());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        gameObjects.remove(event.getGameObject());
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        players.onGameTick(event);
        willowsDraynor.loop();
    }

    @Provides
    CopilotConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CopilotConfig.class);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        players.onMenuOptionClicked(event);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            inventory.update();
        }
    }

}

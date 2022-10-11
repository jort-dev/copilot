package dev.jort.copilot;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.temporal.ChronoUnit;

@Slf4j
@PluginDescriptor(
        name = "Copilot",
        description = "Shows where to click next."
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
    Tracker tracker;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CopilotOverlay2D overlay2d;

    @Inject
    public CopilotOverlay3D overlay3d;

    @Inject
    CopilotWidgetOverlay widgetOverlay;

    @Inject
    CopilotNotificationOverlay notificationOverlay;

    @Inject
    private Chat chat;

    @Inject
    WillowsDraynor willowsDraynor;


    @Schedule(period = 3, unit = ChronoUnit.SECONDS)
    public void schedule() {
        if (!client.getGameState().equals(GameState.LOGGED_IN)) {
            log.info("Waiting to be logged in");
            return;
        }
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Copilot started!");
        overlayManager.add(overlay2d);
        overlayManager.add(overlay3d);
        overlayManager.add(widgetOverlay);
        overlayManager.add(notificationOverlay);
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Util.colorString("Jort's ", "ff00ff"));
            stringBuilder.append(Util.colorString("Copilot ", "0000ff"));
            stringBuilder.append(Util.colorString("has ", "ffff00"));
            stringBuilder.append(Util.colorString("started!", "ff0000"));
            chat.send(stringBuilder.toString());
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
        tracker.onGameTick(event);
        willowsDraynor.loop();
    }

    @Provides
    CopilotConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CopilotConfig.class);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        tracker.onMenuOptionClicked(event);
        chat.send("Clicked menu item: " + event.getMenuOption() + " on " + event.getId());
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            inventory.update();
        }
    }

}

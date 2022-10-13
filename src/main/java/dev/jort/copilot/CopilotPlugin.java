package dev.jort.copilot;

import com.google.inject.Provides;

import javax.inject.Inject;

import dev.jort.copilot.overlays.GameObjectOverlay;
import dev.jort.copilot.overlays.InfoOverlay;
import dev.jort.copilot.overlays.NotificationOverlay;
import dev.jort.copilot.overlays.WidgetOverlay;
import dev.jort.copilot.scripts.FishingBarbarian;
import dev.jort.copilot.scripts.Script;
import dev.jort.copilot.scripts.WillowsDraynor;
import dev.jort.copilot.scripts.YewsWoodcuttingGuild;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
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
    ClientThread clientThread;

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
    private InfoOverlay overlay2d;

    @Inject
    public GameObjectOverlay overlay3d;

    @Inject
    WidgetOverlay widgetOverlay;

    @Inject
    NotificationOverlay notificationOverlay;

    @Inject
    private Chat chat;

    @Inject
    WillowsDraynor willowsDraynor;

    @Inject
    FishingBarbarian fishingBarbarian;

    @Inject
    YewsWoodcuttingGuild yewsWoodcuttingGuild;

    Script runningScript = null;


    @Schedule(period = 1, unit = ChronoUnit.SECONDS)
    public void schedule() {
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
        overlayManager.remove(widgetOverlay);
        overlayManager.remove(notificationOverlay);
        log.info("Copilot stopped!");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Util.colorString("Jort's ", "ff00f"));
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
    public void onChatMessage(ChatMessage event) {
        if (!event.getType().equals(ChatMessageType.PUBLICCHAT)) {
            return;
        }
        log.info("Received message: " + event.getMessage());

        try {
            int id = Integer.parseInt(event.getMessage());
            log.info("Playing sound with ID " + id);
            clientThread.invoke(() -> client.playSoundEffect(id));
        } catch (Exception e) {
            log.info("No number found");
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        tracker.onGameTick(event);
        if (config.willowsDraynor()) {
            willowsDraynor.loop();
            runningScript = willowsDraynor;
        } else if (config.fishingBarbarian()) {
            fishingBarbarian.loop();
            runningScript = fishingBarbarian;
        } else if (config.yewsGuild()) {
            yewsWoodcuttingGuild.loop();
            runningScript = yewsWoodcuttingGuild;
        }

    }

    @Provides
    CopilotConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CopilotConfig.class);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        tracker.onMenuOptionClicked(event);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            inventory.update();
        }
    }

    public Script getRunningScript(){
        return runningScript;
    }

}

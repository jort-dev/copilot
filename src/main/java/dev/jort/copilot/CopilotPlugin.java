package dev.jort.copilot;

import com.google.inject.Provides;
import dev.jort.copilot.overlays.*;
import dev.jort.copilot.scripts.FishingBarbarian;
import dev.jort.copilot.scripts.Script;
import dev.jort.copilot.scripts.WillowsDraynor;
import dev.jort.copilot.scripts.YewsWoodcuttingGuild;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "Copilot",
        description = "Shows where to click next."
)
public class CopilotPlugin extends Plugin {


    // DEFAULT
    @Inject
    private Client client;
    @Inject
    ClientThread clientThread;
    @Inject
    private CopilotConfig config;


    // CUSTOM HELPER OBJECTS
    @Inject
    private GameObjects gameObjects;
    @Inject
    Inventory inventory;
    @Inject
    Tracker tracker;
    @Inject
    private Chat chat;
    @Inject
    Ids ids;


    //OVERLAYS
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
    List<CopilotOverlay> overlays = new ArrayList<>();


    //SCRIPTS
    @Inject
    WillowsDraynor willowsDraynor;
    @Inject
    FishingBarbarian fishingBarbarian;
    @Inject
    YewsWoodcuttingGuild yewsWoodcuttingGuild;

    Script runningScript = null;


    @Schedule(period = 1, unit = ChronoUnit.SECONDS)
    public void schedule() {
        //thread which fires each period
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Copilot started!");

        //initialize overlays
        overlays.add(overlay2d);
        overlays.add(overlay3d);
        overlays.add(widgetOverlay);
        overlays.add(notificationOverlay);
        for (CopilotOverlay overlay : overlays) {
            overlayManager.add((Overlay) overlay);
            overlay.enable();
        }

        //initialize scripts which require arguments
        yewsWoodcuttingGuild.initialize(ids.BANK_CHEST_IDS, new int[]{ids.YEW_LOGS}, ids.YEW_IDS);
    }

    @Override
    protected void shutDown() throws Exception {
        for (CopilotOverlay overlay : overlays) {
            overlayManager.remove((Overlay) overlay);
        }
        log.info("Copilot stopped!");
    }


    //API SUBSCRIPTIONS (only work in this class)

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
    public void onConfigChanged(ConfigChanged configChanged) {
        log.info("Config changed of " + configChanged.getGroup() + ":::" + configChanged);
        if (!configChanged.getGroup().equals("copilot")) {
            return;
        }
        // because we disable it when no script is running, enable it when we may have enabled a script
        setOverlaysEnabled(true);
    }

    public void setOverlaysEnabled(boolean enable) {
        for (CopilotOverlay overlay : overlays) {
            if (enable) {
                overlay.enable();
            } else {
                overlay.disable();
            }
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
        } else {
            setOverlaysEnabled(false);
        }
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


    //OTHER

    @Provides
    CopilotConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CopilotConfig.class);
    }


    public Script getRunningScript() {
        return runningScript;
    }

}

package dev.jort.copilot;

import com.google.inject.Provides;
import dev.jort.copilot.helpers.*;
import dev.jort.copilot.overlays.*;
import dev.jort.copilot.scripts.FishingBarbarian;
import dev.jort.copilot.other.Script;
import dev.jort.copilot.scripts.InventoryMakeScript;
import dev.jort.copilot.scripts.Woodcutting;
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
    @Inject
    Alert alert;
    @Inject
    Npcs npcs;


    //OVERLAYS
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private InfoOverlay overlay2d;
    @Inject
    public EntityOverlay overlay3d;
    @Inject
    WidgetOverlay widgetOverlay;
    @Inject
    NotificationOverlay notificationOverlay;
    List<CopilotOverlay> overlays = new ArrayList<>();


    //SCRIPTS
    @Inject
    FishingBarbarian fishingBarbarian;
    @Inject
    Woodcutting woodcutting;

    @Inject
    InventoryMakeScript crafting;

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
//        yewsWoodcuttingGuild.initialize(ids.BANK_CHEST_IDS, new int[]{ids.YEW_LOGS}, ids.YEW_TREE_IDS);
//        willowsDraynor.initialize(new int[]{ids.BANK_BOOTH}, new int[]{ids.WILLOW_LOGS}, ids.WILLOW_TREE_IDS);
        woodcutting.initialize();
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
    public void onGameStateChanged(GameStateChanged event) {
        gameObjects.onGameStateChanged(event);
        npcs.onGameStateChanged(event);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        gameObjects.onGameObjectSpawned(event);
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        gameObjects.onGameObjectDespawned(event);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        npcs.add(event.getNpc());
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        npcs.remove(event.getNpc());
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
            alert.playSound(id);
        } catch (Exception ignored) {
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        log.info("Config changed of " + event.getGroup() + ":::" + event);
        if (!event.getGroup().equals("copilot")) {
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
        if (!client.getGameState().equals(GameState.LOGGED_IN)) {
            return;
        }
        if (config.woodcutting()) {
            woodcutting.loop();
            runningScript = woodcutting;
        } else if (config.fishingBarbarian()) {
            fishingBarbarian.loop();
            runningScript = fishingBarbarian;
        }
        else if(config.crafting()){
            crafting.loop();
            runningScript = crafting;
        }
        else {
            setOverlaysEnabled(false);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        tracker.onMenuOptionClicked(event);
        woodcutting.onMenuOptionClicked(event);
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

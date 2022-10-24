package dev.jort.copilot;

import com.google.inject.Provides;
import dev.jort.copilot.helpers.*;
import dev.jort.copilot.overlays.*;
import dev.jort.copilot.priority_scripts.Loot;
import dev.jort.copilot.priority_scripts.SpecialAttack;
import dev.jort.copilot.scripts.*;
import dev.jort.copilot.other.Script;
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
import net.runelite.client.plugins.grounditems.GroundItemsPlugin;
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
        description = "Shows where to click next.",
        tags = {"jort", "copilot", "pilot", "autopilot", "overlay"}
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
    @Inject
    Widgets widgets;
    @Inject
    GroundObjects groundObjects;
    @Inject
    GroundItems groundItems;
    @Inject
    GiantsFoundryHelper giantsFoundryHelper;


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
    CopilotOverlayUtil overlayUtil;
    @Inject
    NotificationOverlay notificationOverlay;
    List<CopilotOverlay> overlays = new ArrayList<>();


    //PRIORITY SCRIPTS
    @Inject
    SpecialAttack specialAttack;
    @Inject
    Loot loot;


    //SCRIPTS
    @Inject
    FishingBarbarian fishingBarbarian;
    @Inject
    Woodcutting woodcutting;
    @Inject
    Crafting crafting;
    @Inject
    Inactivity inactivity;
    @Inject
    GiantsFoundry giantsFoundry;


    Script currentRunningScript = null;


    @Schedule(period = 1, unit = ChronoUnit.SECONDS)
    public void schedule() {
        if (!client.getGameState().equals(GameState.LOGGED_IN)) {
            return;
        }
        //thread which fires each second
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
    }

    @Override
    protected void shutDown() throws Exception {
        for (CopilotOverlay overlay : overlays) {
            overlayManager.remove((Overlay) overlay);
        }
        widgets.hideWidgets(false);
        log.info("Copilot stopped!");
    }


    //API SUBSCRIPTIONS (only work in this class)

    @Subscribe
    public void onGameTick(GameTick event) {
        tracker.onGameTick(event);
        if (!client.getGameState().equals(GameState.LOGGED_IN)) {
            return;
        }

        if (config.testScript()) {
            giantsFoundry.loop();
            currentRunningScript = giantsFoundry;
            overlayUtil.handleOverlays(currentRunningScript.getAction());
            return;
        }


        //only run normal scripts when priority scripts are done
        if (!handlePriorityScripts()) {
            handleRunningScripts();
        }

        if (currentRunningScript != null) {
            overlayUtil.handleOverlays(currentRunningScript.getAction());
        }
    }


    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        log.info("Game state changed to " + event.getGameState().name());
        gameObjects.onGameStateChanged(event);
        groundItems.onGameStateChanged(event);
        groundObjects.onGameStateChanged(event);
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
        if (!config.testSounds()) {
            return;
        }
        try {
            int id = Integer.parseInt(event.getMessage());
            log.info("Playing sound with ID " + id);
            alert.playSound(id);
        } catch (Exception ignored) {
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        log.info("Plugin config changed!");
        crafting.onConfigChanged(event);
        widgets.hideWidgets(config.hideWidgets());
        loot.onConfigChanged(event);

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

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        groundObjects.onGroundObjectSpawned(event);
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event) {
        groundObjects.onGroundObjectDespawned(event);
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        widgets.onScriptPostFired(event);
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned event) {
        groundItems.onItemSpawned(event);
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned event) {
        groundItems.onItemDespawned(event);
    }


    //SCRIPT HANDLERS

    /**
     * @return True if it needs to be called again.
     */
    public boolean handlePriorityScripts() {
        if (config.specialAttackAlert()) {
            if (specialAttack.needsToRun()) {
                specialAttack.loop();
                currentRunningScript = specialAttack;
                return true;
            }
        }

        if (config.lootAlert()) {
            if (loot.needsToRun()) {
                loot.loop();
                currentRunningScript = loot;
                return true;
            }
        }
        return false;
    }

    public void handleRunningScripts() {
        if (config.woodcutting()) {
            woodcutting.loop();
            currentRunningScript = woodcutting;
        } else if (config.fishingBarbarian()) {
            fishingBarbarian.loop();
            currentRunningScript = fishingBarbarian;
        } else if (config.crafting()) {
            crafting.loop();
            currentRunningScript = crafting;
        } else if (config.giantsFoundry()) {
            giantsFoundry.loop();
            currentRunningScript = giantsFoundry;
        } else if (config.inactivityAlert()) {
            inactivity.loop();
            currentRunningScript = inactivity;
        } else {
            setOverlaysEnabled(false);
        }
    }


    //OTHER

    @Provides
    CopilotConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CopilotConfig.class);
    }


    public Script getCurrentRunningScript() {
        return currentRunningScript;
    }

}

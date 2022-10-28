package dev.jort.copilot.other;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.helpers.*;
import dev.jort.copilot.overlays.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

import javax.inject.Inject;

/*
Class which all scripts extend to provide them all the helper utilities and a loop function etc.
 */
@Slf4j
public abstract class Script {

    @Inject
    public Client client;
    @Inject
    public CopilotConfig config;


    //UTILITIES
    @Inject
    public Alert alert;
    @Inject
    public Ids ids;
    @Inject
    public Tracker tracker;
    @Inject
    public GameObjects gameObjects;
    @Inject
    public Chat chat;
    @Inject
    public Inventory inventory;
    @Inject
    public Bank bank;
    @Inject
    public Widgets widgets;
    @Inject
    public Combat combat;
    @Inject
    GroundItems groundItems;


    //OVERLAYS
    @Inject
    public EntityOverlay entityOverlay;
    @Inject
    public NotificationOverlay notificationOverlay;
    @Inject
    public InfoOverlay infoOverlay;
    @Inject
    public WidgetOverlay widgetOverlay;
    @Inject
    public CopilotOverlayUtil overlayUtil;


    //ACTIONS
    public IdHolder waitAction = new IdHolder().setName("Wait");
    public IdHolder action = waitAction;

    public IdHolder getAction() {
        return action;
    }

    private boolean setupRan = false;
    private boolean running = true;
    private boolean exitRan = false;

    //call this function when running the script (each gametick for example)
    public int loop() {
        if (!running) {
            if (!exitRan) {
                onExit();
                exitRan = true;
                return Run.STOP;
            }
            log.warn("Calling loop in stopped script!");
            return Run.ERROR;
        }
        if (!setupRan) {
            onStart();
            setupRan = true;
            return Run.OK;
        }
        return onLoop();
    }

    public void onStart() {
    }

    public void onExit() {
    }

    public void setEnabled(boolean enable) {
        running = enable;
    }

    //dont call this function!
    public abstract int onLoop();

}

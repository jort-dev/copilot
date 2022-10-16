package dev.jort.copilot.other;

import dev.jort.copilot.helpers.*;
import dev.jort.copilot.overlays.EntityOverlay;
import dev.jort.copilot.overlays.InfoOverlay;
import dev.jort.copilot.overlays.NotificationOverlay;
import dev.jort.copilot.overlays.WidgetOverlay;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

import javax.inject.Inject;

@Slf4j
public abstract class Script {

    @Inject
    public Client client;


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


    //OVERLAYS
    @Inject
    public EntityOverlay entityOverlay;
    @Inject
    public NotificationOverlay notificationOverlay;
    @Inject
    public InfoOverlay infoOverlay;
    @Inject
    public WidgetOverlay widgetOverlay;


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
    public void loop() {
        if (!running) {
            if (!exitRan) {
                onExit();
                exitRan = true;
            }
            log.warn("Calling loop in stopped script!");
            return;
        }
        if (!setupRan) {
            onStart();
            setupRan = true;
            return;
        }
        onLoop();
    }

    public void onStart() {
    }

    public void onExit() {
    }

    public void setEnabled(boolean enable) {
        running = enable;
    }

    //dont call this function!
    public abstract void onLoop();

}

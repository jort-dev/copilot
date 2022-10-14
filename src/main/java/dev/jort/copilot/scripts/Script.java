package dev.jort.copilot.scripts;

import dev.jort.copilot.helpers.*;
import dev.jort.copilot.other.Action;
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
    Client client;


    //UTILITIES
    @Inject
    Alert alert;
    @Inject
    Ids ids;
    @Inject
    Tracker tracker;
    @Inject
    GameObjects gameObjects;
    @Inject
    Chat chat;
    @Inject
    Inventory inventory;
    @Inject
    Bank bank;


    //OVERLAYS
    @Inject
    EntityOverlay entityOverlay;
    @Inject
    NotificationOverlay notificationOverlay;
    @Inject
    InfoOverlay infoOverlay;
    @Inject
    WidgetOverlay widgetOverlay;


    //ACTIONS
    Action waitAction = new Action().setHint("Wait");
    Action action = waitAction;

    public Action getAction() {
        return action;
    }

    public abstract void loop();

}

package dev.jort.copilot.scripts;

import dev.jort.copilot.helpers.*;
import dev.jort.copilot.other.Action;
import dev.jort.copilot.overlays.GameObjectOverlay;
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

    @Inject
    NotificationOverlay notificationOverlay;

    @Inject
    Sound sound;

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
    GameObjectOverlay overlay3D;

    @Inject
    InfoOverlay overlay2D;

    @Inject
    WidgetOverlay widgetOverlay;

    @Inject
    Bank bank;

    Action waitAction = new Action().setHint("Wait");

    Action action = waitAction;

    public Action getAction() {
        return action;
    }

    public abstract void loop();

}

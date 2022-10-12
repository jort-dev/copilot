package dev.jort.copilot.scripts;

import dev.jort.copilot.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

import javax.inject.Inject;

@Slf4j
public abstract class Script {

    @Inject
    Client client;

    @Inject
    Tracker tracker;

    @Inject
    GameObjects gameObjects;

    @Inject
    Chat chat;

    @Inject
    Inventory inventory;

    @Inject
    CopilotOverlay3D overlay3D;

    @Inject
    CopilotOverlay2D overlay2D;

    @Inject
    CopilotWidgetOverlay widgetOverlay;

    @Inject
    Bank bank;

    abstract void loop();

}

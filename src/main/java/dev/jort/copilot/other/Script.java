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
    public IdHolder waitIdHolder = new IdHolder().setName("Wait");
    public IdHolder idHolder = waitIdHolder;

    public IdHolder getAction() {
        return idHolder;
    }

    public abstract void loop();

}

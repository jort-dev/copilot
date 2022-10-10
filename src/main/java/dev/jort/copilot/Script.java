package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

import javax.inject.Inject;

@Slf4j
public abstract class Script {

    private String hint = "Initializing";

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
    Widgets widgets;

    @Inject
    Bank bank;

    abstract void loop();

    public void setHint(String hint){
        if (this.hint.equals(hint)){
            return;
        }
        log.info("Set hint to " + hint);
        this.hint = hint;
    }

    public String getHint(){
        return hint;
    }

}

package dev.jort.copilot.priority_scripts;

import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.other.PriorityScript;
import dev.jort.copilot.other.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class Kitten extends PriorityScript {

    boolean kittenMessageReceived = false;

    String msg1 = "Your kitten is hungry.";
    String msg2 = "Your kitten is very hungry.";
    String msg3 = "Your kitten wants attention.";
    String msg4 = "Your kitten really wants attention.";

    String[] msgs = new String[0];


    @Override
    public void onStart() {
        msgs = new String[]{msg1, msg2, msg3, msg4};
    }

    public boolean needsToRun() {
        //hack because onstart does not get fired
        if (!config.kittenAlert()) {
            return false;
        }
        if (!kittenMessageReceived) {
            return false;
        }
        return true;
    }

    @Override
    public int onLoop() {
        if (!needsToRun()) {
            action = waitAction;
            return Run.DONE;
        }
        action = new IdHolder()
                .setName("Interact with kitten");
        alert.handleAlert(true);
        return Run.AGAIN;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!event.getType().equals(ChatMessageType.GAMEMESSAGE)) {
            return;
        }
        if (!Util.containsAny(event.getMessage(), msgs)) {
            return;
        }
        kittenMessageReceived = true;
    }

    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (!event.getMenuTarget().contains("Kitten")) {
            return;
        }
        if (!event.getMenuOption().equals("Interact")) {
            return;
        }
        kittenMessageReceived = false;
    }

}


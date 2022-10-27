package dev.jort.copilot.priority_scripts;

import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.other.PriorityScript;
import dev.jort.copilot.other.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Singleton;

/*
GAMEMESSAGE: <col=ef1020>Your kitten is hungry.</col>, null, ChatMessage(messageNode=bk@4d035f00, type=GAMEMESSAGE, name=, message=<col=ef1020>Your kitten is hungry.</col>, sender=null, timestamp=1666903636)
AMEMESSAGE: <col=ef1020>Your kitten wants attention.</col>, null, ChatMessage(messageNode=bk@344da56c, type=GAMEMESSAGE, name=, message=<col=ef1020>Your kitten wants attention.</col>, sender=null, timestamp=1666903727)
AMEMESSAGE: <col=ef1020>Your kitten is very hungry.</col>, null, ChatMessage(messageNode=bk@246c06f3, type=GAMEMESSAGE, name=, message=<col=ef1020>Your kitten is very hungry.</col>, sender=null, timestamp=1666903816)
AMEMESSAGE: <col=ef1020>Your kitten really wants attention.</col>, null, ChatMessage(messageNode=bk@457b586f, type=GAMEMESSAGE, name=, message=<col=ef1020>Your kitten really wants attention.</col>, sender=null, timestamp=1666904087)

 Menu item clicked: , id=47882, itemid=-1, widgetid=-1, target=<col=ffff00>Kitten, option=Interact
 */
@Singleton
@Slf4j
public class Kitten extends PriorityScript {

    boolean kittenMessageReceived = false;

    String msg1 = "Your kitten is hungry.";
    String msg2 = "Your kitten is very hungry.";
    String msg3 = "Your kitten wants attention.";
    String msg4 = "Your kitten really wants attention.";
    String msg5 = "kitten";

    String[] msgs = new String[0];


    @Override
    public boolean needsToRun() {
        //hack because onstart does not get fired
        if (msgs.length == 0) {
            msgs = Util.toArray(msg1, msg2, msg3, msg4, msg5);
        }
        if (!config.kittenAlert()) {
            return false;
        }
        if (!kittenMessageReceived) {
            return false;
        }
        return true;
    }

    @Override
    public void onLoop() {
        action = new IdHolder()
                .setName("Interact with kitten");
        alert.handleAlert(true);
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


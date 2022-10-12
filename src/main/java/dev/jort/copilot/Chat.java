package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class Chat {

    @Inject
    Client client;

    public void send(String message) {
        log.info("Sending chat message: " + message);
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", Util.colorString("Copilot: ", "0000ff") + message, null);
    }
}

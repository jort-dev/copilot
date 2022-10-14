package dev.jort.copilot.helpers;

import dev.jort.copilot.other.Util;
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

    public void sendStartupMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Util.colorString("Jort's ", "ff00f"));
        stringBuilder.append(Util.colorString("Copilot ", "0000ff"));
        stringBuilder.append(Util.colorString("has ", "ffff00"));
        stringBuilder.append(Util.colorString("started!", "ff0000"));
        send(stringBuilder.toString());
    }
}

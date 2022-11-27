package dev.jort.copilot.helpers;

import net.runelite.api.Client;
import net.runelite.api.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class Players {
    @Inject
    Client client;

    public List<Player> getPlayers() {
        return client.getPlayers();
    }

    public Player me() {
        return client.getLocalPlayer();
    }
}


package dev.jort.copilot.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Singleton
@Slf4j
public class Npcs {
    @Inject
    Client client;
    private final List<NPC> npcs = new ArrayList<>();

    public void add(NPC npc) {
        if (npcs.contains(npc)) {
            return;
        }
        npcs.add(npc);
    }

    public void remove(NPC npc) {
        npcs.remove(npc);
    }

    public NPC closest(int... ids) {
        if (npcs.isEmpty()) {
            return null;
        }
        NPC closest = null;
        WorldPoint myLocation = client.getLocalPlayer().getWorldLocation();
        for (NPC npc : npcs) {
            for (int id : ids) {
                if (id != npc.getId()) {
                    continue;
                }
                if (closest == null) {
                    closest = npc;
                    continue;
                }

                double distance = myLocation.distanceTo(npc.getWorldLocation());
                if (distance < myLocation.distanceTo(closest.getWorldLocation())) {
                    closest = npc;
                }
            }
        }
        return closest;
    }

    public List<NPC> filter(Predicate<NPC> p) {
        List<NPC> result = new ArrayList<>();
        for (NPC npc : npcs) {
            if (npc == null) {
                continue;
            }
            if (!p.test(npc)) {
                continue;
            }
            result.add(npc);
        }
        return result;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        add(event.getNpc());
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        remove(event.getNpc());
    }

    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if(gameStateChanged.getGameState().equals(GameState.LOADING)){
            npcs.clear();
        }
    }
}

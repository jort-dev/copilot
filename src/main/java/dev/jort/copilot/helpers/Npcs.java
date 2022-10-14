package dev.jort.copilot.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

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
}

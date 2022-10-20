package dev.jort.copilot.helpers;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Singleton
public class GroundObjects {

    @Inject
    Client client;

    private final ArrayList<GroundObject> groundObjects = new ArrayList<>();

    public void add(GroundObject groundObject) {
        if (groundObjects.contains(groundObject)) {
            return;
        }

        String[] actions = client.getObjectDefinition(groundObject.getId()).getActions();


        for (String action : actions) {
            if (action != null) {
                //dont add gameobject which are not interactable, to prevent fake bank booths for example
                groundObjects.add(groundObject);
                return;
            }
        }
    }

    public void remove(GroundObject groundObject) {
        groundObjects.remove(groundObject);
    }

    public GroundObject closest(int... ids) {
        if (groundObjects.isEmpty()) {
            return null;
        }
        if (ids == null) { // seems to happen at startup in render
            return null;
        }
        GroundObject closest = null;
        WorldPoint myLocation = client.getLocalPlayer().getWorldLocation();
        for (GroundObject groundObject : groundObjects) {
            for (int id : ids) {
                if (id != groundObject.getId()) {
                    continue;
                }
                if (closest == null) {
                    closest = groundObject;
                    continue;
                }

                double distance = myLocation.distanceTo(groundObject.getWorldLocation());
                if (distance < myLocation.distanceTo(closest.getWorldLocation())) {
                    closest = groundObject;
                }
            }
        }
        return closest;
    }

    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        add(event.getGroundObject());
    }

    public void onGroundObjectDespawned(GroundObjectDespawned event) {
        remove(event.getGroundObject());
    }

    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState().equals(GameState.LOADING)) {
            groundObjects.clear();
        }
    }

    public List<GroundObject> filter(Predicate<GroundObject> p) {
        List<GroundObject> result = new ArrayList<>();
        for (GroundObject groundObject : groundObjects) {
            if (groundObject == null) {
                continue;
            }
            if (!p.test(groundObject)) {
                continue;
            }
            result.add(groundObject);
        }
        return result;
    }


}

package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;

@Singleton
@Slf4j
public class GameObjects {

    @Inject
    Client client;

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();

    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public void remove(GameObject gameObject) {
        gameObjects.remove(gameObject);
    }

    public GameObject closest(int... ids) {
        if (gameObjects.isEmpty()) {
            return null;
        }
        GameObject closest = null;
        WorldPoint myLocation = client.getLocalPlayer().getWorldLocation();
        for (GameObject gameObject : gameObjects) {
            for (int id : ids) {
                if (gameObject.getId() == id) {
                    double distance = myLocation.distanceTo(gameObject.getWorldLocation());
                    if (closest == null) {
                        closest = gameObject;
                        continue;
                    }
                    if (distance < myLocation.distanceTo(closest.getWorldLocation())) {
                        closest = gameObject;
                    }
                }
            }
        }
        return closest;
    }
}

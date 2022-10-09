package dev.jort.copilot;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class GameObjects {

    @Inject
    Client client;

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();

    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public void remove(GameObject gameObject){
        gameObjects.remove(gameObject);
    }

    public GameObject closest(int id) {
        if (gameObjects.isEmpty()) {
            return null;
        }
        GameObject closest = gameObjects.get(0);
        WorldPoint myLocation = client.getLocalPlayer().getWorldLocation();
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getId() != id) {
                continue;
            }
            double distance = myLocation.distanceTo(gameObject.getWorldLocation());
            if (distance < myLocation.distanceTo(closest.getWorldLocation())) {
                closest = gameObject;
            }
        }
        return closest;
    }


}

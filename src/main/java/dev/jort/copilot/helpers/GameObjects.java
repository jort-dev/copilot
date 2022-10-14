package dev.jort.copilot.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Singleton
@Slf4j
public class GameObjects {

    @Inject
    Client client;

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();

    public void add(GameObject gameObject) {
        if (gameObjects.contains(gameObject)) {
            return;
        }
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
                if (id != gameObject.getId()) {
                    continue;
                }
                if (closest == null) {
                    closest = gameObject;
                    continue;
                }

                double distance = myLocation.distanceTo(gameObject.getWorldLocation());
                if (distance < myLocation.distanceTo(closest.getWorldLocation())) {
                    closest = gameObject;
                }
            }
        }
        return closest;
    }

    public void onGameObjectSpawned(GameObjectSpawned event) {
        add(event.getGameObject());
    }

    public void onGameObjectDespawned(GameObjectDespawned event) {
        remove(event.getGameObject());
    }

    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if(gameStateChanged.getGameState().equals(GameState.LOADING)){
            gameObjects.clear();
        }
    }

    public List<GameObject> filter(Predicate<GameObject> p) {
        List<GameObject> result = new ArrayList<>();
        for (GameObject gameObject : gameObjects) {
            if (gameObject == null) {
                continue;
            }
            if (!p.test(gameObject)) {
                continue;
            }
            result.add(gameObject);
        }
        return result;
    }
}
